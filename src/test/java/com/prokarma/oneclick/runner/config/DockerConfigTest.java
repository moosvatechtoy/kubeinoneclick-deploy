package com.prokarma.oneclick.runner.config;


import com.spotify.docker.client.exceptions.DockerException;
import com.prokarma.oneclick.settings.bo.Settings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DockerConfigTest {

    @Test
    void dockerClient_shouldUseDockerDaemonUrlSetting() throws DockerException, InterruptedException {
        var settings = new Settings();
        settings.setDockerDaemonUrl("http://test:2375");

        var dockerConfig = new DockerConfig();

        var dockerClient = dockerConfig.client(settings);

        assertEquals("test", dockerClient.getHost());
    }

}