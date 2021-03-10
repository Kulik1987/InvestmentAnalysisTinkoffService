package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;
import ru.tinkoff.invest.openapi.models.Currency;

@Data
public class Commission {
    private Currency currency;
    private double value;
}
