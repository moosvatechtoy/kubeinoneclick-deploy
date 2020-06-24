package com.prokarma.oneclick.dashboard.controller

import com.prokarma.oneclick.modules.repository.TerraformModuleRepository
import com.prokarma.oneclick.stacks.bo.StackState
import com.prokarma.oneclick.stacks.repository.StackRepository
import com.prokarma.oneclick.teams.Team
import com.prokarma.oneclick.teams.User
import com.prokarma.oneclick.test.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DashboardRestControllerTest {

    @Mock
    lateinit var moduleRepository: TerraformModuleRepository

    @Mock
    lateinit var stackRepository: StackRepository

    @InjectMocks
    lateinit var controller: com.prokarma.oneclick.dashboard.controller.DashboardRestController

    @Nested
    inner class WhenAdminUserTest {

        private val user = User("admin", null)

        @Test
        fun `summary() should return modules count`() {
            // when
            whenever(moduleRepository.count()).thenReturn(1L)
            val result = controller.summary(user, null)

            // then
            verify(moduleRepository, times(1)).count()
            verifyNoMoreInteractions(moduleRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("modulesCount", 1L)
        }

        @Test
        fun `summary() should return stacks count`() {
            // when
            whenever(stackRepository.countStacksByState(com.prokarma.oneclick.stacks.bo.StackState.RUNNING)).thenReturn(2)
            whenever(stackRepository.countStacksByState(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE)).thenReturn(3)
            val result = controller.summary(user, null)

            // then
            verify(stackRepository, times(1)).countStacksByState(com.prokarma.oneclick.stacks.bo.StackState.RUNNING)
            verify(stackRepository, times(1)).countStacksByState(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE)
            verifyNoMoreInteractions(stackRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("runningStacksCount", 2L)
                .containsEntry("toUpdateStacksCount", 3L)
        }

    }

    @Nested
    inner class WhenUserWithTeamTest {

        private val team = Team("userTeam")
        private val user = User("user", team)

        @Test
        fun `summary() should return modules count`() {
            // when
            whenever(moduleRepository.countByAuthorizedTeamsContainingOrModuleMetadataCreatedBy(team, user)).thenReturn(4)
            val result = controller.summary(user, team)

            // then
            verify(moduleRepository, times(1)).countByAuthorizedTeamsContainingOrModuleMetadataCreatedBy(team, user)
            verifyNoMoreInteractions(moduleRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("modulesCount", 4L)
        }

        @Test
        fun `summary() should return stacks count`() {
            // when
            whenever(stackRepository.countStacksByStateAndOwnerTeam(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, team)).thenReturn(5)
            whenever(stackRepository.countStacksByStateAndOwnerTeam(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE, team)).thenReturn(6)
            val result = controller.summary(user, team)

            // then
            verify(stackRepository, times(1)).countStacksByStateAndOwnerTeam(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, team)
            verify(stackRepository, times(1)).countStacksByStateAndOwnerTeam(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE, team)
            verifyNoMoreInteractions(stackRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("runningStacksCount", 5L)
                .containsEntry("toUpdateStacksCount", 6L)
        }

    }

    @Nested
    inner class WhenUserWithoutTeamTest {

        private val user = User("user", null)

        @Test
        fun `summary() should return modules count`() {
            // when
            whenever(moduleRepository.countByModuleMetadataCreatedBy(user)).thenReturn(7)
            val result = controller.summary(user, null)

            // then
            verify(moduleRepository, times(1)).countByModuleMetadataCreatedBy(user)
            verifyNoMoreInteractions(moduleRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("modulesCount", 7L)
        }

        @Test
        fun `summary() should return stacks count`() {
            // when
            whenever(stackRepository.countStacksByStateAndCreatedBy(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, user)).thenReturn(8)
            whenever(stackRepository.countStacksByStateAndCreatedBy(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE, user)).thenReturn(9)
            val result = controller.summary(user, null)

            // then
            verify(stackRepository, times(1)).countStacksByStateAndCreatedBy(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, user)
            verify(stackRepository, times(1)).countStacksByStateAndCreatedBy(com.prokarma.oneclick.stacks.bo.StackState.TO_UPDATE, user)
            verifyNoMoreInteractions(stackRepository)
            assertThat(result)
                .isNotNull
                .containsEntry("runningStacksCount", 8L)
                .containsEntry("toUpdateStacksCount", 9L)
        }

    }

}
