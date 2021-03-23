package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

@Data
public class ReportInstrument {
    private String figi;
    private String nameInstrument;
    private String averageProfit;
    private String averagePercentProfit;
    private String averagePercentProfitYear;
    private String averageCountDay;
    private int quantityAll;
    private String allProfitByTicker;
    private String currency;
}
