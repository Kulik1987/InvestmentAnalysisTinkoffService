package repository;

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
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.AccountRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = InvestmentAnalysisTinkoffService.class)
public class AccountRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private final String TOKEN = "t.eM0-UnaX9VHGB2iwfvBlexxcRfkQasak-GMGpYzf3aamlOV_m4nIRl_pCs5xbl6GbwMIR5ljCn5vF-8t6aKPWA";
    private final String BROKER_ACCOUNT_TYPE = "TinkoffIis";
    @Test
    public void checkFindByTokenAndBrokerAccountType(){
        this.entityManager.persist(new Account("203933278","TinkoffIis",TOKEN, true, ""));
        Account account = this.accountRepository.findByTokenAndBrokerAccountType(TOKEN, BROKER_ACCOUNT_TYPE);
        assertThat(account.getBrokerAccountId()).isEqualTo("203933278");
    }
}