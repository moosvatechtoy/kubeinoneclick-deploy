package com.prokarma.oneclick.registries.config;

import com.prokarma.oneclick.registries.RegistryOAuth2Provider;
import com.prokarma.oneclick.registries.RegistryRawContent;
import com.prokarma.oneclick.registries.github.GitHubOAuth2Provider;
import com.prokarma.oneclick.registries.github.GitHubRawContent;
import com.prokarma.oneclick.registries.github.GithubRegistryApi;
import com.prokarma.oneclick.registries.gitlab.GitLabOAuth2Provider;
import com.prokarma.oneclick.registries.gitlab.GitLabRawContent;
import com.prokarma.oneclick.registries.gitlab.GitlabRegistryApi;
import com.prokarma.oneclick.config.BeansConfig;
import com.prokarma.oneclick.registries.RegistryOAuth2Provider;
import com.prokarma.oneclick.registries.RegistryRawContent;
import com.prokarma.oneclick.registries.github.GitHubOAuth2Provider;
import com.prokarma.oneclick.registries.github.GitHubRawContent;
import com.prokarma.oneclick.registries.github.GithubRegistryApi;
import com.prokarma.oneclick.registries.gitlab.GitLabOAuth2Provider;
import com.prokarma.oneclick.registries.gitlab.GitLabRawContent;
import com.prokarma.oneclick.registries.gitlab.GitlabRegistryApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RegistryConfig.class, BeansConfig.class, GitHubRawContent.class, GitLabRawContent.class, GithubRegistryApi.class, GitlabRegistryApi.class})
class RegistryConfigIT {
    @Test
    void registryRawContents_shouldBeInstantiated(@Autowired(required = false) List<RegistryRawContent> registryRawContents) {
        assertThat(registryRawContents).isNotNull()
                .hasSize(2)
                .extracting("class")
                .contains(GitHubRawContent.class, GitLabRawContent.class);
    }

    @Test
    void registryOAuth2Providers_shouldBeInstantiated(@Autowired(required = false) List<RegistryOAuth2Provider> registryOAuth2Providers) {
        assertThat(registryOAuth2Providers).isNotNull()
                .hasSize(2)
                .extracting("class")
                .contains(GitHubOAuth2Provider.class, GitLabOAuth2Provider.class);
    }
}
