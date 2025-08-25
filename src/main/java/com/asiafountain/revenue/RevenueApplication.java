package com.asiafountain.revenue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class RevenueApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevenueApplication.class, args);
	}

	@Bean
	RestTemplate restTemplateRaw(RestTemplateBuilder builder, List<HttpMessageConverter<?>> messageConverters){
		return builder.build();
	}
}
