package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@Table(name = "account", schema ="investment")
@NoArgsConstructor
public class Account {
    @Id
    private String brokerAccountId;
    private String brokerAccountType;
    private String firstName;
    private String lastName;
    private String token;
    private boolean closeAccount;

    public Account(String brokerAccountId, String brokerAccountType) {
        this.brokerAccountId = brokerAccountId;
        this.brokerAccountType = brokerAccountType;
    }

    public Account(String brokerAccountId, String brokerAccountType, String firstName, String lastName, String token) {
        this.brokerAccountId = brokerAccountId;
        this.brokerAccountType = brokerAccountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
    }
}
