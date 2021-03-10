package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;

@Repository
public interface InstrumentsRepository extends CrudRepository<Instruments, String> {
}
