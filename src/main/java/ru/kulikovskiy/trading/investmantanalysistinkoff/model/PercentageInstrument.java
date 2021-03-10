package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PercentageInstrument {
    private LocalDate startDate;
    private LocalDate endDdate;
    private String period;
    private double payInAll;
    private double payOutAll;
    private double comissionAll;
    private double currentSum;
    private String percentProfit;
    private String percentProfitYear;
}
