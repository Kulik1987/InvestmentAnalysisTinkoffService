package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.AccountRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AccountDto> saveClientAccount(String token, Long chatId) throws NotFoundException {
        List<AccountDto> accountDtoList = investmentTinkoffService.getAccounts(token);
        if (accountDtoList == null || accountDtoList.size() == 0) {
            throw new NotFoundException("accounts not found in the Tinkoff investment");
        }
        accountDtoList.forEach(accountDto -> {
            Account account = new Account(accountDto.getBrokerAccountId(), accountDto.getBrokerAccountType(), token, Boolean.FALSE, String.valueOf(chatId));
            accountRepository.save(account);
        });
        return accountDtoList;
    }

    @Override
    public String getAccountId(String token, String brokerAccountType) throws NotFoundException {
        Account account = accountRepository.findByTokenAndBrokerAccountType(token, brokerAccountType);
        if (account == null || StringUtil.isEmpty(account.getBrokerAccountId())) {
            throw new NotFoundException("account not found in the Tinkoff investment");
        }
        return account.getBrokerAccountId();
    }

    @Override
    public Account getAccount(Long chatId, String brokerAccountType) throws NotFoundException {
        Account account = accountRepository.findByChatIdAndBrokerAccountType(String.valueOf(chatId), brokerAccountType);
        if (account == null || StringUtil.isEmpty(account.getBrokerAccountId())) {
            throw new NotFoundException("account not found in the Tinkoff investment");
        }
        return account;
    }
}
