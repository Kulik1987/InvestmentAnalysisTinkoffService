package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

import java.util.List;

public interface AccountService {
    List<AccountDto> saveClientAccount(String token, Long chatId) throws NotFoundException;

    String getAccountId(String token, String brokerAccountType) throws NotFoundException;
    Account getAccount(Long chatId, String brokerAccountType) throws NotFoundException;
}
