package org.hobynye.tym.ping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class PingControllerTest {
    PingController controller;

    @BeforeEach
    public void setUp() {
        controller = new PingController();
    }

    @Test
    public void testPing() {
        Map<String, Object> result = controller.ping();

        assertThat(result.get("ok"), equalTo(true));
        assertThat(result.get("time"), notNullValue());
    }
}
