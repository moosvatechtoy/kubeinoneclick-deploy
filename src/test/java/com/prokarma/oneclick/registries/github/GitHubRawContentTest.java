package com.prokarma.oneclick.registries.github;

import com.prokarma.oneclick.modules.bo.TerraformModule;
import com.prokarma.oneclick.registries.RegistryDetails;
import com.prokarma.oneclick.registries.RegistryFile;
import com.prokarma.oneclick.registries.RegistryType;
import com.prokarma.oneclick.modules.bo.TerraformModule;
import com.prokarma.oneclick.registries.RegistryDetails;
import com.prokarma.oneclick.registries.RegistryFile;
import com.prokarma.oneclick.registries.RegistryType;
import com.prokarma.oneclick.teams.OAuth2User;
import com.prokarma.oneclick.teams.User;
import org.bson.internal.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubRawContentTest {

    @InjectMocks
    private GitHubRawContent gitHubRawContent;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void matches_shouldReturnTrueForGithubModule() {
        TerraformModule githubModule = new TerraformModule();
        githubModule.setRegistryDetails(new RegistryDetails(RegistryType.GITHUB, "some/project"));
        Assertions.assertTrue(gitHubRawContent.matches(githubModule));
    }

    @Test
    void matches_shouldReturnFalseForGitlabModule() {
        TerraformModule gitlabModule = new TerraformModule();
        gitlabModule.setRegistryDetails(new RegistryDetails(RegistryType.GITLAB, "123456"));
        Assertions.assertFalse(gitHubRawContent.matches(gitlabModule));
    }

    @Test
    void getReadmeContent_shouldCallTheApi_andServeDecodedContent(){
        // given
        var module = new TerraformModule();
        module.setRegistryDetails(new RegistryDetails(RegistryType.GITHUB, "Apophis/Chulak"));

        var jack = new User("Jack", null);
        jack.setOAuth2User(new OAuth2User("GITHUB","TOKENSTRING", null));
        module.getModuleMetadata().setCreatedBy(jack);

        var requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        var githubFile = new RegistryFile(Base64.encode("# Module Readme".getBytes()) + "\n");
        var response = new ResponseEntity<>(githubFile, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/Apophis/Chulak/contents/README.md?ref=master"),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(RegistryFile.class))).thenReturn(response);

        // when
        var result = gitHubRawContent.getReadme(module);

        // then
        // content is served decoded
        org.assertj.core.api.Assertions.assertThat(result).isPresent().contains("# Module Readme");
        // request is authenticated
        assertThat(requestCaptor.getValue().getHeaders()).containsEntry("Authorization", List.of("Bearer TOKENSTRING"));
    }

    @Test
    void getReadmeContent_shouldCallTheApiWithoutAuth_ifNoToken_andServeDecodedContent(){
        // given
        var module = new TerraformModule();
        module.setRegistryDetails(new RegistryDetails(RegistryType.GITHUB,  "Apophis/Chulak"));

        var jack = new User("Jack", null);
        module.getModuleMetadata().setCreatedBy(jack);

        var requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        var githubFile = new RegistryFile(Base64.encode("# Module Readme".getBytes()));
        var response = new ResponseEntity<>(githubFile, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/Apophis/Chulak/contents/README.md?ref=master"),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(RegistryFile.class))).thenReturn(response);

        // when
        var result = gitHubRawContent.getReadme(module);

        // then
        // content is served decoded
        org.assertj.core.api.Assertions.assertThat(result).isPresent().contains("# Module Readme");
        // request is authenticated
        assertThat(requestCaptor.getValue().getHeaders()).isEmpty();
    }

    @Test
    void getReadmeContent_shouldCallTheApiWithoutAuth_ifNoOwner_andServeDecodedContent(){
        // given
        var module = new TerraformModule();
        module.setRegistryDetails(new RegistryDetails(RegistryType.GITHUB,  "Apophis/Chulak"));

        var requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        var githubFile = new RegistryFile(Base64.encode("# Module Readme".getBytes()));
        var response = new ResponseEntity<>(githubFile, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/Apophis/Chulak/contents/README.md?ref=master"),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(RegistryFile.class))).thenReturn(response);

        // when
        var result = gitHubRawContent.getReadme(module);

        // then
        // content is served decoded
        org.assertj.core.api.Assertions.assertThat(result).isPresent().contains("# Module Readme");
        // request is authenticated
        assertThat(requestCaptor.getValue().getHeaders()).isEmpty();
    }

    @Test
    void getReadmeContent_withNoRegistryDetails_returnsEmptyContent(){
        // given
        var module = new TerraformModule();

        // when
        var result = gitHubRawContent.getReadme(module);

        // then
        org.assertj.core.api.Assertions.assertThat(result).isEmpty();
    }

    @Test
    void getReadmeContent_shouldReturnEmpty_whenReadmeDoesntExists(){
        // given
        var module = new TerraformModule();
        module.setRegistryDetails(new RegistryDetails(RegistryType.GITHUB, "Apophis/Chulak"));

        var jack = new User("Jack", null);
        jack.setOAuth2User(new OAuth2User("GITHUB","TOKENSTRING", null));
        module.getModuleMetadata().setCreatedBy(jack);

        var requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        var response = new ResponseEntity<RegistryFile>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/Apophis/Chulak/contents/README.md?ref=master"),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(RegistryFile.class))).thenReturn(response);

        // when
        var result = gitHubRawContent.getReadme(module);

        // then
        // content is empty
        org.assertj.core.api.Assertions.assertThat(result).isEmpty();
        // request is authenticated
        assertThat(requestCaptor.getValue().getHeaders()).containsEntry("Authorization", List.of("Bearer TOKENSTRING"));
    }

}