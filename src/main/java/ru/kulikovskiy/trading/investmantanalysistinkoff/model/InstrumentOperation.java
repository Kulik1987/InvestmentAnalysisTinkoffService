package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentOperation {
    @Id
    private String id;
    private Currency currency;
    private LocalDateTime dateOperation;
    private String operationType;
    private double course;
    private int quantity;
    private double payment;
    private String status;
    private double commissionValue;
    private Currency commissionCurrency;
    private String figi;

}
