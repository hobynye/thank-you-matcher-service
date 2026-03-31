package org.hobynye.tym.auth;

import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

  @GetMapping("/api/me")
  public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return Map.of(
        "sub", jwt.getSubject(),
        "email", jwt.getClaimAsString("email"),
        "name", jwt.getClaimAsString("name"),
        "hd", jwt.getClaimAsString("hd")
    );
  }
}