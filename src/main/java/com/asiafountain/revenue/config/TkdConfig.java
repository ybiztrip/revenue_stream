package com.asiafountain.revenue.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tkd")
@NoArgsConstructor
@Setter
@Getter
public class TkdConfig {
    private String authUrl;
    private String baseUrl;
    private String apiKey;
}
