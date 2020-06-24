package com.prokarma.oneclick.runner

import com.prokarma.oneclick.modules.bo.TerraformImage
import com.prokarma.oneclick.runner.config.DockerConfig
import com.prokarma.oneclick.settings.bo.Settings
import com.prokarma.oneclick.stacks.bo.Job
import com.prokarma.oneclick.stacks.workflow.JobWorkflow
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [com.prokarma.oneclick.runner.DockerRunner::class, com.prokarma.oneclick.runner.config.DockerConfig::class, com.prokarma.oneclick.settings.bo.Settings::class, com.prokarma.oneclick.runner.HttpHijackWorkaround::class])
@EnableConfigurationProperties
@TestPropertySource(properties = ["oneclick.dockerDaemonUrl=unix:///var/run/docker.sock"])
class DockerRunnerIT {

    @Autowired
    private lateinit var dockerRunner: com.prokarma.oneclick.runner.DockerRunner

    @Test
    fun `runContainerForJob() should work with a simple script`() {
        val script = "echo 'Hello World'"

        val job = com.prokarma.oneclick.stacks.bo.Job()
        job.terraformImage = TerraformImage.defaultInstance()
        val jobWorkflow = com.prokarma.oneclick.stacks.workflow.JobWorkflow(job)

        Assert.assertEquals(0, dockerRunner.runContainerForJob(jobWorkflow, script).toLong())
    }

    @Test
    fun `runContainerForJob() should return the script exit code`() {
        val script = "exit 5"

        val job = com.prokarma.oneclick.stacks.bo.Job()
        job.terraformImage = TerraformImage.defaultInstance()
        val jobWorkflow = com.prokarma.oneclick.stacks.workflow.JobWorkflow(job)

        Assert.assertEquals(5, dockerRunner.runContainerForJob(jobWorkflow, script).toLong())
    }
}