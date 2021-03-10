package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.PercentageInstrument;

@Data
public class ReportAllDayAllMoneyResponse {
    private PercentageInstrument reportInstrument;
    private String errorMessage;

    public ReportAllDayAllMoneyResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ReportAllDayAllMoneyResponse(PercentageInstrument reportInstrument) {
        this.reportInstrument = reportInstrument;
    }
}
