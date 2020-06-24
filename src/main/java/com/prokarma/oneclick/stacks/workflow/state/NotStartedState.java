package com.prokarma.oneclick.stacks.workflow.state;

import com.prokarma.oneclick.stacks.bo.Step;
import com.prokarma.oneclick.stacks.bo.StepType;
import com.prokarma.oneclick.stacks.workflow.JobWorkflow;

/**
 * Describes a new job before any action
 */
public class NotStartedState implements JobState {
    @Override
    public void plan(JobWorkflow jobWorkflow) {
        var job = jobWorkflow.getJob();
        job.start();

        var step = new Step(StepType.PLAN, job.getId());
        job.getSteps().add(step);
        jobWorkflow.setCurrentStep(step);
        step.start();

        jobWorkflow.setState(new PlanStartedState());
    }
}
