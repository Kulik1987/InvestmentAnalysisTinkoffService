package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Candles;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Position;

import java.util.List;

@Data
public class Payload {
    private List<AccountDto> accounts;
    private List<Instruments> instruments;
    private List<Operations> operations;
    private List<Position> positions;
    private List<Candles> candles;
    private String interval;
    private String figi;

}