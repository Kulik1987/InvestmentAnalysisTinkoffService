package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.ReportAllDayAllMoneyResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.ReportAllDayBreakUpInstrumentResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AnalyzePortfolioService {
    ReportAllDayAllMoneyResponse getReportAllDayAllInstrument(String token, String brokerType) throws NotFoundException;

    ReportAllDayBreakUpInstrumentResponse getReportAllDayBreakUpInstrument(String token, String brokerType) throws NotFoundException;
}
