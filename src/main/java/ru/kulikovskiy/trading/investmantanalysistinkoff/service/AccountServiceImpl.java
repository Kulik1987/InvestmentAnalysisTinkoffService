package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

import static ru.kulikovskiy.trading.HazelcastConst.TOKENS;

@Service
@Slf4j
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public void saveToken(String token, String chatId) {
        hazelcastInstance.getMap(TOKENS).put(chatId, token);
    }
}
