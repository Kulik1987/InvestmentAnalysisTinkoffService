package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.Getter;

@Getter
public enum OperationType {
    SELL("Sell"),
    PAY_OUT("PayOut"),
    PAY_IN("PayIn"),
    BUY("Buy"),
    BROKER_COMISSION("BrokerCommission"),
    COUPON("Coupon"),
    DIVIDEND("Dividend"),
    SERVICE_COMMISSION("ServiceCommission"),
    TAX_BACK("TaxBack"),
    TAX_COUPON("TaxCoupon"),
    TAX_DIVIDEND("TaxDividend"),
    CURRENCY("Currency");
    private String description;

    OperationType(String description) {
        this.description = description;
    }
}
