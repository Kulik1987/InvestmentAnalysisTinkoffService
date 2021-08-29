package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

@Data
@NoArgsConstructor
public class OneTickerCloseOperationReportDto {
    private ReportInstrument reportInstrument;
    private String errorMessage;

    public OneTickerCloseOperationReportDto(ReportInstrument reportInstrument) {
        this.reportInstrument = reportInstrument;
    }
}
