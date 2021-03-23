package ru.kulikovskiy.trading.investmantanalysistinkoff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String token;
    private boolean closeAccount;
    private String chatId;
}
