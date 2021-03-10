package ru.kulikovskiy.trading.investmantanalysistinkoff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean(name = "tcsRestTemplate")
    RestTemplate resttemplate() {
        return new RestTemplate();
    }
}
