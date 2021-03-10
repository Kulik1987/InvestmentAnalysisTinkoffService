package ru.kulikovskiy.trading.investmantanalysistinkoff.mapper;

import org.springframework.stereotype.Component;
import ru.kulikovskiy.trading.DateUtil;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;

import static java.util.Optional.ofNullable;

@Component
public class CurrencyOperationsMapper {
    public CurrencyOperation toCurrencyOperation(Operations operations) {
        CurrencyOperation currencyOperation = new CurrencyOperation();
        currencyOperation.setId(operations.getId());
        currencyOperation.setDateOperation(DateUtil.getLocalDateTime(operations.getDate()));
        currencyOperation.setCurrency(operations.getCurrency());
        currencyOperation.setOperationType(operations.getOperationType());
        currencyOperation.setQuantity(operations.getQuantity());
        currencyOperation.setPayment(operations.getPayment());

        if (operations.getCommission() != null) {
            ofNullable(operations.getCommission().getCurrency()).ifPresent(currencyOperation::setCommissionCurrency);
            ofNullable(operations.getCommission().getValue()).ifPresent(currencyOperation::setCommissionValue);
        }

        ofNullable(operations.getPrice()).ifPresent(currencyOperation::setCourse);
        ofNullable(operations.getStatus()).ifPresent(currencyOperation::setStatus);
        ofNullable(operations.getFigi()).ifPresent(currencyOperation::setFigi);
        return currencyOperation;
    }
}