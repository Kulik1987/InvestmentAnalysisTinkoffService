package ru.kulikovskiy.trading.investmantanalysistinkoff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private String brokerAccountId;
    private String brokerAccountType;
    private String firstName;
    private String lastName;
    private String token;
    private boolean closeAccount;

}
