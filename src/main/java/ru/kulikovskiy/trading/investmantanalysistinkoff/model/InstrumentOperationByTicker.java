package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentOperationByTicker {
    private InstrumentOperation instrumentOperation;
    private String figi;
    private String name;
}
