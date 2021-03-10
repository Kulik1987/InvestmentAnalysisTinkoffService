package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinMaxDate {
    private LocalDateTime minDate;
    private LocalDateTime maxDate;
}
