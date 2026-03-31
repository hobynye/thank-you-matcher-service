package org.hobynye.tym.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeControllerTest {

    private MeController controller;

    @BeforeEach
    void setUp() {
        controller = new MeController();
    }

    @Test
    void meReturnsJwtClaims() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-123");
        when(jwt.getClaimAsString("email")).thenReturn("user@hobynye.org");
        when(jwt.getClaimAsString("name")).thenReturn("Test User");
        when(jwt.getClaimAsString("hd")).thenReturn("hobynye.org");

        Map<String, Object> result = controller.me(jwt);

        assertThat(result.get("sub"), equalTo("user-123"));
        assertThat(result.get("email"), equalTo("user@hobynye.org"));
        assertThat(result.get("name"), equalTo("Test User"));
        assertThat(result.get("hd"), equalTo("hobynye.org"));
    }
}
