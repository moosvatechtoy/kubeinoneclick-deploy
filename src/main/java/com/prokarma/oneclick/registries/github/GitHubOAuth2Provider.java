package com.prokarma.oneclick.registries.github;

import com.prokarma.oneclick.registries.RegistryOAuth2Provider;

public class GitHubOAuth2Provider implements RegistryOAuth2Provider {
    @Override
    public String getProvider() {
        return "github";
    }
}
