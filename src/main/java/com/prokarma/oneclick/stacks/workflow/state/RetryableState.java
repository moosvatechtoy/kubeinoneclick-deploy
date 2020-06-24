package com.prokarma.oneclick.stacks.workflow.state;

import com.prokarma.oneclick.stacks.workflow.JobWorkflow;

interface RetryableState extends JobState {
    @Override
    default void retry(JobWorkflow jobWorkflow) {
        jobWorkflow.getJob().reset();
        jobWorkflow.setState(new NotStartedState());
        jobWorkflow.plan();
    }
}
