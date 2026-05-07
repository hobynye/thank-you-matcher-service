package org.hobynye.tym.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    // --- Audience validator ---

    @Test
    void audienceValidatorPassesWithMatchingAudience() {
        OAuth2TokenValidator<Jwt> validator = audienceValidator("test-client-id");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(List.of("test-client-id"));

        assertThat(validator.validate(jwt).hasErrors(), equalTo(false));
    }

    @Test
    void audienceValidatorFailsWithWrongAudience() {
        OAuth2TokenValidator<Jwt> validator = audienceValidator("test-client-id");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(List.of("wrong-client-id"));

        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertThat(result.hasErrors(), equalTo(true));
    }

    @Test
    void audienceValidatorFailsWhenAudienceIsNull() {
        OAuth2TokenValidator<Jwt> validator = audienceValidator("test-client-id");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(null);

        assertThat(validator.validate(jwt).hasErrors(), equalTo(true));
    }

    // --- Hosted domain validator ---

    @Test
    void hostedDomainValidatorPassesWithMatchingDomain() {
        OAuth2TokenValidator<Jwt> validator = hostedDomainValidator("hobynye.org");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("hd")).thenReturn("hobynye.org");

        assertThat(validator.validate(jwt).hasErrors(), equalTo(false));
    }

    @Test
    void hostedDomainValidatorIsCaseInsensitive() {
        OAuth2TokenValidator<Jwt> validator = hostedDomainValidator("hobynye.org");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("hd")).thenReturn("HOBYNYE.ORG");

        assertThat(validator.validate(jwt).hasErrors(), equalTo(false));
    }

    @Test
    void hostedDomainValidatorFailsWithWrongDomain() {
        OAuth2TokenValidator<Jwt> validator = hostedDomainValidator("hobynye.org");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("hd")).thenReturn("gmail.com");

        assertThat(validator.validate(jwt).hasErrors(), equalTo(true));
    }

    @Test
    void hostedDomainValidatorFailsWhenHdClaimAbsent() {
        OAuth2TokenValidator<Jwt> validator = hostedDomainValidator("hobynye.org");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("hd")).thenReturn(null);

        assertThat(validator.validate(jwt).hasErrors(), equalTo(true));
    }

    // --- Helpers ---

    private OAuth2TokenValidator<Jwt> audienceValidator(String clientId) {
        return ReflectionTestUtils.invokeMethod(new SecurityConfig(), "audienceValidator", clientId);
    }

    private OAuth2TokenValidator<Jwt> hostedDomainValidator(String domain) {
        return ReflectionTestUtils.invokeMethod(new SecurityConfig(), "hostedDomainValidator", domain);
    }
}
