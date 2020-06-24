package com.prokarma.oneclick.registries.github

import com.fasterxml.jackson.annotation.JsonAlias
import com.prokarma.oneclick.registries.AbstractRegistryApi
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.SourceRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GithubRegistryApi(restTemplate: RestTemplate): AbstractRegistryApi<GithubRepository>(
        restTemplate,
        RegistryType.GITHUB,
        GithubRepository::class.java,
        Array<GithubRepository>::class.java)

/**
 * Github source repository implementation.
 * The id property is read-only and computed from the repo's fullname
 */
data class GithubRepository (
        @JsonAlias("full_name") override val fullName: String,
        @JsonAlias("html_url") override val htmlUrl: String): SourceRepository {

    override val id: String
        get() = fullName

}
