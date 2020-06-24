package com.prokarma.oneclick.registries.controller

import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.gitlab.GitlabRepository
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
class GitlabRegistryControllerTest{

    @Mock
    lateinit var gitlabRegistryApi: RegistryApi<GitlabRepository>

    @Mock
    lateinit var registryService: RegistryService

    @InjectMocks
    lateinit var gitlabRegistryController: GitlabRegistryController

    @Test
    fun `getRepositories() should call the gitlab registry api`() {
        // given
        val john = User("john", null)
        // when
        gitlabRegistryController.getRepositories(john)
        // then
        verify(gitlabRegistryApi).getRepositories(john)
    }

    @Test
    fun `importRepository() should call the registryService`() {
        // given
        val user = User("juwit", null)

        // when
        gitlabRegistryController.importRepository("15689", user)

        // then
        verify(registryService).importRepository("15689", RegistryType.GITLAB, user)

        verifyNoMoreInteractions(registryService)
    }
}