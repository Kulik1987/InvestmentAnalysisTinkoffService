package ru.kulikovskiy.trading.investmantanalysistinkoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableJpaRepositories
public class InvestmentAnalysisTinkoffService {
    public static
    void main(String[] args) {

        ApiContextInitializer.init();
        SpringApplication.run(InvestmentAnalysisTinkoffService.class, args);
    }
}

