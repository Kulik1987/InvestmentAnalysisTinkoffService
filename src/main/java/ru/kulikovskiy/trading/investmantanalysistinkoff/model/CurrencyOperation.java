package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
    private double paymentTemp;

    public CurrencyOperation(String id, Currency currency, LocalDateTime dateOperation, String operationType, double course, int quantity, double payment, String status, double commissionValue, Currency commissionCurrency, String figi) {
        this.id = id;
        this.currency = currency;
        this.dateOperation = dateOperation;
        this.operationType = operationType;
        this.course = course;
        this.quantity = quantity;
        this.payment = payment;
        this.status = status;
        this.commissionValue = commissionValue;
        this.commissionCurrency = commissionCurrency;
        this.figi = figi;
    }
}
