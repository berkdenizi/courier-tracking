package com.bd.couriertracking.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.application.name:courier-tracking}")
    private String appName;

    @Value("${build.version:unknown}")
    private String appVersion;

    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "service", appName,
                "version", appVersion,
                "time", Instant.now().toString()
        );
    }
}