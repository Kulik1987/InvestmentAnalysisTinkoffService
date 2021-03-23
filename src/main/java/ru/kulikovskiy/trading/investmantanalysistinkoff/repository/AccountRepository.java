package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    Account findByTokenAndBrokerAccountType(String token, String brokerAccountType);

    Account findByChatIdAndBrokerAccountType(String token, String brokerAccountType);
}
