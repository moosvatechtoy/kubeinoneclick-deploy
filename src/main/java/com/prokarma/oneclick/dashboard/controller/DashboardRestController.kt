package com.prokarma.oneclick.dashboard.controller

import com.prokarma.oneclick.modules.repository.TerraformModuleRepository
import com.prokarma.oneclick.stacks.bo.StackState
import com.prokarma.oneclick.stacks.repository.StackRepository
import com.prokarma.oneclick.teams.Team
import com.prokarma.oneclick.teams.User
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
@Secured
class DashboardRestController(
    private val moduleRepository: TerraformModuleRepository,
    private val stackRepository: StackRepository) {

    @GetMapping("/summary")
    fun summary(user: User, team: Team?) =
        when {
            user.isAdmin -> {
                mapOf(
                    "modulesCount" to moduleRepository.count(), "stacksCount" to stackRepository.count(),
                    "runningStacksCount" to stackRepository.countStacksByState(com.prokarma.oneclick.stacks.bo.StackState.RUNNING),
                        "stacks" to stackRepository.findAll())
            }
            team != null -> {
                mapOf(
                    "modulesCount" to moduleRepository.countByAuthorizedTeamsContainingOrModuleMetadataCreatedBy(team, user),
                        "stacksCount" to stackRepository.countByOwnerTeam(team),
                    "runningStacksCount" to stackRepository.countStacksByStateAndOwnerTeam(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, team),
                        "stacks" to stackRepository.findByOwnerTeam(team))
            }
            else -> {
                mapOf(
                    "modulesCount" to moduleRepository.countByModuleMetadataCreatedBy(user),
                        "stacksCount" to stackRepository.countStacksByCreatedBy(user),
                    "runningStacksCount" to stackRepository.countStacksByStateAndCreatedBy(com.prokarma.oneclick.stacks.bo.StackState.RUNNING, user),
                        "stacks" to stackRepository.findByCreatedBy(user))
            }
        }

}
