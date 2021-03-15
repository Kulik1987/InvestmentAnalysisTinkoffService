package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.InstrumentDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentsRepository;

import java.util.List;

@Service
public class InstrumentsServiceImpl implements InstrumentsService {
    @Autowired
    private InstrumentsRepository instrumentsRepository;
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;

    @Override
    public InstrumentDto getInstruments(String token) throws NotFoundException {
        InstrumentDto response = new InstrumentDto();
        response.setCountStock(getStocks(token));
        response.setCountBond(getBonds(token));
        response.setCountEtf(getEtfs(token));
        response.setCountCurrency(getCurrencies(token));
        return response;
    }

    private int getStocks(String token) throws NotFoundException {
        List<Instruments> instrumentsList = investmentTinkoffService.getStocks(token);
        throwExceptionInstrumentsEmpty(instrumentsList);
        return saveInstruments(instrumentsList);
    }

    private int getBonds(String token) throws NotFoundException {
        List<Instruments> instrumentsList = investmentTinkoffService.getBonds(token);
        throwExceptionInstrumentsEmpty(instrumentsList);
        return saveInstruments(instrumentsList);
    }


    private int getEtfs(String token) throws NotFoundException {
        List<Instruments> instrumentsList = investmentTinkoffService.getEtfs(token);
        throwExceptionInstrumentsEmpty(instrumentsList);
        return saveInstruments(instrumentsList);
    }

    private int getCurrencies(String token) throws NotFoundException {
        List<Instruments> instrumentsList = investmentTinkoffService.getCurrencies(token);
        throwExceptionInstrumentsEmpty(instrumentsList);
        return saveInstruments(instrumentsList);
    }

    private int saveInstruments(List<Instruments> instrumentsList) {
        return instrumentsList.stream().mapToInt(instrument -> {
            Instruments instruments = new Instruments(instrument.getFigi(), instrument.getTicker(), instrument.getIsin(), instrument.getMinPriceIncrement(),
                    instrument.getLot(), instrument.getCurrency(), instrument.getName(), instrument.getType());
            instrumentsRepository.save(instruments);
            return 1;
        }).sum();
    }

    private void throwExceptionInstrumentsEmpty(List<Instruments> instrumentsList) throws NotFoundException {
        if (instrumentsList.isEmpty()) {
            throw new NotFoundException("please load instrument from Tinkoff investment");
        }
    }
}
