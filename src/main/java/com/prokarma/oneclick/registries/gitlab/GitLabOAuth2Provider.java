package com.prokarma.oneclick.registries.gitlab;

import com.prokarma.oneclick.registries.RegistryOAuth2Provider;

public class GitLabOAuth2Provider implements RegistryOAuth2Provider {
    @Override
    public String getProvider() {
        return "gitlab";
    }
}
