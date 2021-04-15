package service

import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentsRepository
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InstrumentsService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InstrumentsServiceImpl
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InvestmentTinkoffService
import spock.lang.Specification

class InstrumentServiceImplTest extends Specification {

    def UNSUCCESS_TOKEN = "TestToken"
    def instrumentStock = new Instruments(figi: "BBG000CTQBF3",
            ticker: "SBUX",
            isin: "US8552441094",
            minPriceIncrement: 1,
            lot: 0.01,
            currency: "USD",
            name: "Starbucks Corporation",
            type: "Stock")
    def instrumentCurrency = new Instruments(figi: "BBG0013HGFT4",
            ticker: "USD000UTSTOM",
            isin: "USD000UTSTOM",
            minPriceIncrement: 1000,
            lot: 0.0025,
            currency: "RUB",
            name: "Доллар США",
            type: "Currency")

    def instrumentEtf = new Instruments(figi: "BBG000CTQBF3",
            ticker: "RU000A101X76",
            isin: "RU000A101X76",
            minPriceIncrement: 1,
            lot: 0.002,
            currency: "RUB",
            name: "Тинькофф iMOEX",
            type: "Etf")

    def instrumentBonds = new Instruments(figi: "BBG00T22WKV5",
            ticker: "RU000A101KT1",
            isin: "RU000A101KT1",
            minPriceIncrement: 1,
            lot: 0.01,
            currency: "RUB",
            name: "ОФЗ 29013",
            type: "Bond")

    def bondList = Collections.singletonList(instrumentBonds)
    def currencyList = Collections.singletonList(instrumentCurrency)
    def stockList = Collections.singletonList(instrumentStock)
    def etfList = Collections.singletonList(instrumentEtf)
    def response

    private investmentTinkoffService = Mock(InvestmentTinkoffService) {
        getStocks(TOKEN) >> stockList
        getEtfs(TOKEN) >> etfList
        getCurrencies(TOKEN) >> currencyList
        getBonds(TOKEN) >> bondList
        getStocks(UNSUCCESS_TOKEN) >> new ArrayList<Instruments>()
        getEtfs(UNSUCCESS_TOKEN) >> new ArrayList<Instruments>()
        getCurrencies(UNSUCCESS_TOKEN) >> new ArrayList<Instruments>()
        getBonds(UNSUCCESS_TOKEN) >> new ArrayList<Instruments>()
    }

    private instrumentRepository = Mock(InstrumentsRepository) {
    }
    private InstrumentsService instrumentsService = new InstrumentsServiceImpl(
            investmentTinkoffService: investmentTinkoffService,
            instrumentsRepository: instrumentRepository
    )

    def "load instruments SUCCESS"() {
        given:

        when:
        response = instrumentsService.getInstruments(TOKEN)
        then:
        4 * instrumentRepository.save(_)
        response.countBond == 1
        response.countCurrency == 1
        response.countEtf == 1
        response.countStock == 1

    }

    def "load instruments UNSUCCESS"() {
        given:

        when:
        response = instrumentsService.getInstruments(UNSUCCESS_TOKEN)
        then:
        def e = thrown(NotFoundException)
        e.message == "please load instrument from Tinkoff investment"
    }
}
