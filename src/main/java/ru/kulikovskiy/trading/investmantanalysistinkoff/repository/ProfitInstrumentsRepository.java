package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.ProfitInstrument;

@Repository
public interface ProfitInstrumentsRepository extends CrudRepository<ProfitInstrument, String> {
}
