package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.OperationType;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "instrument_operation", schema ="investment")
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
