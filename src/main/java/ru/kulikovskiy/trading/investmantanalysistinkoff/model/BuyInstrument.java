package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class BuyInstrument {
    private String id;
    private LocalDateTime startDate;
    private int quantityBuy;
    private double buyCourse;
    private boolean isOpenPosition;
    private int quantityPortfolio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuyInstrument that = (BuyInstrument) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public BuyInstrument(String id) {
        this.id = id;
    }
}
