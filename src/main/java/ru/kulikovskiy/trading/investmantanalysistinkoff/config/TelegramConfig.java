package ru.kulikovskiy.trading.investmantanalysistinkoff.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bot")
@Getter
@Setter
public class TelegramConfig {
    private String name;
    private String token;
}
