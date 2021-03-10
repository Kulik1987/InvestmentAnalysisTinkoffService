package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class AveragePositionPriceNoNkd {
    private String currency;
    private double value;
}
