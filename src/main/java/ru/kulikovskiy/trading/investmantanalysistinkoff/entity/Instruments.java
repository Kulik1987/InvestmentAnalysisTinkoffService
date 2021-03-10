package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "instruments", schema ="investment")
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
