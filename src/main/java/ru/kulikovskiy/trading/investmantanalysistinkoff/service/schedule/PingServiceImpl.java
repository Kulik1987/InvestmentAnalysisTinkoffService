package ru.kulikovskiy.trading.investmantanalysistinkoff.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PingServiceImpl implements PingService {
    @Scheduled(cron = "0 0,15,30,45 * * * *")
    public void running() {
        log.info("Service is running");
    }
}
