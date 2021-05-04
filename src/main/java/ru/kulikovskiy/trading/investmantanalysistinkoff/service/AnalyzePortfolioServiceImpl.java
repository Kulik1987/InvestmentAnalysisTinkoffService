package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import com.hazelcast.core.HazelcastInstance;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.*;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.BuyInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.SellInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.*;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.OperationType;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.StatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static ru.kulikovskiy.trading.DateUtil.getStringFromLocalDateTime;
import static ru.kulikovskiy.trading.HazelcastConst.CURRENCY;
import static ru.kulikovskiy.trading.investmantanalysistinkoff.Const.*;

@Service
public class AnalyzePortfolioServiceImpl implements AnalyzePortfolioService {
    private static final String ALL_PAY_IN = "allPayIn";
    private static final String ALL_PAY_IN_SEPARATE = "allPayInSeparate";

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastInstance;

    String BROKER_TYPE = "TinkoffIis";
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;
    @Autowired
    private BuyInstrumentMapper buyInstrumentMapper;
    @Autowired
    private SellInstrumentMapper sellInstrumentMapper;
    @Autowired
    private OperationsService operationsService;

    private final String FIGI_USD = "BBG0013HGFT4";
    private final String FIGI_EUR = "BBG0013HJJ31";


    @Override
    public TotalReportDto getTotalReport(String token) throws NotFoundException {
        String accountId = getAccountId(token);

        Period period = getPeriodDateAll();
        OperationDto operationDto = getOperations(token, BROKER_TYPE, period.getEndDate(), period.getStartDate(), accountId);

        List<CurrencyOperation> currencyOperations = operationDto.getCurrencyOperationList();
        getCountDayOpenInvest(period, currencyOperations);

        PercentageInstrument percentageInstrument = getPercentageInstrument(token, accountId, period, currencyOperations, ALL_PAY_IN);
        return new TotalReportDto(percentageInstrument);
    }


    @Override
    public TotalReportDto getReportAllDayAllInstrumentSeparatePayIn(String token) throws NotFoundException {
        String accountId = getAccountId(token);

        Period period = getPeriodDateAll();
        OperationDto operationDto = getOperations(token, BROKER_TYPE, period.getEndDate(), period.getStartDate(), accountId);

        List<CurrencyOperation> currencyOperations = operationDto.getCurrencyOperationList();
        getCountDayOpenInvest(period, currencyOperations);

        PercentageInstrument percentageInstrument = getPercentageInstrument(token, accountId, period, currencyOperations, ALL_PAY_IN_SEPARATE);
        return new TotalReportDto(percentageInstrument);
    }

    @Override
    public OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(String token, String ticker) throws NotFoundException {
        String accountId = getAccountId(token);
        Period period = getPeriodDateAll();
        // костыль на баг тинька
        String tickerModify = "";
        if (TCSG.equals(ticker)) {
            tickerModify = TCS;
            hazelcastInstance.getMap(CURRENCY).put(TCS, RUB);
        } else if (TCS.equals(ticker)) {
            tickerModify = ticker;
            hazelcastInstance.getMap(CURRENCY).put(TCS, USD);
        } else {
            tickerModify = ticker;
        }
        ////////////////////////////
        FigiNameDto figiNameDto = getNameInstrumentByFigi(tickerModify, token);

        OperationDto operationDto = getOperationsByFigi(token, BROKER_TYPE, period.getEndDate(), period.getStartDate(), accountId, figiNameDto.getFigi());
        List<InstrumentOperation> instrumentOperationList = operationDto.getInstrumentOperationList();

        List<SellInstrument> sellInstruments = getSellInstruments(instrumentOperationList, tickerModify);
        List<BuyInstrument> buyInstruments = getBuyOperationByFigi(instrumentOperationList);

        TradeInstrument tradeInstrument = getTradeInstrument(figiNameDto.getFigi(), figiNameDto.getName(), sellInstruments, buyInstruments);

        ReportInstrument reportInstrument = setPercantageByInstrument(tradeInstrument.getSellInstrument(), ticker, tradeInstrument.getName());
        return new OneTickerCloseOperationReportDto(reportInstrument);
    }

    private void getCountDayOpenInvest(Period period, List<CurrencyOperation> currencyOperations) {
        LocalDateTime startDate = currencyOperations.stream().map(u -> u.getDateOperation()).min(LocalDateTime::compareTo).get();
        period.setStartDate(startDate);
        period.setDayOpen(DAYS.between(period.getStartDate(), period.getEndDate()));
    }

    private OperationDto getOperationsByFigi(String token, String brokerType, @NotNull LocalDateTime
            maxDate, @NotNull LocalDateTime minDate, @NotNull String accountId, String figi) throws NotFoundException {
        String startPeriod = getStringFromLocalDateTime(minDate);
        String endPeriod = getStringFromLocalDateTime(maxDate);
        return operationsService.getOperationsBetweenDateByFigi(startPeriod, endPeriod, token, brokerType, accountId, figi);
    }

    private FigiNameDto getNameInstrumentByFigi(String ticker, String token) {
        List<Instruments> instruments = investmentTinkoffService.getStocks(token);
        Instruments instrument = instruments.stream().filter(i -> ticker.equals(i.getTicker())).findFirst().get();
        if (instrument == null) {
            instruments = investmentTinkoffService.getBonds(token);
            instrument = instruments.stream().filter(i -> ticker.equals(i.getTicker())).findFirst().get();
            if (instrument == null) {
                instruments = investmentTinkoffService.getEtfs(token);
                instrument = instruments.stream().filter(i -> ticker.equals(i.getTicker())).findFirst().get();
                if (instrument == null) {
                    instruments = investmentTinkoffService.getCurrencies(token);
                    instrument = instruments.stream().filter(i -> ticker.equals(i.getTicker())).findFirst().get();
                }
            }
        }
        return new FigiNameDto(instrument.getFigi(), instrument.getName());
    }

    @NotNull
    private PercentageInstrument getPercentageInstrument(String token, String accountId, Period
            period, List<CurrencyOperation> currencyOperations, String reportType) throws NotFoundException {
        int paySumInRub = (int) getSumPayInRub(currencyOperations);
        int paySumOutRub = (int) getSumPayOutRub(currencyOperations);
        double sumComissionAll = getSumCommissionAll(currencyOperations);

        List<Position> positionList = getPositionList(accountId, token);
        final double[] sumRubPortfolio = {0};
        final double[] sumUsdPortfolio = {0};
        final double[] sumEurPortfolio = {0};

        double allSumRubPortfolio = getAllSumRubPortfolio(positionList, sumRubPortfolio, sumUsdPortfolio, sumEurPortfolio);
        double percentProfit = getPercentProfit(paySumInRub, allSumRubPortfolio);
        int avgDayMoneyOnAccount = 0;
        int dayOpenPortfolio = (int) DAYS.between(currencyOperations.get(currencyOperations.size() - 1).getDateOperation().toLocalDate(), LocalDate.now());

        if (ALL_PAY_IN_SEPARATE.equals(reportType)) {
            avgDayMoneyOnAccount = (int) getDayOpenPortfolioAvg(currencyOperations, paySumInRub);
        } else if (ALL_PAY_IN.equals(reportType)) {
            avgDayMoneyOnAccount = (int) period.getDayOpen();
        }
        double percentProfitYear = getPercentProfitYear(paySumInRub, allSumRubPortfolio, avgDayMoneyOnAccount);

        PercentageInstrument percentageInstrument = new PercentageInstrument(period.getStartDate().toLocalDate(), period.getEndDate().toLocalDate(), String.valueOf(dayOpenPortfolio), String.valueOf(avgDayMoneyOnAccount), paySumInRub, paySumOutRub, sumComissionAll, Math.round(allSumRubPortfolio * 100d) / 100d, String.valueOf(Math.round(percentProfit * 100d) / 100d) + "%", String.valueOf(Math.round(percentProfitYear * 100d) / 100d) + "%");
        return percentageInstrument;
    }

    private String getAccountId(String token) throws NotFoundException {
        List<AccountDto> accounts = investmentTinkoffService.getAccounts(token);
        if ((accounts == null) || (accounts.isEmpty())){
            throw new NotFoundException("account is empty");
        }
        return accounts.stream().filter(a -> BROKER_TYPE.equals(a.getBrokerAccountType())).findFirst().get().getBrokerAccountId();
    }

    @NotNull
    private TradeInstrument getTradeInstrument(String figi, String name, List<SellInstrument> sellInstruments, List<BuyInstrument> buyInstruments) {
        TradeInstrument tradeInstrument = new TradeInstrument();
        tradeInstrument.setFigi(figi);
        tradeInstrument.setName(name);
        if ((!buyInstruments.isEmpty()) && (!sellInstruments.isEmpty())) {
            List<SellInstrumentPercentage> sellInstrumentsWithPercantage = getPercantageList(buyInstruments, sellInstruments);
            tradeInstrument.setSellInstrument(sellInstrumentsWithPercantage);
        }
        return tradeInstrument;
    }

    private List<SellInstrument> getSellInstruments(List<InstrumentOperation> instrumentOperationList, String
            ticker) throws NotFoundException {
        List<SellInstrument> sellInstruments = getSellOperationByFigi(instrumentOperationList);
        if (sellInstruments.isEmpty()) {
            throw new NotFoundException("Sell operation for ticker= " + ticker + " is not found ");
        }
        return sellInstruments;
    }

    private double getPercentProfitYear(double payInRub, double allSumRubPortfolio, double dayOpenPortfolioAvg) {
        return (allSumRubPortfolio / payInRub - 1) * 100 * 365 / dayOpenPortfolioAvg;
    }

    private double getDayOpenPortfolioAvg(List<CurrencyOperation> currencyOperations, double payInRub) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_IN.getDescription().equals(co.getOperationType())).mapToDouble(o -> {
            long days = DAYS.between(o.getDateOperation(), LocalDateTime.now());
            return days * o.getPayment() / payInRub;
        }).sum();
    }

    private double getSumPayInRub(List<CurrencyOperation> currencyOperations) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_IN.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
    }

    private double getSumPayOutRub(List<CurrencyOperation> currencyOperations) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_OUT.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
    }

    private double getSumCommissionAll(List<CurrencyOperation> currencyOperations) {
        return currencyOperations.stream()
                .filter(co -> (OperationType.SERVICE_COMMISSION.getDescription().equals(co.getOperationType()) || (OperationType.BROKER_COMISSION.getDescription().equals(co.getOperationType()))))
                .mapToDouble(CurrencyOperation::getPayment).sum();
    }

    private List<BuyInstrument> getBuyOperationByFigi(List<InstrumentOperation> instrumentOperationList) {
        return instrumentOperationList.stream()
                .filter(o -> OperationType.BUY.getDescription().equals(o.getOperationType())).map(buyInstrumentMapper::getBuyInstrumentFromInstrumentOperation)
                .sorted(Comparator.comparing(BuyInstrument::getStartDate)).collect(Collectors.toList());
    }

    private List<SellInstrument> getSellOperationByFigi(List<InstrumentOperation> instrumentOperationList) {
        return instrumentOperationList.stream().filter(o -> ((OperationType.SELL.getDescription().equals(o.getOperationType())) && (!StatusType.DECLINE.getDescription().equals(o.getStatus()))))
                .map(sellInstrumentMapper::getSellInstrumentFromOperationInstrument).collect(Collectors.toList());
    }

    private Period getPeriodDateAll() {
        Period period = new Period();
        LocalDateTime minDate = LocalDateTime.now().minusYears(5);
        LocalDateTime maxDate = LocalDateTime.now();

        period.setStartDate(minDate);
        period.setEndDate(maxDate);
        return period;
    }

    private OperationDto getOperations(String token, String brokerType, @NotNull LocalDateTime
            maxDate, @NotNull LocalDateTime minDate, @NotNull String accountId) throws NotFoundException {
        String startPeriod = getStringFromLocalDateTime(minDate);
        String endPeriod = getStringFromLocalDateTime(maxDate);
        return operationsService.getOperationsBetweenDate(startPeriod, endPeriod, token, brokerType, accountId);
    }

    private double getCurrentRateInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() + position.getExpectedYield().getValue() / position.getBalance();
    }

    private double getCurrentSumInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() * position.getBalance() + position.getExpectedYield().getValue();
    }

    private ReportInstrument setPercantageByInstrument(List<SellInstrumentPercentage> sellInstrument, String ticker, String name) {
        int quantityAll = sellInstrument.stream()
                .mapToInt(si -> si.getQuantitySell()).sum();
        double averageCountDay = sellInstrument.stream()
                .mapToDouble(si -> (double) si.getCountDay() * si.getQuantitySell() / quantityAll).sum();
        double averageProfit = sellInstrument.stream()
                .mapToDouble(si -> si.getProfit() * si.getQuantitySell() / quantityAll).sum();
        double averagePercent = sellInstrument.stream()
                .mapToDouble(si -> si.getPercentProfit() * si.getQuantitySell() / quantityAll).sum();
        double averagePercentYear = sellInstrument.stream()
                .mapToDouble(si -> si.getPercentProfitYear() * si.getQuantitySell() / quantityAll).sum();

        ReportInstrument reportInstrument = new ReportInstrument();
        reportInstrument.setFigi(ticker);
        reportInstrument.setQuantityAll(quantityAll);
        reportInstrument.setNameInstrument(name);
        reportInstrument.setAverageCountDay(String.valueOf(Math.round(averageCountDay)));
        reportInstrument.setAverageProfit(String.valueOf(Math.round(averageProfit * 100d) / 100d));
        reportInstrument.setAveragePercentProfit(Math.round(averagePercent * 100d) / 100d + "%");
        reportInstrument.setAveragePercentProfitYear(Math.round(averagePercentYear * 100d) / 100d + "%");
        return reportInstrument;
    }

    private List<SellInstrumentPercentage> getPercantageList
            (List<BuyInstrument> buyInstruments, List<SellInstrument> sellInstrumentList) {
        List<SellInstrumentPercentage> sellInstrumentsPercent = new ArrayList<>();
        sellInstrumentList.stream().sorted(Comparator.comparing(SellInstrument::getEndDate)).forEach(sim -> {
            int version = 0;
            do {
                BuyInstrument buyInstrument = buyInstruments.get(0);
                int sellQuantity = sim.getQuantityTemp();
                if (sellQuantity == buyInstrument.getQuantityPortfolio()) {
                    SellInstrumentPercentage sellInstrument = getPercantage(sim, buyInstrument, version);
                    sellInstrument.setQuantitySell(buyInstrument.getQuantityPortfolio());
                    sellInstrument.setVersion(version);
                    sellInstrumentsPercent.add(sellInstrument);

                    buyInstruments.remove(0);
                    sim.setQuantityTemp(0);
                    version++;

                } else if (sellQuantity < buyInstrument.getQuantityPortfolio()) {
                    SellInstrumentPercentage sellInstrument = getPercantage(sim, buyInstrument, version);
                    sellInstrument.setVersion(version);
                    sellInstrumentsPercent.add(sellInstrument);

                    buyInstrument.setQuantityPortfolio(buyInstrument.getQuantityPortfolio() - sellQuantity);
                    buyInstruments.set(0, buyInstrument);

                    sim.setQuantityTemp(0);
                    version++;
                } else {
                    SellInstrumentPercentage sellInstrument = getPercantage(sim, buyInstrument, version);
                    sellInstrument.setQuantitySell(buyInstrument.getQuantityPortfolio());

                    sellInstrumentsPercent.add(sellInstrument);
                    buyInstruments.remove(0);
                    int quantityTemp = sim.getQuantityTemp();
                    sim.setQuantityTemp(quantityTemp - buyInstrument.getQuantityPortfolio());
                    version++;
                }
            } while (sim.getQuantityTemp() > 0);
        });
        return sellInstrumentsPercent;
    }

    private SellInstrumentPercentage getPercantage(SellInstrument sellInstrument, BuyInstrument buyInstrument,
                                                   int version) {
        SellInstrumentPercentage sellInstrumentPercentage = new SellInstrumentPercentage(sellInstrument.getId());
        sellInstrumentPercentage.setQuantitySell(sellInstrument.getQuantitySell());
        sellInstrumentPercentage.setEndDate(sellInstrument.getEndDate());
        sellInstrumentPercentage.setCountDay(sellInstrument.getCountDay());
        sellInstrumentPercentage.setSellCourse(sellInstrument.getSellCourse());

        int betweenDay = (int) DAYS.between(buyInstrument.getStartDate(), sellInstrumentPercentage.getEndDate());
        if (betweenDay == 0) {
            sellInstrumentPercentage.setCountDay(1);
        } else {
            sellInstrumentPercentage.setCountDay(betweenDay);
        }
        double profit = sellInstrumentPercentage.getSellCourse() - buyInstrument.getBuyCourse();
        sellInstrumentPercentage.setProfit(profit);
        double persent = profit / buyInstrument.getBuyCourse() * 100;
        sellInstrumentPercentage.setPercentProfit(persent);

        sellInstrumentPercentage.setPercentProfitYear(persent * (365 / sellInstrumentPercentage.getCountDay()));
        return sellInstrumentPercentage;
    }

    private double getPercentProfit(double payInRub, double allSumRubPortfolio) {
        return (allSumRubPortfolio / payInRub - 1) * 100;
    }

    private double getAllSumRubPortfolio(List<Position> positionList, double[] sumRubPortfolio,
                                         double[] sumUsdPortfolio, double[] sumEurPortfolio) {
        double allSumRubPortfolio;
        Position positionUsd = positionList.stream().filter(position -> FIGI_USD.equals(position.getFigi())).findFirst().orElse(null);
        Position positionEur = positionList.stream().filter(position -> FIGI_EUR.equals(position.getFigi())).findFirst().orElse(null);

        double rateUsd = 0;
        if (positionUsd != null) {
            rateUsd = getCurrentRateInstrument(positionUsd);
        }

        double rateEur = 0;
        if (positionEur != null) {
            rateEur = getCurrentRateInstrument(positionEur);
        }
        positionList.forEach(p -> {
            if ("USD".equals(p.getAveragePositionPrice().getCurrency())) {
                sumUsdPortfolio[0] += getCurrentSumInstrument(p);
            } else if ("RUB".equals(p.getAveragePositionPrice().getCurrency())) {
                sumRubPortfolio[0] += getCurrentSumInstrument(p);
            } else if ("EUR".equals(p.getAveragePositionPrice().getCurrency())) {
                sumEurPortfolio[0] += getCurrentSumInstrument(p);
            }
        });
        allSumRubPortfolio = sumRubPortfolio[0] + sumUsdPortfolio[0] * rateUsd + sumEurPortfolio[0] * rateEur;
        return allSumRubPortfolio;
    }

    private List<Position> getPositionList(String accountId, String token) throws NotFoundException {
        List<Position> positionList = investmentTinkoffService.getPosition(accountId, token);
        if (positionList.isEmpty()) {
            throw new NotFoundException("positions is empty");
        }
        return positionList;
    }
}