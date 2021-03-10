package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Period {
    private long dayOpen;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
