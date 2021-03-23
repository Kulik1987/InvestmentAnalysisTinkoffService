package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AnalyzePortfolioService {
    AllMoneyReportDto getReportAllDayAllInstrument(Long chatId, String brokerType) throws NotFoundException;

    AllMoneyReportDto getReportAllDayAllInstrumentSeparatePayIn(Long chatId, String brokerType) throws NotFoundException;

    OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(Long chatId, String brokerType, String ticker) throws NotFoundException;

    AllTickerCloseOperationReportDto getAllTickerCloseOperationReportDto(Long chatId, String brokerType) throws NotFoundException;

}
