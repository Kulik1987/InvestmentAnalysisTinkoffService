package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class InstrumentsTinkoffResponse {
    private String trackingId;
    private Payload payload;
}
