package ru.kulikovskiy.trading.investmantanalysistinkoff.mapper;

import org.springframework.stereotype.Component;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.SellInstrument;

@Component
public class SellInstrumentMapper {
    public SellInstrument getSellInstrumentFromOperationInstrument(InstrumentOperation instrumentOperation) {
        SellInstrument sellInstrument = new SellInstrument(instrumentOperation.getId());
        sellInstrument.setEndDate(instrumentOperation.getDateOperation());
        sellInstrument.setQuantitySell(instrumentOperation.getQuantity());
        sellInstrument.setQuantityTemp(instrumentOperation.getQuantity());
        sellInstrument.setSellCourse(instrumentOperation.getCourse());
        return sellInstrument;
    }
}
