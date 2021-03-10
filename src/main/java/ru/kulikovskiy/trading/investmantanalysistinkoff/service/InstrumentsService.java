package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.InstrumentResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface InstrumentsService {
    InstrumentResponse getInstruments(String token) throws NotFoundException;

}
