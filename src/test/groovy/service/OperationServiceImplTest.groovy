package service


import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.CurrencyOperationsMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.InstrumentOperationsMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.CurrencyOperationRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InvestmentTinkoffService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.OperationsService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.OperationsServiceImpl
import ru.tinkoff.invest.openapi.models.Currency
import spock.lang.Specification

import java.time.LocalDateTime

class OperationServiceImplTest extends Specification {
    def UNSUCCESS_TOKEN = "TestToken"
    def UNSUCCESS_TOKEN_OPERATIONS_EMPTY = "TestToken1"
    def BROKER_ACCOUNT_TYPE = "TinkoffIis"
    def ACCOUNT_ID_IIS = "2039332784"
    def ACCOUNT_ID = "203933278"
    def ACCOUNT_TYPE_IIS = "TinkoffIis"
    def DATE = "2020-07-31T17:04:32.000Z"
    def TO_DATE = "2021-07-31T18:04:32.000Z"
    def operationToCurrency = new Operations(operationType: "PayIn",
            date: DATE,
            isMarginCall: false,
            figi: "BBG000BSJK37",
            quantity: 11,
            price: 12.2,
            payment: 12.2,
            currency: Currency.USD,
            status: "Done",
            id: "1")

    def operationToInstrument = new Operations(operationType: "Buy",
            date: DATE,
            isMarginCall: false,
            figi: "TCS00A1029T9",
            quantity: 10,
            price: 12.12,
            payment: 121.2,
            currency: Currency.USD,
            status: "Done",
            id: "1")

    def instrument = new InstrumentOperation(id: "1",
            currency: Currency.USD,
            dateOperation: LocalDateTime.now(),
            operationType: "Buy",
            course: 12.12,
            quantity: 10,
            payment: 121.2,
            status: "Done",
            commissionCurrency: Currency.USD,
            figi: "TCS00A1029T9"
    )
    def currency = new CurrencyOperation(id: "1",
            currency: Currency.USD,
            dateOperation: LocalDateTime.now(),
            operationType: "Buy",
            course: 12.2,
            quantity: 1,
            payment: 12.2,
            status: "Done",
            commissionCurrency: Currency.USD,
            figi: "TCS00A1029T9"
    )
    def operationsList = new ArrayList<Operations>(Arrays.asList(operationToInstrument, operationToCurrency))
    def response

    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getOperations(_, _, ACCOUNT_ID_IIS, _) >> operationsList
        getOperations(_, _, ACCOUNT_ID, _) >> new ArrayList<Operations>()
    }
    private accountService = Mock(AccountService) {
        getAccountId(TOKEN, _) >> ACCOUNT_ID_IIS
        getAccountId(UNSUCCESS_TOKEN, _) >> ""
        getAccountId(UNSUCCESS_TOKEN_OPERATIONS_EMPTY, _) >> ACCOUNT_ID
    }
    private currencyOperationsMapper = Mock(CurrencyOperationsMapper) {
        toCurrencyOperation(_) >> currency
    }
    private instrumentOperationsMapper = Mock(InstrumentOperationsMapper) {
        toInstrumentOperation(_) >> instrument
    }
    private instrumentOperationRepository = Mock(InstrumentOperationRepository) {
    }
    private currencyOperationRepository = Mock(CurrencyOperationRepository) {
    }

    private OperationsService operationsService = new OperationsServiceImpl(
            investmentTinkoffService: investmentTinkoffService,
            accountService: accountService,
            instrumentOperationRepository: instrumentOperationRepository,
            currencyOperationRepository: currencyOperationRepository,
            currencyOperationsMapper: currencyOperationsMapper,
            instrumentOperationsMapper: instrumentOperationsMapper
    )

    def "get account from token SUCCESS"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, TOKEN, BROKER_ACCOUNT_TYPE)

        then:
        1 * currencyOperationRepository.save(_)
        response.countLoadOperation == 2
    }

    def "get account from token UNSUCCESS AccountId emplty"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, UNSUCCESS_TOKEN, BROKER_ACCOUNT_TYPE)

        then:
        response.errorMessage == "accountId is empty"
    }

    def "get account from token UNSUCCESS Operations emplty"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, UNSUCCESS_TOKEN_OPERATIONS_EMPTY, BROKER_ACCOUNT_TYPE)

        then:
        response.errorMessage == "operationList is empty"
    }
}
