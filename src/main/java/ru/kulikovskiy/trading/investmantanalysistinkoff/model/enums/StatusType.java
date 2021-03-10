package ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums;

import lombok.Getter;

@Getter
public enum StatusType {
    DECLINE("Decline");
    private String description;

    StatusType(String description) {
        this.description = description;
    }
}
