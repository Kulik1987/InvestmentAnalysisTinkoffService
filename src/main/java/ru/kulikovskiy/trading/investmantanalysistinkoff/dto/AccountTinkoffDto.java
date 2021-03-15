package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class AccountTinkoffDto {
    private String trackingId;
    private Payload payload;
    private String status;
}
