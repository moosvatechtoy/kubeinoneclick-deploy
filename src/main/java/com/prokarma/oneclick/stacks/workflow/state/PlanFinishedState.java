package com.prokarma.oneclick.stacks.workflow.state;

import com.prokarma.oneclick.stacks.bo.JobStatus;
import com.prokarma.oneclick.stacks.bo.Step;
import com.prokarma.oneclick.stacks.bo.StepType;
import com.prokarma.oneclick.stacks.workflow.JobWorkflow;

/**
 * Describes a job which plan has been finished
 */
public class PlanFinishedState implements JobState {
    @Override
    public void apply(JobWorkflow jobWorkflow) {
        var job = jobWorkflow.getJob();
        job.proceed(JobStatus.APPLY_STARTED);

        var step = new Step(StepType.APPLY, job.getId());
        job.getSteps().add(step);
        jobWorkflow.setCurrentStep(step);
        step.start();

        jobWorkflow.setState(new ApplyStartedState());
    }
}
