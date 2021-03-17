package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;


import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

import java.util.List;

@Data
public class AllTickerCloseOperationReportDto {
    private List<ReportInstrument> reportInstrument;
    private String allSumProfit;
    private String errorMessage;

    public AllTickerCloseOperationReportDto(List<ReportInstrument> reportInstrument, String allSumProfit) {
        this.reportInstrument = reportInstrument;
        this.allSumProfit = allSumProfit;
    }

}
