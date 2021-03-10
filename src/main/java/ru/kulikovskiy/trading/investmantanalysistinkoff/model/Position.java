package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class Position {
    private String figi;
    private String ticker;
    private String isin;
    private String instrumentType;
    private double balance;
    private double blocked;
    private ExpectedYield expectedYield;
    private int lots;
    private AveragePositionPrice averagePositionPrice;
    private Commission averagePositionPriceNoNkd;
    private String name;
}