package io.codeka.gaia.registries.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.codeka.gaia.modules.bo.Variable
import io.codeka.gaia.modules.repository.TerraformModuleRepository
import io.codeka.gaia.registries.RegistryDetails
import io.codeka.gaia.registries.RegistryType
import io.codeka.gaia.teams.OAuth2User
import io.codeka.gaia.teams.User
import io.codeka.gaia.test.MongoContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.client.RestTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@SpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureWebClient
@AutoConfigureMockMvc
class GitlabRegistryControllerIT {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var terraformModuleRepository: TerraformModuleRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        private val mongoContainer = MongoContainer().withScript("src/test/resources/db/10_user.js")
    }

    @Autowired
    private lateinit var gitlabRegistryController: GitlabRegistryController

    @Test
    fun validateTestConfiguration() {
        assertThat(objectMapper).isNotNull
        assertThat(gitlabRegistryController).isNotNull
        assertThat(restTemplate).isNotNull
    }

    @Test
    fun testGetRepositories() {
        // given
        val server = MockRestServiceServer.bindTo(restTemplate).build()
        server.expect(requestTo("https://gitlab.com/api/v4/projects?visibility=public&owned=true"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/gitlab/selmak-public-repos.json"), MediaType.APPLICATION_JSON))

        val selmak = User("Selmak", null)
        selmak.oAuth2User = OAuth2User("GITLAB", "Tok'ra", null)

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/registries/gitlab/repositories").with(SecurityMockMvcRequestPostProcessors.user("selmak")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""[
                    {"fullName":"selmak/terraform-docker-mongo","htmlUrl":"https://gitlab.com/selmak/terraform-docker-mongo","id":"16181047"}
                    ]""".trimIndent())
                )

        server.verify()
    }

    @Test
    fun testImportRepository() {
        // given
        val server = MockRestServiceServer.bindTo(restTemplate).build()

        server.expect(requestTo("https://gitlab.com/api/v4/projects/16181047"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/gitlab/selmak-terraform-docker-mongo.json"), MediaType.APPLICATION_JSON))

        server.expect(requestTo("https://gitlab.com/api/v4/projects/16181047/repository/files/variables.tf?ref=master"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/gitlab/selmak-terraform-docker-mongo-content-variables.json"), MediaType.APPLICATION_JSON))

        server.expect(requestTo("https://gitlab.com/api/v4/projects/16181047/repository/files/main.tf?ref=master"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/gitlab/selmak-terraform-docker-mongo-content-main.json"), MediaType.APPLICATION_JSON))

        val selmak = User("Selmak", null)
        selmak.oAuth2User = OAuth2User("GITLAB", "Tok'ra", null)

        // when
        val importedModule = gitlabRegistryController.importRepository("16181047", selmak)

        // then
        assertThat(importedModule).isNotNull

        assertThat(importedModule.id).isNotBlank()
        assertThat(importedModule.name).isEqualTo("selmak/terraform-docker-mongo")

        assertThat(importedModule.moduleMetadata.createdBy).isEqualTo(selmak)
        assertThat(importedModule.moduleMetadata.createdAt).isEqualToIgnoringSeconds(LocalDateTime.now())

        assertThat(importedModule.gitRepositoryUrl).isEqualTo("https://gitlab.com/selmak/terraform-docker-mongo")
        assertThat(importedModule.registryDetails).isEqualTo(RegistryDetails(RegistryType.GITLAB, "16181047"))
        assertThat(importedModule.terraformImage.tag).isEqualTo("latest")

        assertThat(importedModule.variables).containsExactly(
                Variable("mongo_container_name", "string", "name of the container"),
                Variable("mongo_exposed_port", "string", "exposed port of the mongo container", "27017")
        )

        assertThat(terraformModuleRepository.findById(importedModule.id))
                .isNotEmpty
                .hasValueSatisfying { it.name == "selmak/terraform-docker-mongo" }

        server.verify()
    }
}