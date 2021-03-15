package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import groovy.transform.AutoImplement;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.BuyInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.SellInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.*;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.StatusType;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.CurrencyOperationRepository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentsRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kulikovskiy.trading.DateUtil.getStringFromLocalDateTime;

@Service
public class AnalyzePortfolioServiceImpl implements AnalyzePortfolioService {
    @Autowired
    private InstrumentOperationRepository instrumentOperationRepository;
    @Autowired
    private CurrencyOperationRepository currencyOperationRepository;
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;
    @Autowired
    private BuyInstrumentMapper buyInstrumentMapper;
    @Autowired
    private SellInstrumentMapper sellInstrumentMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OperationsService operationsService;
    @Autowired
    private InstrumentsRepository instrumentsRepository;

    private final String FIGI_USD = "BBG0013HGFT4";

    @Override
    public AllMoneyReportDto getReportAllDayAllInstrument(String token, String brokerType) throws NotFoundException {
        String accountId = getAccount(token, brokerType);

        Period period = getPeriodDateAll();
        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();
        long dayOpenPortfolio = period.getDayOpen();

        getNewOperations(token, brokerType, maxDate, minDate);
        List<CurrencyOperation> currencyOperations = getCurrencyOperations();

        double payInRub = getPayInRub(currencyOperations);
        double payOutRub = getPayOutRub(currencyOperations);
        double comissionAll = getCommissionAll(currencyOperations);

        List<Position> positionList = getPositionList(accountId, token);
        final double[] sumRubPortfolio = {0};
        final double[] sumUsdPortfolio = {0};

        double allSumRubPortfolio = getAllSumRubPortfolio(positionList, sumRubPortfolio, sumUsdPortfolio);
        double percentProfit = getPercentProfit(payInRub, allSumRubPortfolio);
        double percentProfitYear = getPercentProfitYear(dayOpenPortfolio, payInRub, allSumRubPortfolio);

        return new AllMoneyReportDto(new PercentageInstrument(minDate.toLocalDate(), maxDate.toLocalDate(), String.valueOf(dayOpenPortfolio), String.valueOf(dayOpenPortfolio), payInRub, payOutRub, comissionAll, Math.round(allSumRubPortfolio * 100d) / 100d, String.valueOf(Math.round(percentProfit * 100d) / 100d) + "%", String.valueOf(Math.round(percentProfitYear * 100d) / 100d) + "%"));
    }

    @Override
    public AllMoneyReportDto getReportAllDayAllInstrumentSeparatePayIn(String token, String brokerType) throws NotFoundException {
        String accountId = getAccount(token, brokerType);

        Period period = getPeriodDateAll();
        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();
        long dayOpen = period.getDayOpen();

        getNewOperations(token, accountId, maxDate, minDate);

        List<CurrencyOperation> currencyOperations = getCurrencyOperations();

        double payInRub = getPayInRub(currencyOperations);
        double payOutRub = getPayOutRub(currencyOperations);
        double comissionAll = getCommissionAll(currencyOperations);

        List<Position> positionList = getPositionList(accountId, token);
        final double[] sumRubPortfolio = {0};
        final double[] sumUsdPortfolio = {0};

        double allSumRubPortfolio = getAllSumRubPortfolio(positionList, sumRubPortfolio, sumUsdPortfolio);
        double percentProfit = getPercentProfit(payInRub, allSumRubPortfolio);
        double dayOpenPortfolioAvg = getDayOpenPortfolioAvg(currencyOperations, payInRub);
        double percentProfitYear = getPercentProfitYear(payInRub, allSumRubPortfolio, dayOpenPortfolioAvg);

        return new AllMoneyReportDto(new PercentageInstrument(minDate.toLocalDate(), maxDate.toLocalDate(), String.valueOf(dayOpen), String.valueOf(Math.round(dayOpenPortfolioAvg)), payInRub, payOutRub, comissionAll, Math.round(allSumRubPortfolio * 100d) / 100d, Math.round(percentProfit * 100d) / 100d + "%", String.valueOf(Math.round(percentProfitYear * 100d) / 100d) + "%"));
    }

    @Override
    public OneTickerCloseOperationReportDto getReportAllDayByTickerCloseOperation(String token, String brokerType, String ticker) throws NotFoundException {
        String accountId = getAccount(token, brokerType);

        Period period = getPeriodDateAll();
        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();

        getNewOperations(token, accountId, maxDate, minDate);

        List<InstrumentOperationByTicker> instrumentOperationByTickers = getInstrumentOperationByTickers(ticker);
        String figi = instrumentOperationByTickers.get(0).getFigi();
        String name = instrumentOperationByTickers.get(0).getName();

        List<InstrumentOperation> instrumentOperationList = instrumentOperationByTickers.stream().map(iot -> iot.getInstrumentOperation()).collect(Collectors.toList());
        List<SellInstrument> sellInstruments = getSellInstruments(instrumentOperationList, ticker);
        List<BuyInstrument> buyInstruments = getBuyOperationByFigi(instrumentOperationList);

        TradeInstrument tradeInstrument = getTradeInstrument(figi, name, sellInstruments, buyInstruments);

        ReportInstrument reportInstrument = setPercantageByInstrument(tradeInstrument.getSellInstrument(), tradeInstrument.getFigi(), tradeInstrument.getName());
        return new OneTickerCloseOperationReportDto(reportInstrument);
    }

    @Override
    public AllTickerCloseOperationReportDto getAllTickerCloseOperationReportDto(String token, String brokerType) throws NotFoundException {
        String accountId = getAccount(token, brokerType);

        Period period = getPeriodDateAll();
        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();

        getNewOperations(token, accountId, maxDate, minDate);

        List<Instruments> instruments = instrumentsRepository.getInstrument("Currency");
        List<ReportInstrument> reportInstruments = instruments.stream().map(i -> {
            List<InstrumentOperationByTicker> instrumentOperationByTickers = instrumentOperationRepository.findInstrumentsByTicker(i.getTicker());
            List<InstrumentOperation> instrumentOperationList = instrumentOperationByTickers.stream().map(iot -> iot.getInstrumentOperation()).collect(Collectors.toList());
            List<SellInstrument> sellInstruments = null;
            try {
                sellInstruments = getSellInstruments(instrumentOperationList, i.getTicker());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            List<BuyInstrument> buyInstruments = getBuyOperationByFigi(instrumentOperationList);

            TradeInstrument tradeInstrument = getTradeInstrument(i.getFigi(), i.getName(), sellInstruments, buyInstruments);

            return setPercantageByInstrument(tradeInstrument.getSellInstrument(), tradeInstrument.getFigi(), tradeInstrument.getName());
        }).collect(Collectors.toList());
        double allSumProfit = reportInstruments.stream().mapToDouble(i -> i.getQuantityAll()*Double.valueOf(i.getAverageProfit())).sum();
        AllTickerCloseOperationReportDto allTickerCloseOperationReportDto = new AllTickerCloseOperationReportDto(reportInstruments, String.valueOf(allSumProfit))
                ;
        return allTickerCloseOperationReportDto;
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

    private List<SellInstrument> getSellInstruments(List<InstrumentOperation> instrumentOperationList, String ticker) throws NotFoundException {
        List<SellInstrument> sellInstruments = getSellOperationByFigi(instrumentOperationList);
        if (sellInstruments.isEmpty()) {
            throw new NotFoundException("Sell operation for ticker= " + ticker + " is not found ");
        }
        return sellInstruments;
    }

    private List<InstrumentOperationByTicker> getInstrumentOperationByTickers(String ticker) throws NotFoundException {
        List<InstrumentOperationByTicker> instrumentOperationByTickers = instrumentOperationRepository.findInstrumentsByTicker(ticker);
        if ((instrumentOperationByTickers == null) || (instrumentOperationByTickers.stream().count() == 0)) {
            throw new NotFoundException("instrument for ticker= " + ticker + ", is not found or operation for this ticker is not found");
        }
        return instrumentOperationByTickers;
    }

    private double getPercentProfitYear(double payInRub, double allSumRubPortfolio, double dayOpenPortfolioAvg) {
        return (allSumRubPortfolio / payInRub - 1) * 100 * 365 / dayOpenPortfolioAvg;
    }

    private double getDayOpenPortfolioAvg(List<CurrencyOperation> currencyOperations, double payInRub) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_IN.getDescription().equals(co.getOperationType())).mapToDouble(o -> {
            long days = ChronoUnit.DAYS.between(o.getDateOperation(), LocalDateTime.now());
            return days * o.getPayment() / payInRub;
        }).sum();
    }

    private List<CurrencyOperation> getCurrencyOperations() throws NotFoundException {
        Iterable<CurrencyOperation> currencyOperationIterable = currencyOperationRepository.findAll();
        if (currencyOperationIterable == null) {
            throw new NotFoundException("operation not found between this date");
        }
        List<CurrencyOperation> currencyOperations = new ArrayList<>();
        currencyOperationIterable.forEach(currencyOperations::add);
        return currencyOperations;
    }

    private List<Instruments> getInstruments() throws NotFoundException {
        Iterable<Instruments> instruments = instrumentsRepository.findAll();
        if (instruments == null) {
            throw new NotFoundException("operation not found between this date");
        }
        List<Instruments> instrumentsResponse = new ArrayList<>();
        instruments.forEach(instrumentsResponse::add);
        return instrumentsResponse;
    }

    private double getPayInRub(List<CurrencyOperation> currencyOperations) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_IN.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
    }

    private double getPayOutRub(List<CurrencyOperation> currencyOperations) {
        return currencyOperations.stream().filter(co -> OperationType.PAY_OUT.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
    }

    private double getCommissionAll(List<CurrencyOperation> currencyOperations) {
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

    private String getAccount(String token, String brokerType) throws NotFoundException {
        String accountId = accountService.getAccountId(token, brokerType);
        if (StringUtil.isEmpty(accountId)) {
            throw new NotFoundException("account is empty");
        }
        return accountId;
    }

    private Period getPeriodDateAll() {
        Period period = new Period();
        LocalDateTime minDate = instrumentOperationRepository.getMinDate();
        LocalDateTime maxDate = LocalDateTime.now();

        period.setStartDate(minDate);
        period.setEndDate(maxDate);
        period.setDayOpen(ChronoUnit.DAYS.between(minDate, maxDate));
        return period;
    }

    private void getNewOperations(String token, String brokerType, LocalDateTime maxDate, LocalDateTime minDate) {
        if (maxDate.toLocalDate().isBefore(LocalDate.now())) {
            String startPeriod = getStringFromLocalDateTime(maxDate);
            String endPeriod = getStringFromLocalDateTime(minDate);
            try {
                operationsService.getOperationsBetweenDate(startPeriod, endPeriod, token, brokerType);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private double getCurrentRateInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() + position.getExpectedYield().getValue() / position.getBalance();
    }

    private double getCurrentSumInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() * position.getBalance() + position.getExpectedYield().getValue();
    }

    private ReportInstrument setPercantageByInstrument(List<SellInstrumentPercentage> sellInstrument, String figi, String name) {
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
        reportInstrument.setFigi(figi);
        reportInstrument.setQuantityAll(quantityAll);
        reportInstrument.setNameInstrument(name);
        reportInstrument.setAverageCountDay(String.valueOf(Math.round(averageCountDay)));
        reportInstrument.setAverageProfit(String.valueOf(Math.round(averageProfit * 100d) / 100d));
        reportInstrument.setAveragePercentProfit(Math.round(averagePercent * 100d) / 100d + "%");
        reportInstrument.setAveragePercentProfitYear(Math.round(averagePercentYear * 100d) / 100d + "%");
        return reportInstrument;
    }

    private List<SellInstrumentPercentage> getPercantageList(List<BuyInstrument> buyInstruments, List<SellInstrument> sellInstrumentList) {
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

    private SellInstrumentPercentage getPercantage(SellInstrument sellInstrument, BuyInstrument buyInstrument, int version) {
        SellInstrumentPercentage sellInstrumentPercentage = new SellInstrumentPercentage(sellInstrument.getId());
        sellInstrumentPercentage.setQuantitySell(sellInstrument.getQuantitySell());
        sellInstrumentPercentage.setEndDate(sellInstrument.getEndDate());
        sellInstrumentPercentage.setCountDay(sellInstrument.getCountDay());
        sellInstrumentPercentage.setSellCourse(sellInstrument.getSellCourse());

        int betweenDay = (int) ChronoUnit.DAYS.between(buyInstrument.getStartDate(), sellInstrumentPercentage.getEndDate());
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

    private double getPercentProfitYear(long dayOpenPortfolio, double payInRub, double allSumRubPortfolio) {
        return (allSumRubPortfolio / payInRub - 1) * 100 * 365 / dayOpenPortfolio;
    }

    private double getPercentProfit(double payInRub, double allSumRubPortfolio) {
        return (allSumRubPortfolio / payInRub - 1) * 100;
    }

    private double getAllSumRubPortfolio(List<Position> positionList, double[] sumRubPortfolio, double[] sumUsdPortfolio) {
        double allSumRubPortfolio;
        Position positionUsd = positionList.stream().filter(position -> FIGI_USD.equals(position.getFigi())).findFirst().orElse(null);
        if (positionUsd == null) {
            positionList.forEach(p -> sumRubPortfolio[0] += getCurrentSumInstrument(p));
            allSumRubPortfolio = sumRubPortfolio[0];
        } else {
            double rateUsd = getCurrentRateInstrument(positionUsd);
            positionList.forEach(p -> {
                if ("USD".equals(p.getAveragePositionPrice().getCurrency())) {
                    sumUsdPortfolio[0] += getCurrentSumInstrument(p);
                } else if ("RUB".equals(p.getAveragePositionPrice().getCurrency())) {
                    sumRubPortfolio[0] += getCurrentSumInstrument(p);
                }
            });
            allSumRubPortfolio = sumRubPortfolio[0] + sumUsdPortfolio[0] * rateUsd;
        }
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