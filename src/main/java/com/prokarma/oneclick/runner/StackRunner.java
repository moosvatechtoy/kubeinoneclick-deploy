package com.prokarma.oneclick.runner;

import com.prokarma.oneclick.modules.bo.TerraformModule;
import com.prokarma.oneclick.stacks.bo.Job;
import com.prokarma.oneclick.stacks.bo.JobType;
import com.prokarma.oneclick.stacks.bo.Stack;
import com.prokarma.oneclick.stacks.bo.StackState;
import com.prokarma.oneclick.stacks.repository.JobRepository;
import com.prokarma.oneclick.stacks.repository.StackRepository;
import com.prokarma.oneclick.stacks.repository.StepRepository;
import com.prokarma.oneclick.stacks.workflow.JobWorkflow;
import com.prokarma.oneclick.modules.bo.TerraformModule;
import com.prokarma.oneclick.stacks.bo.Job;
import com.prokarma.oneclick.stacks.bo.JobType;
import com.prokarma.oneclick.stacks.bo.Stack;
import com.prokarma.oneclick.stacks.bo.StackState;
import com.prokarma.oneclick.stacks.repository.JobRepository;
import com.prokarma.oneclick.stacks.repository.StackRepository;
import com.prokarma.oneclick.stacks.repository.StepRepository;
import com.prokarma.oneclick.stacks.workflow.JobWorkflow;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * Runs a module instance
 */
@Service
public class StackRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StackRunner.class);

    private DockerRunner dockerRunner;
    private StackCommandBuilder stackCommandBuilder;
    private StackRepository stackRepository;
    private JobRepository jobRepository;
    private StepRepository stepRepository;

    private Map<String, Job> jobs = new HashMap<>();
    private static final String[] TERRAFORM_COMMANDS =  new String[] {"terraform init", "terraform version", "terraform plan",
            "terraform apply", "terraform destroy"};

    @Value("${terraform.exe.path}")
    private String terraformLocalExecPath;

    @Autowired
    public StackRunner(DockerRunner dockerRunner, StackCommandBuilder stackCommandBuilder,
                       StackRepository stackRepository, JobRepository jobRepository, StepRepository stepRepository) {
        this.dockerRunner = dockerRunner;
        this.stackCommandBuilder = stackCommandBuilder;
        this.stackRepository = stackRepository;
        this.jobRepository = jobRepository;
        this.stepRepository = stepRepository;
    }

    private String managePlanScript(Job job, Stack stack, TerraformModule module) {
        if (JobType.RUN == job.getType()) {
            return stackCommandBuilder.buildPlanScript(job, stack, module);
        }
        return stackCommandBuilder.buildPlanDestroyScript(job, stack, module);
    }

    private void managePlanResult(Integer result, JobWorkflow jobWorkflow, Stack stack) {
        if (result == 0) {
            // diff is empty
            jobWorkflow.end();
        } else if (result == 2) {
            // there is a diff, set the status of the stack to : "TO_UPDATE"
            if (StackState.NEW != stack.getState() && jobWorkflow.getJob().getType() != JobType.DESTROY) {
                stack.setState(StackState.TO_UPDATE);
                stackRepository.save(stack);
            }
            jobWorkflow.end();
        } else {
            // error
            jobWorkflow.fail();
        }
    }

    private String manageApplyScript(Job job, Stack stack, TerraformModule module) {
        if (JobType.RUN == job.getType()) {
            return stackCommandBuilder.buildApplyScript(job, stack, module);
        }
        return stackCommandBuilder.buildDestroyScript(job, stack, module);
    }

    private void manageApplyResult(Integer result, JobWorkflow jobWorkflow, Stack stack) {
        if (result == 0) {
            jobWorkflow.end();

            // update stack information
            stack.setState(jobWorkflow.getJob().getType() == JobType.RUN ? StackState.RUNNING : StackState.STOPPED);
            stackRepository.save(stack);
        } else {
            jobWorkflow.fail();
        }
    }

    /**
     * @param jobWorkflow
     * @param jobActionFn function applying o the job
     * @param scriptFn    function allowing to get the right script to execute
     * @param resultFn    function treating the result ot the executed script
     */
    private void treatJob(JobWorkflow jobWorkflow,TerraformModule module, Consumer<JobWorkflow> jobActionFn,
                          Supplier<String> scriptFn, IntConsumer resultFn) {
        // execute the workflow of the job
        jobActionFn.accept(jobWorkflow);

        var job = jobWorkflow.getJob();
        this.jobs.put(job.getId(), job);
        stepRepository.saveAll(job.getSteps());
        jobRepository.save(job);

        // get the wanted script
        var script = scriptFn.get();
        LOG.debug("Running Stack Script===>>>" + script);
        int result = 0;
        if (module.isRemoteRun()) {
            result = this.dockerRunner.runContainerForJob(jobWorkflow, script);
        } else {
            result = runLocalForJob(jobWorkflow, script, module);
        }

        // manage the result of the execution of the script
        resultFn.accept(result);

        // save job to database
        stepRepository.saveAll(job.getSteps());
        jobRepository.save(job);
        this.jobs.remove(job.getId());
    }

    @Async
    public void plan(JobWorkflow jobWorkflow, TerraformModule module, Stack stack) {
        treatJob(
            jobWorkflow, module,
            JobWorkflow::plan,
            () -> managePlanScript(jobWorkflow.getJob(), stack, module),
            result -> managePlanResult(result, jobWorkflow, stack)
        );
    }

    @Async
    public void apply(JobWorkflow jobWorkflow, TerraformModule module, Stack stack) {
        treatJob(
            jobWorkflow, module,
            JobWorkflow::apply,
            () -> manageApplyScript(jobWorkflow.getJob(), stack, module),
            result -> manageApplyResult(result, jobWorkflow, stack)
        );
    }

    @Async
    public void retry(JobWorkflow jobWorkflow, TerraformModule module, Stack stack) {
        stepRepository.deleteByJobId(jobWorkflow.getJob().getId());
        treatJob(
            jobWorkflow, module,
            JobWorkflow::retry,
            () -> managePlanScript(jobWorkflow.getJob(), stack, module),
            result -> managePlanResult(result, jobWorkflow, stack)
        );
    }

    public Optional<Job> getJob(String jobId) {
        return Optional.ofNullable(this.jobs.get(jobId));
    }

    public int runLocalForJob(JobWorkflow jobWorkflow, String script, TerraformModule module) {
        try {
            String scriptLocal = replaceTerraformWithLocalPath(script, module);
            var step = jobWorkflow.getCurrentStep();
            final ProcessBuilder builder = new ProcessBuilder("sh", "-c", scriptLocal);
            final Process process = builder.start();
            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8").newDecoder()));
            final BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                LOG.debug(s);
                step.getLogsWriter().append(s + '\n');
            }
            while ((s = errorInput.readLine()) != null) {
                LOG.debug(s);
                step.getLogsWriter().append(s + '\n');
            }
            return 0;
        } catch (final IOException e) {
            LOG.error("Exception when running job", e);
            return 99;
        }
    }

    private String replaceTerraformWithLocalPath(String script, TerraformModule module) {
        String terraformLocalExecPathLocal = StringUtils.isNotEmpty(module.getTerraformPath()) ? module.getTerraformPath() : terraformLocalExecPath;
        StringBuilder scriptLocal = new StringBuilder(script);
        for(String command : TERRAFORM_COMMANDS) {
            int startIndex = scriptLocal.indexOf(command);
            if (startIndex == -1) {
                continue;
            }
            int endIndex = startIndex + command.length();
            scriptLocal.replace(startIndex, endIndex, terraformLocalExecPathLocal.concat(command));
        }
        return scriptLocal.toString();
    }

}
