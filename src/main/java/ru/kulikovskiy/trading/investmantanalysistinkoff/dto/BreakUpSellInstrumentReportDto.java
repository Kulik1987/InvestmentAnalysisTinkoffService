package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

import java.util.List;

@Data
public class BreakUpSellInstrumentReportDto {
    private List<ReportInstrument> reportInstrumentList;
    private String errorMessage;

    public BreakUpSellInstrumentReportDto(List<ReportInstrument> reportInstrumentList) {
        this.reportInstrumentList = reportInstrumentList;
    }

    public BreakUpSellInstrumentReportDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
