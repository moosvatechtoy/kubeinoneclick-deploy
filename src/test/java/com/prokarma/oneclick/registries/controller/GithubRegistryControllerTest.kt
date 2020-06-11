package com.prokarma.oneclick.registries.controller

import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.github.GithubRepository
import com.prokarma.oneclick.registries.service.RegistryService
import com.prokarma.oneclick.teams.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GithubRegistryControllerTest {

    @Mock
    lateinit var githubRegistryApi: RegistryApi<GithubRepository>

    @Mock
    lateinit var registryService: RegistryService

    @InjectMocks
    lateinit var githubRegistryController: GithubRegistryController

    @Test
    fun `getRepositories() should call the github registry api`() {
        // given
        val john = User("john", null)
        // when
        githubRegistryController.getRepositories(john)
        // then
        verify(githubRegistryApi).getRepositories(john)
    }

    @Test
    fun `importRepository() should call the github registry and create a module`() {
        // given
        val user = User("juwit", null)

        // when
        githubRegistryController.importRepository("juwit", "terraform-docker-mongo", user)

        // then
        verify(registryService).importRepository("juwit/terraform-docker-mongo", RegistryType.GITHUB, user)

        verifyNoMoreInteractions(registryService)
    }
}
