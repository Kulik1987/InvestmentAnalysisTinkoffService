package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;


import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

@Data
public class OneTickerCloseOperationReportDto {
    private ReportInstrument reportInstrument;
    private String errorMessage;

    public OneTickerCloseOperationReportDto(ReportInstrument reportInstrument) {
        this.reportInstrument = reportInstrument;
    }

    public OneTickerCloseOperationReportDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
