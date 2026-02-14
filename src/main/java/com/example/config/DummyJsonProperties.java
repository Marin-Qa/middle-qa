package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "dummyjson")
@Data
public class DummyJsonProperties {
    private String baseUrl;
    private long timeoutSeconds;
    private Map<String, String> defaultHeaders;
}
