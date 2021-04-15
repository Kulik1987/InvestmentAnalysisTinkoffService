package service


import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Account
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.BuyInstrumentMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.SellInstrumentMapper
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.AveragePositionPrice
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.ExpectedYield
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.PercentageInstrument
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Position
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.CurrencyOperationRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioServiceImpl
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InvestmentTinkoffService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class AnalyzePortfolioServiceImplTest extends Specification {

    def UNSUCCESS_TOKEN = "TestToken"
    def UNSUCCESS_TOKEN_POSITION_EMPTY = "testEmpty"
    def FIRST_NAME = "KULIK"
    def LAST_NAME = "POUL"
    def ACCOUNT_ID_IIS = "2039332784"
    def ACCOUNT_ID = "1234"
    def ACCOUNT_TYPE = "Tinkoff"
    def ACCOUNT_TYPE_IIS = "TinkoffIis"
    def accountIis = new Account(brokerAccountId: ACCOUNT_ID_IIS,
            brokerAccountType: ACCOUNT_TYPE_IIS,
            firstName: FIRST_NAME,
            lastName: LAST_NAME,
            token: TOKEN)
    def payIn = new CurrencyOperation(id: "470419302",
            dateOperation: LocalDateTime.now().minusDays(2),
            operationType: "PayIn",
            course: 0,
            quantity: 0,
            payment: 1001,
            status: "Done",
            commissionValue: 0
    )

    def buyStock = new InstrumentOperation(id: "22529360061",
            dateOperation: LocalDateTime.now().minusDays(1),
            operationType: "Buy",
            course: 1000,
            quantity: 1,
            payment: 1000,
            status: "Done",
            commissionValue: -1,
            figi: "BBG000C3NTN5"
    )
    def sellStock = new InstrumentOperation(id: "22529360061",
            dateOperation: LocalDateTime.now(),
            operationType: "Sell",
            course: 1100,
            quantity: 1,
            payment: 1000,
            status: "Done",
            commissionValue: -2,
            figi: 'BBG000C3NTN5'
    )
    def expectedYield = new ExpectedYield(
            currency: "USD",
            value: 1000
    )
    def averagePositionPrice = new AveragePositionPrice(
            currency: "USD",
            value: 100
    )
    def percantageInstrument = new PercentageInstrument(startDate: LocalDate.of(2020, 10, 01),
            endDdate: LocalDate.of(2021, 02, 18),
            period: 140,
            payInAll: 1001.0,
            payOutAll: 0.0,
            comissionAll: 0.0,
            currentSum: 1100.0,
            percentProfit: "9.89%",
            percentProfitYear: "25.78%"
    )

    def position = new Position(figi: "BBG000C3NTN5",
            ticker: "HOLX",
            isin: "US4364401012",
            instrumentType: "Stock",
            balance: 1,
            lots: 1,
            expectedYield: expectedYield,
            averagePositionPrice: averagePositionPrice,
            name: "Hologic"
    )

    def operationsList = new ArrayList<InstrumentOperation>(Arrays.asList(buyStock, sellStock))
    def figiName = ["BBG000C3NTN5", "Hologic"]
    def response

    private instrumentOperationRepository = Mock(InstrumentOperationRepository) {
        getMinDate() >> LocalDateTime.of(2020, 10, 1, 1, 1)
        findAll() >> operationsList
        getUniqueFigi() >> Collections.singletonList(figiName.toArray())
        findByFigiOrderByDateOperationAsc(_) >> operationsList
    }
    private currencyOperationRepository = Mock(CurrencyOperationRepository) {
        findAll() >> Collections.singletonList(payIn)
    }
    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getPosition(ACCOUNT_ID_IIS, TOKEN) >> Collections.singletonList(position)
        getPosition(ACCOUNT_ID, UNSUCCESS_TOKEN_POSITION_EMPTY) >> new ArrayList<Position>()
    }

    private buyInstrumentMapper = Mock(BuyInstrumentMapper) {

    }
    private sellInstrumentMapper = Mock(SellInstrumentMapper) {

    }
    private accountService = Mock(AccountService) {
        getAccountId(TOKEN, _) >> ACCOUNT_ID_IIS
        getAccountId(UNSUCCESS_TOKEN, _) >> ""
        getAccountId(UNSUCCESS_TOKEN_POSITION_EMPTY, _) >> ACCOUNT_ID
    }

    private AnalyzePortfolioServiceImpl analyzePortfolioServiceImpl = new AnalyzePortfolioServiceImpl(
            sellInstrumentMapper: sellInstrumentMapper,
            buyInstrumentMapper: buyInstrumentMapper,
            investmentTinkoffService: investmentTinkoffService,
            instrumentOperationRepository: instrumentOperationRepository,
            currencyOperationRepository: currencyOperationRepository,
            accountService: accountService
    )

    def "Analize portfolio all SUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(TOKEN, ACCOUNT_TYPE_IIS)
        then:
        response.reportInstrument == percantageInstrument
    }

    def "Analize portfolio all UNSUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(UNSUCCESS_TOKEN, ACCOUNT_TYPE)
        then:
        def e = thrown(NotFoundException)
        e.message == "account is empty"
    }

    def "Analize portfolio all position empty UNSUCCESS"() {
        given:

        when:
        response = analyzePortfolioServiceImpl.getReportAllDayAllInstrument(UNSUCCESS_TOKEN_POSITION_EMPTY, ACCOUNT_TYPE)
        then:
        def e = thrown(NotFoundException)
        e.message == "positions is empty"
    }
}
