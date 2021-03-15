package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;

import java.util.List;

@Repository
public interface InstrumentsRepository extends CrudRepository<Instruments, String> {
    @Query(value = "FROM Instruments i WHERE i.type <> :type and EXISTS (" +
            "SELECT 1 FROM InstrumentOperation io WHERE i.figi = io.figi and io.operationType = 'Sell')")
    List<Instruments> getInstrument(@Param("type") String type);
}
