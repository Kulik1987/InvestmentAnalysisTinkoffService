package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class OperationsDto {
    private String trackingId;
    private Payload payload;
}
