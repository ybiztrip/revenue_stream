package com.asiafountain.revenue.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "general")
@NoArgsConstructor
@Setter
@Getter
public class GeneralConfig {
    private String hotelPath;
    private String flightPath;
}
