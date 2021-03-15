package ru.kulikovskiy.trading.investmantanalysistinkoff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.FigiNameInstrument;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperationByTicker;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InstrumentOperationRepository extends CrudRepository<InstrumentOperation, String> {

    @Query("select min(io.dateOperation) from InstrumentOperation io")
    LocalDateTime getMinDate();

    @Query("select io.figi, i.name from InstrumentOperation io join Instruments i on i.figi = io.figi group by io.figi, i.name")
    List<Object[]> getUniqueFigi();

    List<InstrumentOperation> findByFigiOrderByDateOperationAsc(String figi);

    @Query("select new ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperationByTicker(io, i.ticker, i.name) from InstrumentOperation io join Instruments i on i.figi = io.figi where i.ticker = :ticker and io.status = 'Done'")
    List<InstrumentOperationByTicker> findInstrumentsByTicker(String ticker);
}


