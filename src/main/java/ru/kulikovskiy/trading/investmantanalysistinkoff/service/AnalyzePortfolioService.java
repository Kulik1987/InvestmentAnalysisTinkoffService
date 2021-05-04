package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.TotalReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AnalyzePortfolioService {
    TotalReportDto getTotalReport(String token) throws NotFoundException;

    TotalReportDto getReportAllDayAllInstrumentSeparatePayIn(String token) throws NotFoundException;

    OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(String token, String ticker) throws NotFoundException;
}
