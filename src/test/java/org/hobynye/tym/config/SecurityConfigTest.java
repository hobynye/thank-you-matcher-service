package org.hobynye.tym.config;

import org.hobynye.tym.auth.MeController;
import org.hobynye.tym.ping.PingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MeController.class, PingController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "CLIENT_ID=test-client-id",
        "ISSUER_URI=https://accounts.google.com",
        "app.security.allowed-domain=hobynye.org"
})
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtDecoder jwtDecoder;

    // --- Filter chain ---

    @Test
    void pingEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk());
    }

    @Test
    void meEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointReturnsUserClaimsWithValidJwt() throws Exception {
        mockMvc.perform(get("/api/me")
                        .with(jwt().jwt(j -> j
                                .subject("user-123")
                                .claim("email", "user@hobynye.org")
                                .claim("name", "Test User")
                                .claim("hd", "hobynye.org"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value("user-123"))
                .andExpect(jsonPath("$.email").value("user@hobynye.org"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.hd").value("hobynye.org"));
    }

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
