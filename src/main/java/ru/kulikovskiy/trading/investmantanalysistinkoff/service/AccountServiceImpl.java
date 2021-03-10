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

    private final String FIRST_NAME = "KULIK";
    private final String LAST_NAME = "POUL";


    @Override
    public List<AccountDto> saveClientAccount(String token) throws NotFoundException {
        List<AccountDto> accountDtoList = investmentTinkoffService.getAccounts(token);
        if (accountDtoList == null || accountDtoList.size() == 0) {
            throw new NotFoundException("accounts not found in the Tinkoff investment");
        }
        accountDtoList.forEach(accountDto -> {
            Account account = new Account(accountDto.getBrokerAccountId(), accountDto.getBrokerAccountType(), FIRST_NAME, LAST_NAME, token, Boolean.FALSE);
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

}
