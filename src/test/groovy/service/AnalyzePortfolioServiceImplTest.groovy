package service


import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationDto
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.BuyInstrumentMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.SellInstrumentMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.*
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioServiceImpl
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InvestmentTinkoffService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.OperationsService
import ru.tinkoff.invest.openapi.models.Currency
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class AnalyzePortfolioServiceImplTest extends Specification {
    def TOKEN = "Token"
    def UNSUCCESS_TOKEN = "TestToken"
    def UNSUCCESS_TOKEN_POSITION_EMPTY = "testEmpty"

    def ACCOUNT_ID_IIS = "12345"
    def ACCOUNT_ID = "1234"
    def ACCOUNT_TYPE = "Tinkoff"
    def ACCOUNT_TYPE_IIS = "TinkoffIis"
    def FIGI = "BBG0047315Y7"

    def instrumentOperation = new InstrumentOperation(id: "22670232279",
            currency: Currency.RUB,
            dateOperation: LocalDateTime.of(2021, 2, 20,10,23),
            operationType: "Buy",
            course: 200.0,
            quantity: 120,
            payment: 22000.0,
            status: "Done",
            commissionValue: -4.07,
            commissionCurrency: Currency.RUB,
            figi: FIGI
    )

    def currenсyOperation = new CurrencyOperation(
            id: "741987406",
            currency: Currency.RUB,
            dateOperation: LocalDateTime.of(2021, 1, 21,12,23),
            operationType: "PayIn",
            course: 0,
            quantity: 0,
            payment: 22000.0,
            status: "Done",
            commissionValue: 0
    )

    def currenсyOperationSep = new CurrencyOperation(
            id: "741987406",
            currency: Currency.RUB,
            dateOperation: LocalDateTime.of(2020, 10, 21,12,23),
            operationType: "PayIn",
            course: 0,
            quantity: 0,
            payment: 2000.0,
            status: "Done",
            commissionValue: 0
    )

    def expectedYield = new ExpectedYield(
            currency: "RUB",
            value: 2000
    )
    def averagePositionPrice = new AveragePositionPrice(
            currency: "RUB",
            value: 220
    )


    def position = new Position(figi: FIGI,
            ticker: "SBERP",
            isin: "RU0009029557",
            instrumentType: "Stock",
            balance: 120,
            lots: 120,
            expectedYield: expectedYield,
            averagePositionPrice: averagePositionPrice,
            name: "Сбербанк России - привилегированные акции"
    )

    def percantageInstrumentSeparate = new PercentageInstrument(startDate: LocalDate.of(2020, 10, 21),
            endDdate: LocalDate.now(),
            period: 191,
            periodAvg: 105,
            payInAll: 24000.0,
            payOutAll: 0.0,
            comissionAll: 0.0,
            currentSum: 28400.0,
            percentProfit: "18.33%",
            percentProfitYear: "63.73%"
    )
    def percantageInstrument = new PercentageInstrument(startDate: LocalDate.of(2020, 10, 21),
            endDdate: LocalDate.now(),
            period: 191,
            periodAvg: 190,
            payInAll: 24000.0,
            payOutAll: 0.0,
            comissionAll: 0.0,
            currentSum: 28400.0,
            percentProfit: "18.33%",
            percentProfitYear: "35.22%"
    )
    def accountDto = new AccountDto(brokerAccountId: ACCOUNT_ID,
            brokerAccountType: ACCOUNT_TYPE)
    def accountIisDto = new AccountDto(brokerAccountId: ACCOUNT_ID_IIS,
            brokerAccountType: ACCOUNT_TYPE_IIS)



    def instrumentOperationList = Collections.singletonList(instrumentOperation)
    def currencyOperationList = new ArrayList<>(Arrays.asList(currenсyOperation, currenсyOperationSep))
    def operationDto = new OperationDto(currencyOperationList: currencyOperationList,
            instrumentOperationList: instrumentOperationList,
            countLoadOperation: 2
    )
    def response


    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getAccounts(TOKEN) >> new ArrayList<AccountDto>(Arrays.asList(accountDto, accountIisDto))
        getAccounts(UNSUCCESS_TOKEN) >> new ArrayList<AccountDto>()
        getAccounts(UNSUCCESS_TOKEN_POSITION_EMPTY) >> new ArrayList<AccountDto>(Arrays.asList(accountDto, accountIisDto))
        getPosition(ACCOUNT_ID_IIS, TOKEN) >> Collections.singletonList(position)
        getPosition(_, UNSUCCESS_TOKEN_POSITION_EMPTY) >> new ArrayList<Position>()
    }

    private operationsService = Mock(OperationsService) {
        getOperationsBetweenDate(_, _, TOKEN, ACCOUNT_TYPE_IIS, ACCOUNT_ID_IIS) >> operationDto
        getOperationsBetweenDate(_, _, UNSUCCESS_TOKEN_POSITION_EMPTY, ACCOUNT_TYPE_IIS, ACCOUNT_ID_IIS) >> operationDto
    }

    private buyInstrumentMapper = Mock(BuyInstrumentMapper) {

    }
    private sellInstrumentMapper = Mock(SellInstrumentMapper) {

    }

    private AnalyzePortfolioServiceImpl analyzePortfolioServiceImpl = new AnalyzePortfolioServiceImpl(
            sellInstrumentMapper: sellInstrumentMapper,
            buyInstrumentMapper: buyInstrumentMapper,
            investmentTinkoffService: investmentTinkoffService,
            operationsService: operationsService
    )

    def "getReportAllDayAllInstrument SUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(TOKEN)
        then:
        response.reportInstrument == percantageInstrument
    }

    def "Analize portfolio all UNSUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(UNSUCCESS_TOKEN)
        then:
        def e = thrown(NotFoundException)
        e.message == "account is empty"
    }

    def "Analize portfolio all position empty UNSUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(UNSUCCESS_TOKEN_POSITION_EMPTY)
        then:
        def e = thrown(NotFoundException)
        e.message == "positions is empty"
    }

    def "getReportAllDayAllInstrumentSeparatePayIn SUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrumentSeparatePayIn(TOKEN)
        then:
        response.reportInstrument == percantageInstrumentSeparate
    }
}