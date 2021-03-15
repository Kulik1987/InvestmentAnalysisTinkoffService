package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;

@Data
public class InstrumentDto {
    int countStock;
    int countBond;
    int countEtf;
    int countCurrency;
    String errorMessage;
}
