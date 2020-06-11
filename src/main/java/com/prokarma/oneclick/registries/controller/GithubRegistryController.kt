package com.prokarma.oneclick.registries.controller

import com.prokarma.oneclick.modules.bo.TerraformModule
import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.github.GithubRepository
import com.prokarma.oneclick.registries.service.RegistryService
import com.prokarma.oneclick.teams.User
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registries/github")
@Secured
class GithubRegistryController(
        private val githubRegistryApi: RegistryApi<GithubRepository>,
        private val registryService: RegistryService) {

    @GetMapping("/repositories")
    fun getRepositories(user: User): List<GithubRepository> {
        return this.githubRegistryApi.getRepositories(user)
    }

    @PostMapping("/repositories/{owner}/{repo}/import")
    @ResponseStatus(HttpStatus.CREATED)
    fun importRepository(@PathVariable owner: String, @PathVariable repo: String, user: User): com.prokarma.oneclick.modules.bo.TerraformModule {
        return registryService.importRepository("$owner/$repo", RegistryType.GITHUB, user)
    }
}
