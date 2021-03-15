package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.PercentageInstrument;

@Data
public class AllMoneyReportDto {
    private PercentageInstrument reportInstrument;
    private String errorMessage;

    public AllMoneyReportDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AllMoneyReportDto(PercentageInstrument reportInstrument) {
        this.reportInstrument = reportInstrument;
    }
}
