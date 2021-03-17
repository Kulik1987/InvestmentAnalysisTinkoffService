package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AnalyzePortfolioService {
    AllMoneyReportDto getReportAllDayAllInstrument(String token) throws NotFoundException;

    AllMoneyReportDto getReportAllDayAllInstrumentSeparatePayIn(String token) throws NotFoundException;

    OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(String token, String ticker) throws NotFoundException;
}
