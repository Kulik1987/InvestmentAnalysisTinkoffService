package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperation;

import java.util.List;

@Data
public class OperationDto {
    private List<CurrencyOperation> currencyOperationList;
    private List<InstrumentOperation> instrumentOperationList;
    int countLoadOperation;
    String errorMessage;
}
