package repository;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.kulikovskiy.trading.investmantanalysistinkoff.InvestmentAnalysisTinkoffService;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository;
import ru.tinkoff.invest.openapi.models.Currency;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = InvestmentAnalysisTinkoffService.class)
public class InstrumentOperationRepositoryTest {

    private final LocalDateTime MIN_DATE = LocalDateTime.of(2000,1,1,1,1,1);
    private final String FIGI = "BBG0016SSV00";
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InstrumentOperationRepository instrumentOperationRepository;

    private InstrumentOperation instrumentOperation;

    @Before
    public void initObjects() {
        instrumentOperation = new InstrumentOperation("1", Currency.RUB, LocalDateTime.of(2000,1,1,1,1,1),
                "test", 12, 1, 12, "done", 0, Currency.RUB, FIGI);
    }

    @Test
    public void checkGetMinDate(){
        this.entityManager.persist(instrumentOperation);
        LocalDateTime minDate = this.instrumentOperationRepository.getMinDate();
        assertThat(minDate.equals(MIN_DATE));
    }

    @Test
    public void checkFindByFigiOrderByDateOperationAsc() {
        this.entityManager.persist(instrumentOperation);
        InstrumentOperation instrumentOperation = this.instrumentOperationRepository.findByFigiOrderByDateOperationAsc(FIGI).get(0);

        assertThat(instrumentOperation.getFigi().equals(FIGI));
        assertThat(instrumentOperation.getCurrency().equals(Currency.RUB));
        assertThat(instrumentOperation.getCommissionCurrency().equals(Currency.RUB));
        assertThat(instrumentOperation.getCourse() == 12);
        assertThat(instrumentOperation.getQuantity() == 1);
        assertThat(instrumentOperation.getStatus().equals("done"));
        assertThat(instrumentOperation.getPayment() == 12);
    }
}