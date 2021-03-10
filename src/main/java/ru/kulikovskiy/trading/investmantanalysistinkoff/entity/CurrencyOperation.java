package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import com.sun.istack.NotNull;
import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.OperationType;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "currency_operation", schema ="investment")
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
