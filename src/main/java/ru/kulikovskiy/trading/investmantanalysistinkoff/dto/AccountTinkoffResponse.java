package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class AccountTinkoffResponse {
    private String trackingId;
    private Payload payload;
    private String status;
}
