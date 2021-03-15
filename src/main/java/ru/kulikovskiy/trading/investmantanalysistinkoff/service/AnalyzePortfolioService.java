package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AnalyzePortfolioService {
    AllMoneyReportDto getReportAllDayAllInstrument(String token, String brokerType) throws NotFoundException;

    AllMoneyReportDto getReportAllDayAllInstrumentSeparatePayIn(String token, String brokerType) throws NotFoundException;

    OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(String token, String brokerType, String ticker) throws NotFoundException;

    AllTickerCloseOperationReportDto getAllTickerCloseOperationReportDto(String token, String brokerType) throws NotFoundException;

}
