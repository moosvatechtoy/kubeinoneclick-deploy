package com.prokarma.oneclick.config.security.ldap;

import com.prokarma.oneclick.test.MongoContainer;
import com.prokarma.oneclick.test.MongoContainer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext
@Testcontainers
class LdapSecurityConfigIT {

    @Container
    private static MongoContainer mongoContainer = new MongoContainer();

    @Nested
    @SpringBootTest(properties = "oneclick.ldap.enabled=false")
    class LdapSecurityConfigNotLoadedTest {
        @Test
        void ldapSecurityConfig_shouldNotBeInstantiated(
                @Autowired(required = false) LdapSecurityConfig ldapSecurityConfig) {
            assertNull(ldapSecurityConfig);
        }
    }

    @Nested
    @SpringBootTest(properties = {
            "oneclick.ldap.enabled=true",
            "oneclick.ldap.userDnPatterns=test_dn",
            "oneclick.ldap.url=ldap://test_url",
    })
    class LdapSecurityConfigLoadedTest {
        @Test
        void ldapSecurityConfig_shouldBeInstantiated(
                @Autowired(required = false) LdapSecurityConfig ldapSecurityConfig) {
            assertNotNull(ldapSecurityConfig);
        }
    }
}