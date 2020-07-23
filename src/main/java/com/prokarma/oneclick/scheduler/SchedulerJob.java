package com.prokarma.oneclick.scheduler;

import com.prokarma.oneclick.constants.OneClickConstantsI;
import com.prokarma.oneclick.modules.repository.TerraformModuleRepository;
import com.prokarma.oneclick.runner.StackRunner;
import com.prokarma.oneclick.stacks.bo.JobType;
import com.prokarma.oneclick.stacks.bo.Stack;
import com.prokarma.oneclick.stacks.bo.StackState;
import com.prokarma.oneclick.stacks.repository.JobRepository;
import com.prokarma.oneclick.stacks.repository.StackRepository;
import com.prokarma.oneclick.stacks.service.StackScheduleTimeCalculateUtil;
import com.prokarma.oneclick.stacks.workflow.JobWorkflow;
import com.prokarma.oneclick.teams.Team;
import com.prokarma.oneclick.teams.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SchedulerJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerJob.class);

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private TerraformModuleRepository terraformModuleRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private StackRunner stackRunner;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LOG.warn("Scheduler Job process started..");
        List<Stack> stackList = stackRepository.findAll();
        stackList.forEach(stack -> {
            CompletableFuture.runAsync(() -> {
                try {
                    processScheduling(stack);
                } catch (ParseException e) {
                    LOG.error("Scheduler Job process failed for stack==>" + stack.getId(), e);
                    e.printStackTrace();
                }
            });
        });
        LOG.warn("Scheduler Job process completed..!!");
    }

    private void processScheduling(Stack stack) throws ParseException {
        if(stack.isEnableTTL() && !stack.isSchedulingTriggered()) {
            stack.setSchedulingTriggered(true);
            stackRepository.save(stack);
            LocalDateTime  timeNow = LocalDateTime.ofInstant(new Date().toInstant(),
                    ZoneId.of("UTC", ZoneId.SHORT_IDS));
            if (stack.isDeploySchedule() && stack.isDestroySchedule()) {
                if(stack.getNextDeployScheduleTime().isBefore(stack.getNextDestroyScheduleTime()) &&
                        stack.getNextDeployScheduleTime().isBefore(timeNow)) {
                    deployStack(stack, JobType.DESTROY);
                    deployStack(stack, JobType.RUN);
                } else if (stack.getNextDestroyScheduleTime().isBefore(timeNow)) {
                    deployStack(stack, JobType.DESTROY);
                }
                StackScheduleTimeCalculateUtil.calculateScheduleTime(stack);
            } else if(stack.isDeploySchedule() && stack.getNextDeployScheduleTime().isBefore(timeNow)) {
                deployStack(stack, JobType.DESTROY);
                deployStack(stack, JobType.RUN);
                StackScheduleTimeCalculateUtil.calculateScheduleTime(stack);
            } else if ((stack.isDestroySchedule() || (null != stack.getDestroyType() && !OneClickConstantsI.DESTROY_NEVER.equalsIgnoreCase(stack.getDestroyAfterHours())))
                    && stack.getNextDestroyScheduleTime().isBefore(LocalDateTime.now())) {
                deployStack(stack, JobType.DESTROY);
                StackScheduleTimeCalculateUtil.calculateScheduleTime(stack);
            }
            stack.setSchedulingTriggered(false);
            stackRepository.save(stack);
        }
    }

    private void deployStack(Stack stack, JobType jobType) {
        if(jobType == JobType.DESTROY && (stack.getState() != StackState.RUNNING || stack.getState() != StackState.FAILED)) {
            return;
        }
        LOG.warn("Deploying/Destroying Stack:", stack.getId());
        var module = this.terraformModuleRepository.findById(stack.getModuleId()).orElseThrow();
        var job = new com.prokarma.oneclick.stacks.bo.Job(jobType, stack.getId(), stack.getUpdatedBy());
        job.setTerraformImage(module.getTerraformImage());
        jobRepository.save(job);
        LOG.warn("Created Job:", job.getId());
        LOG.warn("Plan Started for job =>", job.getId());
        stackRunner.plan(new JobWorkflow(job), module, stack);
        LOG.warn("Plan Completed for job =>", job.getId());
        LOG.warn("Apply Started for job =>", job.getId());
        stackRunner.apply(new JobWorkflow(job), module, stack);
        LOG.warn("Apply Completed for job =>", job.getId());
    }
}
