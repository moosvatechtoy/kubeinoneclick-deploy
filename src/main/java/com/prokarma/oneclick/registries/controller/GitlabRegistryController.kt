package com.prokarma.oneclick.registries.controller

import com.prokarma.oneclick.modules.bo.TerraformModule
import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.gitlab.GitlabRepository
import com.prokarma.oneclick.registries.service.RegistryService
import com.prokarma.oneclick.teams.User
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registries/gitlab")
@Secured
class GitlabRegistryController(
        private val gitlabRegistryApi: RegistryApi<GitlabRepository>,
        private val registryService: RegistryService) {

    @GetMapping("/repositories")
    fun getRepositories(user: User): List<GitlabRepository> {
        return this.gitlabRegistryApi.getRepositories(user)
    }

    @PostMapping("/repositories/{projectId}/import")
    @ResponseStatus(HttpStatus.CREATED)
    fun importRepository(@PathVariable projectId: String, user: User): com.prokarma.oneclick.modules.bo.TerraformModule {
        return registryService.importRepository(projectId, RegistryType.GITLAB, user)
    }
}
