package ru.kulikovskiy.trading.investmantanalysistinkoff.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ClientConfig {
    private String tokenTest;
    private String token;
}
