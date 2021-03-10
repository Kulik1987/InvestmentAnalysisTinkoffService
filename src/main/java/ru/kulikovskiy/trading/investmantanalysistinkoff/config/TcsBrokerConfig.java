package ru.kulikovskiy.trading.investmantanalysistinkoff.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tcsbroker")
@Getter
@Setter
public class TcsBrokerConfig {
    private String url;
}
