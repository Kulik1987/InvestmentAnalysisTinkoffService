package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.TotalReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

import java.util.List;

public interface AnalyzePortfolioService {
    List<TotalReportDto> getTotalReport(String token) throws NotFoundException;

    List<TotalReportDto> getReportAllDayAllInstrumentSeparatePayIn(String token) throws NotFoundException;

    List<OneTickerCloseOperationReportDto> getReportAllDayByTickerCloseOperation(String token, String ticker) throws NotFoundException;
}
