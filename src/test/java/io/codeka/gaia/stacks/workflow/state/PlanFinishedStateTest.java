package io.codeka.gaia.stacks.workflow.state;

import io.codeka.gaia.stacks.bo.*;
import io.codeka.gaia.stacks.workflow.JobWorkflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanFinishedStateTest {

    @Mock
    JobWorkflow jobWorkflow;

    private Job job;

    private PlanFinishedState state;

    @BeforeEach
    void setup() {
        job = new Job();
        job.setId("test_jobId");
        job.getSteps().add(new Step());
        lenient().when(jobWorkflow.getJob()).thenReturn(job); // use lenient to prevent mockito from throwing exception for tests not needing this mock

        state = new PlanFinishedState();
    }

    @Test
    void plan_shouldNotBePossible() {
        assertThrows(UnsupportedOperationException.class, () -> state.plan(jobWorkflow));
    }

    @Test
    void apply_shouldStartTheJob() {
        // when
        state.apply(jobWorkflow);

        // then
        assertEquals(JobStatus.APPLY_STARTED, job.getStatus());
    }

    @Test
    void apply_shouldStartAnApplyStep() {
        // when
        state.apply(jobWorkflow);

        // then
        assertThat(job.getSteps()).isNotEmpty().hasSize(2);
        var step = job.getSteps().get(1);
        assertNotNull(step.getId());
        assertEquals("test_jobId", step.getJobId());
        assertEquals(StepType.APPLY, step.getType());
        assertEquals(StepStatus.STARTED, step.getStatus());
        assertThat(step.getStartDateTime()).isNotNull().isEqualToIgnoringSeconds(LocalDateTime.now());
    }

    @Test
    void apply_shouldUpdateWorkflow() {
        // when
        state.apply(jobWorkflow);

        // then
        var step = job.getSteps().get(1);
        verify(jobWorkflow).setCurrentStep(step);
        verify(jobWorkflow).setState(any(ApplyStartedState.class));
    }

    @Test
    void end_shouldNotBePossible() {
        assertThrows(UnsupportedOperationException.class, () -> state.end(jobWorkflow));
    }

    @Test
    void fail_shouldNotBePossible() {
        assertThrows(UnsupportedOperationException.class, () -> state.fail(jobWorkflow));
    }

    @Test
    void retry_shouldNotBePossible() {
        assertThrows(UnsupportedOperationException.class, () -> state.retry(jobWorkflow));
    }
}
