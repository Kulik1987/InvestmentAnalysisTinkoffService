package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellInstrumentPercentage {
    private String id;
    private int version;
    private LocalDateTime endDate;
    private int countDay;
    private int quantitySell;
    private double sellCourse;
    private double profit;
    private double percentProfit;
    private double percentProfitYear;
    private int quantityTemp;

    public SellInstrumentPercentage(String id) {
        this.id = id;
    }

}
