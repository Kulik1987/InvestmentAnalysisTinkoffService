package ru.kulikovskiy.trading.investmantanalysistinkoff.mapper;

import org.springframework.stereotype.Component;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.BuyInstrument;

@Component
public class BuyInstrumentMapper {
    public BuyInstrument getBuyInstrumentFromInstrumentOperation(InstrumentOperation instrumentOperation) {
        BuyInstrument buyInstrument = new BuyInstrument(instrumentOperation.getId());
        buyInstrument.setStartDate(instrumentOperation.getDateOperation());
        buyInstrument.setBuyCourse(instrumentOperation.getCourse());
        buyInstrument.setQuantityBuy(instrumentOperation.getQuantity());
        buyInstrument.setOpenPosition(Boolean.TRUE);
        buyInstrument.setQuantityPortfolio(instrumentOperation.getQuantity());
        return buyInstrument;
    }
}
