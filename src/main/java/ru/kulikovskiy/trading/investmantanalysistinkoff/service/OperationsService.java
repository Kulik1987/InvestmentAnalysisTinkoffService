package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface OperationsService {
    OperationDto getOperationsBetweenDate(String startPeriod, String endPeriod, String token, String brokerType, String accountId) throws NotFoundException;

    OperationDto getOperationsBetweenDateByFigi(String startPeriod, String endPeriod, String token, String brokerType, String accountId, String figi) throws NotFoundException;
}