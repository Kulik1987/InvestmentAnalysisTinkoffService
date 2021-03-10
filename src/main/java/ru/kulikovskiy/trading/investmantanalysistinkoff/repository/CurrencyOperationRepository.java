package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation;

import java.util.List;

@Repository
public interface CurrencyOperationRepository extends CrudRepository<CurrencyOperation, String> {

}
