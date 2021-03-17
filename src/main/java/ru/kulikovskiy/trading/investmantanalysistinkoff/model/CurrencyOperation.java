package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import com.sun.istack.NotNull;
import lombok.Data;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class CurrencyOperation {
    @Id
    @NotNull
    private String id;
    @NotNull
    private Currency currency;
    @NotNull
    private LocalDateTime dateOperation;
    @NotNull
    private String operationType;
    private double course;
    @NotNull
    private int quantity;
    @NotNull
    private double payment;
    private String status;
    private double commissionValue;
    private Currency commissionCurrency;
    private String figi;
}
