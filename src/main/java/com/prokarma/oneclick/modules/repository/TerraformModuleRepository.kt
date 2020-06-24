package com.prokarma.oneclick.modules.repository

import com.prokarma.oneclick.modules.bo.TerraformModule
import com.prokarma.oneclick.teams.Team
import com.prokarma.oneclick.teams.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TerraformModuleRepository : MongoRepository<com.prokarma.oneclick.modules.bo.TerraformModule, String> {
    fun countByModuleMetadataCreatedBy(user: User): Long
    fun countByAuthorizedTeamsContainingOrModuleMetadataCreatedBy(team: Team, user: User): Long
    fun findAllByModuleMetadataCreatedByOrAuthorizedTeamsContaining(user: User, team: Team): List<com.prokarma.oneclick.modules.bo.TerraformModule>
    fun findAllByModuleMetadataCreatedBy(user: User): List<com.prokarma.oneclick.modules.bo.TerraformModule>
}
