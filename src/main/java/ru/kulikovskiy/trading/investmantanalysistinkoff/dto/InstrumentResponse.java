package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class InstrumentResponse {
    int countStock;
    int countBond;
    int countEtf;
    int countCurrency;
    String errorMessage;
}
