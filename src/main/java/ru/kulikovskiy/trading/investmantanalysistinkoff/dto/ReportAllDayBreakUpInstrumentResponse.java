package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

import java.util.List;

@Data
public class ReportAllDayBreakUpInstrumentResponse {
    private List<ReportInstrument> reportInstrumentList;
    private String errorMessage;

    public ReportAllDayBreakUpInstrumentResponse(List<ReportInstrument> reportInstrumentList) {
        this.reportInstrumentList = reportInstrumentList;
    }

    public ReportAllDayBreakUpInstrumentResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
