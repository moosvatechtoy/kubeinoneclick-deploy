package com.prokarma.oneclick.registries.gitlab

import com.prokarma.oneclick.registries.RegistryRawContent
import com.prokarma.oneclick.registries.RegistryType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GitLabRawContent(restTemplate: RestTemplate) : RegistryRawContent(RegistryType.GITLAB, restTemplate)