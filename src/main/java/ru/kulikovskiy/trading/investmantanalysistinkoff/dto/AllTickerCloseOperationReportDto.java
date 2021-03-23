package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;


import lombok.Data;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ReportInstrument;

import java.util.List;

@Data
public class AllTickerCloseOperationReportDto {
    private List<ReportInstrument> reportInstrument;
    private String allSumProfit;
    private String allSumProfitUsd;
    private String allSumProfitEur;
    private String errorMessage;

    public AllTickerCloseOperationReportDto(List<ReportInstrument> reportInstrument, String allSumProfit, String allSumProfitUsd, String allSumProfitEur) {
        this.reportInstrument = reportInstrument;
        this.allSumProfit = allSumProfit;
        this.allSumProfitUsd = allSumProfitUsd;
        this.allSumProfitEur = allSumProfitEur;
    }
}
