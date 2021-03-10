package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class Candles {
    private double o;
    private double c;
    private double h;
    private double l;
    private int v;
    private String time;
    private String interval;
    private String figi;
}
