package io.codeka.gaia.dashboard.controller

import io.codeka.gaia.modules.repository.TerraformModuleRepository
import io.codeka.gaia.stacks.bo.StackState
import io.codeka.gaia.stacks.repository.StackRepository
import io.codeka.gaia.teams.Team
import io.codeka.gaia.teams.User
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
                    "runningStacksCount" to stackRepository.countStacksByState(StackState.RUNNING),
                        "stacks" to stackRepository.findAll())
            }
            team != null -> {
                mapOf(
                    "modulesCount" to moduleRepository.countByAuthorizedTeamsContainingOrModuleMetadataCreatedBy(team, user),
                        "stacksCount" to stackRepository.countByOwnerTeam(team),
                    "runningStacksCount" to stackRepository.countStacksByStateAndOwnerTeam(StackState.RUNNING, team),
                        "stacks" to stackRepository.findByOwnerTeam(team))
            }
            else -> {
                mapOf(
                    "modulesCount" to moduleRepository.countByModuleMetadataCreatedBy(user),
                        "stacksCount" to stackRepository.countStacksByCreatedBy(user),
                    "runningStacksCount" to stackRepository.countStacksByStateAndCreatedBy(StackState.RUNNING, user),
                        "stacks" to stackRepository.findByCreatedBy(user))
            }
        }

}
