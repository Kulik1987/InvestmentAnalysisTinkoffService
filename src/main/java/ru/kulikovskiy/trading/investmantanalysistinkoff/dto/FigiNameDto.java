package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FigiNameDto {
    private String figi;
    private String name;
}