package org.hobynye.tym.ping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

public class PingControllerIT {

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(new PingController()).build();
    }

    @Test
    void testPingEndpointReturnsOkResponse() {
        client.get().uri("/api/ping")
                .exchange()
                .expectStatus( )
                .isOk()
                .expectBody()
                .jsonPath("$.ok").isEqualTo(true)
                .jsonPath("$.time").isNotEmpty();
    }

    @Test
    void testPingEndpointReturnsErrorResponse() {
        client.post().uri("/api/ping")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}
