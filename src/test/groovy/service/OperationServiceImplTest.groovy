package service

import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.CurrencyOperationsMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.InstrumentOperationsMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.CurrencyOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations
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
    def DATE = "2020-07-31T17:04:32.000Z"
    def TO_DATE = "2021-07-31T18:04:32.000Z"
    def TOKEN = "Test_Token"
    def BROKER_TYPE = "TinkoffIis"
    def FIGI = "TestFigi"
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
    def operationsFigiList = new ArrayList<Operations>(Arrays.asList(operationToInstrument))
    def response

    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getOperations(_, _, ACCOUNT_ID_IIS, UNSUCCESS_TOKEN_OPERATIONS_EMPTY) >> new ArrayList<Operations>()
        getOperations(_, _, ACCOUNT_ID_IIS,TOKEN) >> operationsList
        getOperationsByFigi(_, _, ACCOUNT_ID_IIS,TOKEN, FIGI) >> operationsFigiList
    }
    private currencyOperationsMapper = Mock(CurrencyOperationsMapper) {
        toCurrencyOperation(_ as Operations) >> currency
    }
    private instrumentOperationsMapper = Mock(InstrumentOperationsMapper) {
        toInstrumentOperation(_) >> instrument
    }

    private OperationsService operationsService = new OperationsServiceImpl(
            investmentTinkoffService: investmentTinkoffService,
            currencyOperationsMapper: currencyOperationsMapper,
            instrumentOperationsMapper: instrumentOperationsMapper
    )

    def "getOperationsBetweenDate SUCCESS"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, TOKEN , BROKER_TYPE, ACCOUNT_ID_IIS)

        then:
        response.countLoadOperation == 2
    }

    def "getOperationsBetweenDate UNSUCCESS AccountId emplty"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, UNSUCCESS_TOKEN, BROKER_ACCOUNT_TYPE, "")

        then:
        response.errorMessage == "accountId is empty"
    }

    def "getOperationsBetweenDate UNSUCCESS Operations emplty"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDate(DATE, TO_DATE, UNSUCCESS_TOKEN_OPERATIONS_EMPTY, BROKER_ACCOUNT_TYPE,ACCOUNT_ID_IIS)

        then:
        response.errorMessage == "operationList is empty"
    }

    def "getOperationsBetweenDateByFigi SUCCESS"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDateByFigi(DATE, TO_DATE, TOKEN , BROKER_TYPE, ACCOUNT_ID_IIS, FIGI)

        then:
        response.countLoadOperation == 1
    }

    def "getOperationsBetweenDateByFigi UNSUCCESS AccountId emplty"() {
        given:

        when:
        response = operationsService.getOperationsBetweenDateByFigi(DATE, TO_DATE, UNSUCCESS_TOKEN, BROKER_ACCOUNT_TYPE, "",FIGI)

        then:
        response.errorMessage == "accountId is empty"
    }


}
