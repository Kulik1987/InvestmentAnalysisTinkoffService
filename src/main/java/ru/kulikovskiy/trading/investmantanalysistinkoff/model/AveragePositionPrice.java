package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class AveragePositionPrice {
    private String currency;
    private double value;
}
