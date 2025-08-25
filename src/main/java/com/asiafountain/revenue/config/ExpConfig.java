package com.asiafountain.revenue.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "exp")
@NoArgsConstructor
@Setter
@Getter
public class ExpConfig {
    private String baseUrl;
    private String apiKey;
    private String sharedKey;
    private String email;
}
