package ru.kulikovskiy.trading.investmantanalysistinkoff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @JsonProperty("brokerAccountType")
    private String brokerAccountType;
    @JsonProperty("brokerAccountId")
    private String brokerAccountId;
}
