package com.example.jsso.config.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.mvc.cors")
public class CorsProperties {

    private List<CorsConfig> configs;

    public static record CorsConfig(
        String pattern,
        String allowedOrigins,
        String allowedOriginPatterns,
        String allowedHeaders,
        String exposedHeaders,
        String allowedMethods,
        Boolean allowCredentials,
        Long maxAge
    ) { }
}
