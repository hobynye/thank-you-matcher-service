package org.hobynye.tym;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
public class ApplicationIT {

    ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context, notNullValue());
    }
}
