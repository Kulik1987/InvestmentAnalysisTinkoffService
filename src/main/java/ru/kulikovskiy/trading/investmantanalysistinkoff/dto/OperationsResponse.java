package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class OperationsResponse {
    private String trackingId;
    private Payload payload;
}
