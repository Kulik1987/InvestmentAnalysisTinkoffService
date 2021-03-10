package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;
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

    List<Position> getPosition(String brokerAccountId, String token);

    double getCandles(String figi, String token, String from, String to);

}
