package com.prokarma.oneclick.registries.config

import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryOAuth2Provider
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.SourceRepository
import com.prokarma.oneclick.registries.github.GitHubOAuth2Provider
import com.prokarma.oneclick.registries.github.GithubRegistryApi
import com.prokarma.oneclick.registries.gitlab.GitLabOAuth2Provider
import com.prokarma.oneclick.registries.gitlab.GitlabRegistryApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Config for registry management
 */
@Configuration
open class RegistryConfig {

    @Bean
    open fun registryOAuth2Providers(): List<com.prokarma.oneclick.registries.RegistryOAuth2Provider> {
        return listOf(com.prokarma.oneclick.registries.github.GitHubOAuth2Provider(), com.prokarma.oneclick.registries.gitlab.GitLabOAuth2Provider())
    }

    @Bean
    open fun registryApis(
            githubRegistryApi: GithubRegistryApi,
            gitlabRegistryApi: GitlabRegistryApi): Map<RegistryType, RegistryApi<out SourceRepository>> {
        return mapOf(Pair(RegistryType.GITHUB, githubRegistryApi),
                Pair(RegistryType.GITLAB, gitlabRegistryApi))
    }
}