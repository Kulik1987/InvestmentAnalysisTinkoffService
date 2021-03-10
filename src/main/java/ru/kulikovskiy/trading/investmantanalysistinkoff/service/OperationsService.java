package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface OperationsService {
    OperationResponse getOperationsBetweenDate(String startPeriod, String endPeriod, String token, String brokerType) throws NotFoundException;
}