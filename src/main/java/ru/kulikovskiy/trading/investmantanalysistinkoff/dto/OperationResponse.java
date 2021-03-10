package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class OperationResponse {
    int countLoadOperation;
    String errorMessage;
}
