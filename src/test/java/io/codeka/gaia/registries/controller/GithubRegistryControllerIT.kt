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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@SpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureWebClient
@AutoConfigureMockMvc
class GithubRegistryControllerIT {

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
    private lateinit var githubRegistryController: GithubRegistryController

    @Test
    fun validateTestConfiguration() {
        assertThat(mockMvc).isNotNull
        assertThat(objectMapper).isNotNull
        assertThat(githubRegistryController).isNotNull
        assertThat(restTemplate).isNotNull
    }

    @Test
    fun testGetRepositories() {
        // given
        val server = MockRestServiceServer.bindTo(restTemplate).build()
        server.expect(requestTo("https://api.github.com/user/repos?visibility=public"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/github/selmak-public-repos.json"), MediaType.APPLICATION_JSON))

        val selmak = User("Selmak", null)
        selmak.oAuth2User = OAuth2User("GITHUB", "Tok'ra", null)

        // when
        mockMvc.perform(get("/api/registries/github/repositories").with(user("selmak")))
                .andExpect(status().isOk())
                .andExpect(content().json("""[
                    {"fullName":"selmak/terraform-aws-eks","htmlUrl":"https://github.com/selmak/terraform-aws-eks","id":"selmak/terraform-aws-eks"},
                    {"fullName":"selmak/terraform-docker-mongo","htmlUrl":"https://github.com/selmak/terraform-docker-mongo","id":"selmak/terraform-docker-mongo"},
                    {"fullName":"selmak/terraform-provider-aws-examples","htmlUrl":"https://github.com/selmak/terraform-provider-aws-examples","id":"selmak/terraform-provider-aws-examples"}
                    ]""".trimIndent())
                )

        server.verify()
    }

    @Test
    fun testImportRepository() {
        // given
        val server = MockRestServiceServer.bindTo(restTemplate).build()

        server.expect(requestTo("https://api.github.com/repos/selmak/terraform-docker-mongo"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/github/selmak-terraform-docker-mongo.json"), MediaType.APPLICATION_JSON))

        server.expect(requestTo("https://api.github.com/repos/selmak/terraform-docker-mongo/contents/variables.tf?ref=master"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/github/selmak-terraform-docker-mongo-content-variables.json"), MediaType.APPLICATION_JSON))

        server.expect(requestTo("https://api.github.com/repos/selmak/terraform-docker-mongo/contents/main.tf?ref=master"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer Tok'ra"))
                .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("/rest/github/selmak-terraform-docker-mongo-content-main.json"), MediaType.APPLICATION_JSON))

        val selmak = User("Selmak", null)
        selmak.oAuth2User = OAuth2User("GITHUB", "Tok'ra", null)

        // when
        val importedModule = githubRegistryController.importRepository("selmak", "terraform-docker-mongo", selmak)

        // then
        assertThat(importedModule).isNotNull

        assertThat(importedModule.id).isNotBlank()
        assertThat(importedModule.name).isEqualTo("selmak/terraform-docker-mongo")

        assertThat(importedModule.moduleMetadata.createdBy).isEqualTo(selmak)
        assertThat(importedModule.moduleMetadata.createdAt).isEqualToIgnoringSeconds(LocalDateTime.now())

        assertThat(importedModule.gitRepositoryUrl).isEqualTo("https://github.com/selmak/terraform-docker-mongo")
        assertThat(importedModule.registryDetails).isEqualTo(RegistryDetails(RegistryType.GITHUB, "selmak/terraform-docker-mongo"))
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