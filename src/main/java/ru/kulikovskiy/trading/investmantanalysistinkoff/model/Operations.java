package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tinkoff.invest.openapi.models.Currency;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operations {
    @NotNull
    private String operationType;
    @NotNull
    private String date;
    private boolean isMarginCall;
    private String figi;
    @NotNull
    private int quantity;
    private int quantityExecuted;
    private double price;
    @NotNull
    private double payment;
    @NotNull
    private Currency currency;
    private Commission commission;
    private List<Trades> trades;
    private String status;
    @NotNull
    private String id;
}
