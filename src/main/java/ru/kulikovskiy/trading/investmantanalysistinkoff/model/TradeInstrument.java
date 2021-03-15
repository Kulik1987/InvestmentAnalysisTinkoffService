package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class TradeInstrument {
    private String figi;
    private String name;
    private List<BuyInstrument> buyInstruments;
    private List<SellInstrumentPercentage> sellInstrument;
    private Dividend dividend;

    public TradeInstrument(String figi) {
        this.figi = figi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeInstrument that = (TradeInstrument) o;
        return figi.equals(that.figi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(figi);
    }
}