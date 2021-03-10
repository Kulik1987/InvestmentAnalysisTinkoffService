package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class Trades {
    private String tradeId;
    private String date;
    private int quantity;
    private double price;
}
