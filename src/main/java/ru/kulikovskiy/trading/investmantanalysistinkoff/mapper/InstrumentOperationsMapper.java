package ru.kulikovskiy.trading.investmantanalysistinkoff.mapper;

import org.springframework.stereotype.Component;
import ru.kulikovskiy.trading.DateUtil;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class InstrumentOperationsMapper {
    public InstrumentOperation toInstrumentOperation(Operations operations) {
        InstrumentOperation instrumentOperation = new InstrumentOperation();
        instrumentOperation.setId(operations.getId());
        instrumentOperation.setDateOperation(DateUtil.getLocalDateTime(operations.getDate()));
        instrumentOperation.setCurrency(operations.getCurrency());
        instrumentOperation.setOperationType(operations.getOperationType());
        instrumentOperation.setQuantity(operations.getQuantityExecuted());
        instrumentOperation.setPayment(operations.getPayment());

        if (operations.getCommission() != null) {
            ofNullable(operations.getCommission().getCurrency()).ifPresent(instrumentOperation::setCommissionCurrency);
            Optional.of(operations.getCommission().getValue()).ifPresent(instrumentOperation::setCommissionValue);
        }

        Optional.of(operations.getPrice()).ifPresent(instrumentOperation::setCourse);
        ofNullable(operations.getStatus()).ifPresent(instrumentOperation::setStatus);
        ofNullable(operations.getFigi()).ifPresent(instrumentOperation::setFigi);
        return instrumentOperation;
    }
}