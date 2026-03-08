package org.hobynye.tym.ping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PingControllerTest {
    PingController controller;

    @BeforeEach
    public void setUp() {
        controller = new PingController();
    }

    @Test
    public void testPing() {
        when(OffsetDateTime.now().toString()).thenReturn("2021-01-01T00:00:00Z");

        Map<String, Object> result = controller.ping();

        assertThat(result.get("ok"), equalTo(true));
        assertThat(result.get("timestamp"), equalTo("2021-01-01T00:00:00Z"));
    }
}
