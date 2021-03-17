package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellInstrument {
    private String id;
    private int version;
    private LocalDateTime endDate;
    private int countDay;
    private int quantitySell;
    private double sellCourse;
    private int quantityTemp;

    public SellInstrument(String id) {
        this.id = id;
    }

}
