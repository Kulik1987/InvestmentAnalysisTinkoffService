package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.InstrumentDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface InstrumentsService {
    InstrumentDto getInstruments(String token) throws NotFoundException;

}
