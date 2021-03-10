package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "profit_instrument", schema ="investment")
public class ProfitInstrument {
    @Id
    @SequenceGenerator(name = "profit_instrument_id", sequenceName = "profit_instrument_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profit_instrument_id")
    private int id;
    private String figi;
    private LocalDate buyDate;
    private LocalDate saleDate;
    private String buyPrice;
    private String salePrice;
    private int dayOwnership;
    private double profit;
    private double percentPerAnnum;
    private String currency;
    private int count;
}
