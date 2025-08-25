package com.asiafountain.revenue.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tvlk")
@NoArgsConstructor
@Setter
@Getter
public class TvlkConfig {
    private String authUrl;
    private String flightBaseUl;
    private String flightClientId;
    private String flightClientSecret;
    private String hotelBaseUrl;
    private String hotelClientId;
    private String hotelClientSecret;

}
