package org.hobynye.tym.ping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class PingController {
    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "ok", true,
                "time", OffsetDateTime.now().toString()
        );
    }
}
