package com.prokarma.oneclick.registries.service

import com.prokarma.oneclick.hcl.HclParser
import com.prokarma.oneclick.modules.bo.ModuleMetadata
import com.prokarma.oneclick.modules.bo.TerraformImage
import com.prokarma.oneclick.modules.bo.TerraformModule
import com.prokarma.oneclick.modules.repository.TerraformModuleRepository
import com.prokarma.oneclick.registries.RegistryApi
import com.prokarma.oneclick.registries.RegistryDetails
import com.prokarma.oneclick.registries.RegistryType
import com.prokarma.oneclick.registries.SourceRepository
import com.prokarma.oneclick.teams.User
import org.springframework.stereotype.Service
import java.util.*

interface RegistryService {
    fun importRepository(projectId: String, registryType: RegistryType, user: User): com.prokarma.oneclick.modules.bo.TerraformModule
}

@Service
class RegistryServiceImpl(
        private val hclParser: com.prokarma.oneclick.hcl.HclParser,
        private val moduleRepository: TerraformModuleRepository,
        private val registryApis: Map<RegistryType, RegistryApi<out SourceRepository>>
) : RegistryService {

    override fun importRepository(projectId: String, registryType: RegistryType, user: User): com.prokarma.oneclick.modules.bo.TerraformModule {
        val module = com.prokarma.oneclick.modules.bo.TerraformModule()
        module.id = UUID.randomUUID().toString()

        val codeRepository = registryApis[registryType]?.getRepository(user, projectId)!!

        module.gitRepositoryUrl = codeRepository.htmlUrl
        module.gitBranch = "master"
        module.name = codeRepository.fullName
        module.terraformImage = TerraformImage.defaultInstance()

        module.registryDetails = RegistryDetails(registryType, codeRepository.id)
        module.moduleMetadata = ModuleMetadata(createdBy = user)

        // get variables
        val variablesFile = registryApis[registryType]?.getFileContent(user, projectId, "variables.tf")!!
        module.variables = hclParser.parseVariables(variablesFile)

        // find main provider
        val mainFile = registryApis[registryType]?.getFileContent(user, projectId, "main.tf")!!
        module.mainProvider = hclParser.parseProvider(mainFile)

        // saving module !
        return moduleRepository.save(module)
    }

}