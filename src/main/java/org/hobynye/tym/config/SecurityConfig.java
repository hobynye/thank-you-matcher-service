package org.hobynye.tym.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${app.security.allowed-domain}")
  private String allowedDomain;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/api/ping").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder(@Value("${CLIENT_ID}") String clientId) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withIssuerLocation("https://accounts.google.com")
        .build();

    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");
    OAuth2TokenValidator<Jwt> withAudience = audienceValidator(clientId);
    OAuth2TokenValidator<Jwt> withHostedDomain = hostedDomainValidator(allowedDomain);

    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
        withIssuer,
        withAudience,
        withHostedDomain
    ));

    return decoder;
  }

  private OAuth2TokenValidator<Jwt> audienceValidator(String expectedAudience) {
    return jwt -> {
      List<String> audiences = jwt.getAudience();
      if (audiences != null && audiences.contains(expectedAudience)) {
        return OAuth2TokenValidatorResult.success();
      }
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid_token", "Invalid audience", null)
      );
    };
  }

  private OAuth2TokenValidator<Jwt> hostedDomainValidator(String expectedDomain) {
    return jwt -> {
      String hd = jwt.getClaimAsString("hd");
      if (expectedDomain.equalsIgnoreCase(hd)) {
        return OAuth2TokenValidatorResult.success();
      }
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid_token", "User is not in the allowed Google Workspace domain", null)
      );
    };
  }
}