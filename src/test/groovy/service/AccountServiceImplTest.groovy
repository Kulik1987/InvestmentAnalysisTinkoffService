package service

import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.AccountRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountServiceImpl
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InvestmentTinkoffService
import spock.lang.Specification

class AccountServiceImplTest extends Specification {

    def FIRST_NAME = "KULIK"
    def LAST_NAME = "POUL"

    def ACCOUNT_ID = "2039206103"
    def ACCOUNT_ID_IIS = "2039332784"
    def ACCOUNT_TYPE = "Tinkoff"
    def ACCOUNT_TYPE_IIS = "TinkoffIis"
    def accountBroker = new Account(brokerAccountId: ACCOUNT_ID,
            brokerAccountType: ACCOUNT_TYPE,
            firstName: FIRST_NAME,
            lastName: LAST_NAME,
            token: TOKEN)
    def accountIis = new Account(brokerAccountId: ACCOUNT_ID_IIS,
            brokerAccountType: ACCOUNT_TYPE_IIS,
            firstName: FIRST_NAME,
            lastName: LAST_NAME,
            token: TOKEN)
    def accountDto = new AccountDto(brokerAccountId: ACCOUNT_ID,
    brokerAccountType: ACCOUNT_TYPE)

    def response

    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getAccounts("testNull") >> null
        getAccounts(TOKEN as String) >> Collections.singletonList(accountDto)
    }
    private accountRepository = Mock(AccountRepository) {
        findByTokenAndBrokerAccountType(TOKEN, ACCOUNT_TYPE) >> accountBroker
        findByTokenAndBrokerAccountType(TOKEN, ACCOUNT_TYPE_IIS) >> accountIis
    }

    private AccountService accountService = new AccountServiceImpl(
            investmentTinkoffService: investmentTinkoffService,
            accountRepository: accountRepository
    )


    def "get account from token SUCCESS"() {
        given:

        when:
        response = accountService.saveClientAccount(TOKEN)


        then:
        1* accountRepository.save(_)
        response.size() == 1
        response.contains(new AccountDto(brokerAccountType: accountBroker.brokerAccountType, brokerAccountId: accountBroker.brokerAccountId))
    }

    def "Name"() {
    }

    def "get account from token UNSUCCESS"() {
        given:

        when:
            response = accountService.saveClientAccount("testNull")

        then:
            def e = thrown(NotFoundException)
            e.message == "accounts not found in the Tinkoff investment"
    }
}
