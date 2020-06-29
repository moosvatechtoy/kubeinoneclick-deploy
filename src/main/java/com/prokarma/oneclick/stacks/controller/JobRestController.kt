package com.prokarma.oneclick.stacks.controller

import com.prokarma.oneclick.modules.repository.TerraformModuleRepository
import com.prokarma.oneclick.runner.StackRunner
import com.prokarma.oneclick.stacks.bo.Job
import com.prokarma.oneclick.stacks.repository.JobRepository
import com.prokarma.oneclick.stacks.repository.StackRepository
import com.prokarma.oneclick.stacks.repository.StepRepository
import com.prokarma.oneclick.stacks.workflow.JobWorkflow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/jobs")
class JobRestController(
        private val jobRepository: JobRepository,
        private val stackRepository: StackRepository,
        private val moduleRepository: TerraformModuleRepository,
        private val stackRunner: com.prokarma.oneclick.runner.StackRunner,
        private val stepRepository: com.prokarma.oneclick.stacks.repository.StepRepository) {

    @GetMapping(params = ["stackId"])
    fun jobs(@RequestParam stackId: String) = jobRepository.findAllByStackIdOrderByStartDateTimeDesc(stackId)

    @GetMapping("/{id}")
    fun job(@PathVariable id: String): com.prokarma.oneclick.stacks.bo.Job {
        val runningJob = stackRunner.getJob(id);
        if (runningJob.isPresent) {
            var job = runningJob.get();
            if (null == job.endDateTime) {
                job.endDateTime = LocalDateTime.now();
            }
            return runningJob.get();
        }
        var job = jobRepository.findById(id).orElseThrow { JobNotFoundException() };
        if (null == job.endDateTime) {
            job.endDateTime = LocalDateTime.now();
        }
        return job
    }

    @PostMapping("/{id}/plan")
    fun plan(@PathVariable id: String) {
        val job = jobRepository.findById(id).orElseThrow { JobNotFoundException() }
        val stack = stackRepository.findById(job.stackId).orElseThrow()
        val module = moduleRepository.findById(stack.moduleId).orElseThrow()
        stackRunner.plan(com.prokarma.oneclick.stacks.workflow.JobWorkflow(job), module, stack)
    }

    @PostMapping("/{id}/apply")
    fun apply(@PathVariable id: String) {
        val job = jobRepository.findById(id).orElseThrow { JobNotFoundException() }
        val stack = stackRepository.findById(job.stackId).orElseThrow()
        val module = moduleRepository.findById(stack.moduleId).orElseThrow()
        stackRunner.apply(com.prokarma.oneclick.stacks.workflow.JobWorkflow(job), module, stack)
    }

    @PostMapping("/{id}/retry")
    fun retry(@PathVariable id: String) {
        val job = jobRepository.findById(id).orElseThrow { JobNotFoundException() }
        val stack = stackRepository.findById(job.stackId).orElseThrow()
        val module = moduleRepository.findById(stack.moduleId).orElseThrow()
        stackRunner.retry(com.prokarma.oneclick.stacks.workflow.JobWorkflow(job), module, stack)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        stepRepository.deleteByJobId(id)
        jobRepository.deleteById(id)
    }

}

@ResponseStatus(HttpStatus.NOT_FOUND)
internal class JobNotFoundException : RuntimeException()
