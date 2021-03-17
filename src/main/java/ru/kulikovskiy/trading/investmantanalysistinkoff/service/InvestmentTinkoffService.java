package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Position;

import java.util.List;

public interface InvestmentTinkoffService {
    List<AccountDto> getAccounts(String token);

    List<Instruments> getStocks(String token);

    List<Instruments> getBonds(String token);

    List<Instruments> getEtfs(String token);

    List<Instruments> getCurrencies(String token);

    List<Operations> getOperations(String from, String to, String brokerAccountId, String token);

    List<Operations> getOperationsByFigi(String from, String to, String brokerAccountId, String token, String figi);

    List<Position> getPosition(String brokerAccountId, String token);

}
