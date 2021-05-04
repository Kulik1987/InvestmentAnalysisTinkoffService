package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.PercentageInstrument;

@Data
public class TotalReportDto {
    private PercentageInstrument reportInstrument;
    private String errorMessage;

    public TotalReportDto(PercentageInstrument reportInstrument) {
        this.reportInstrument = reportInstrument;
    }
}
