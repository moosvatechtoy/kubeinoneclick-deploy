package com.prokarma.oneclick.registries.github

import com.prokarma.oneclick.registries.RegistryRawContent
import com.prokarma.oneclick.registries.RegistryType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GitHubRawContent(restTemplate: RestTemplate) : RegistryRawContent(RegistryType.GITHUB, restTemplate)
