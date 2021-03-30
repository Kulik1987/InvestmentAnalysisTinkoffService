package ru.kulikovskiy.trading.investmantanalysistinkoff.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService;

@Service
@Slf4j
public class PingServiceImpl implements PingService {
    @Autowired
    private AccountService accountService;

    @Override
    @Scheduled(cron = "0 */10 * * * *")
    public void pingBot() {
        try {
            String token = accountService.getToken("1");
        } catch (NotFoundException e) {
            log.info("app don't sleep");
        }
    }
}
