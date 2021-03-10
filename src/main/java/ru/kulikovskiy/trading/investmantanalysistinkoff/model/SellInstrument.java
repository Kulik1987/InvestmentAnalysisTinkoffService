package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class SellInstrument {
    private String id;
    private String version;
    private LocalDateTime endDate;
    private long countDay;
    private int quantitySell;
    private double sellCourse;
    private double profit;
    private double percentProfit;
    private double percentProfitYear;

    public SellInstrument(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellInstrument that = (SellInstrument) o;
        return id.equals(that.id) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
