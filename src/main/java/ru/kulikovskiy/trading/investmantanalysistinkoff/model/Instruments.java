package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Instruments {
    @Id
    private String figi;
    private String ticker;
    private String isin;
    private double minPriceIncrement;
    private int lot;
    private String currency;
    private String name;
    private String type;
}
