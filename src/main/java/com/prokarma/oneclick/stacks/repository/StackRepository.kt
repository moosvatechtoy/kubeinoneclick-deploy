package com.prokarma.oneclick.stacks.repository

import com.prokarma.oneclick.stacks.bo.Stack
import com.prokarma.oneclick.stacks.bo.StackState
import com.prokarma.oneclick.teams.Team
import com.prokarma.oneclick.teams.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository interface for stacks
 */
@Repository
interface StackRepository : MongoRepository<com.prokarma.oneclick.stacks.bo.Stack, String> {
    fun countStacksByStateAndOwnerTeam(state: com.prokarma.oneclick.stacks.bo.StackState, team: Team): Long
    fun countStacksByStateAndCreatedBy(state: com.prokarma.oneclick.stacks.bo.StackState, user: User): Long
    fun countStacksByCreatedBy(user: User): Long
    fun countStacksByState(state: com.prokarma.oneclick.stacks.bo.StackState): Long
    fun findByCreatedBy(userWithNoTeam: User): List<com.prokarma.oneclick.stacks.bo.Stack>
    fun findByOwnerTeam(team: Team): List<com.prokarma.oneclick.stacks.bo.Stack>
    fun countByOwnerTeam(team: Team): Long
    fun findByIdAndOwnerTeam(id: String, team: Team): Optional<com.prokarma.oneclick.stacks.bo.Stack>
    fun findByModuleId(moduleId: String): List<com.prokarma.oneclick.stacks.bo.Stack>
}
