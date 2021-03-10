package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.ReportAllDayAllMoneyResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.ReportAllDayBreakUpInstrumentResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.BuyInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.SellInstrumentMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.*;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.StatusType;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.CurrencyOperationRepository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    private final String FIGI_USD = "BBG0013HGFT4";

    public ReportAllDayAllMoneyResponse getReportAllDayAllInstrument(String token, String brokerType) throws NotFoundException {
        String accountId = accountService.getAccountId(token, brokerType);
        if (StringUtil.isEmpty(accountId)) {
            throw new NotFoundException("account is empty");
        }
        Period period = getPeriodDateAll();

        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();
        long dayOpenPortfolio = period.getDayOpen();

        getNewOperations(token, accountId, maxDate, minDate);

        Iterable<CurrencyOperation> currencyOperationIterable = currencyOperationRepository.findAll();
        if (currencyOperationIterable == null) {
            throw new NotFoundException("operation not found between this date");
        }
        List<CurrencyOperation> currencyOperations = new ArrayList<>();
        currencyOperationIterable.forEach(currencyOperations::add);

        double payInRub = currencyOperations.stream().filter(co -> OperationType.PAY_IN.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
        double payOutRub = currencyOperations.stream().filter(co -> OperationType.PAY_OUT.getDescription().equals(co.getOperationType())).mapToDouble(CurrencyOperation::getPayment).sum();
        double comissionAll = currencyOperations.stream()
                .filter(co -> (OperationType.SERVICE_COMMISSION.getDescription().equals(co.getOperationType()) || (OperationType.BROKER_COMISSION.getDescription().equals(co.getOperationType()))))
                .mapToDouble(CurrencyOperation::getPayment).sum();

        List<Position> positionList = investmentTinkoffService.getPosition(accountId, token);
        if (positionList.isEmpty()) {
            throw new NotFoundException("positions is empty");
        }
        AtomicReference<Double> sumRubPortfolio = new AtomicReference<>((double) 0);
        AtomicReference<Double> sumUsdPortfolio = new AtomicReference<>((double) 0);
        double allSumRubPortfolio = 0;

        Position positionUsd = positionList.stream().filter(position -> FIGI_USD.equals(position.getFigi())).findFirst().orElse(null);
        if (positionUsd == null) {
            positionList.forEach(p -> sumRubPortfolio.set(sumRubPortfolio.get() + getCurrentSumInstrument(p)));
            allSumRubPortfolio = sumRubPortfolio.get();
        } else {
            double rateUsd = getCurrentRateInstrument(positionUsd);
            positionList.forEach(p -> {
                if ("USD".equals(p.getAveragePositionPrice().getCurrency())) {
                    sumUsdPortfolio.set(sumUsdPortfolio.get() + getCurrentSumInstrument(p));
                } else if ("RUB".equals(p.getAveragePositionPrice().getCurrency())) {
                    sumRubPortfolio.set(sumRubPortfolio.get() + getCurrentSumInstrument(p));
                }
            });
            allSumRubPortfolio = sumRubPortfolio.get() + sumUsdPortfolio.get() * rateUsd;
        }

        double percentProfit = (allSumRubPortfolio / payInRub - 1) * 100;
        double percentProfitYear = (allSumRubPortfolio / payInRub - 1) * 100 * 365 / dayOpenPortfolio;

        return new ReportAllDayAllMoneyResponse(new PercentageInstrument(minDate.toLocalDate(), maxDate.toLocalDate(), String.valueOf(dayOpenPortfolio), payInRub, payOutRub, comissionAll, Math.round(allSumRubPortfolio * 100d) / 100d, String.valueOf(Math.round(percentProfit * 100d) / 100d) + "%", String.valueOf(Math.round(percentProfitYear * 100d) / 100d) + "%"));
    }

    @Override
    //Unused, change this method to all operation for FIGI  with profit
    public ReportAllDayBreakUpInstrumentResponse getReportAllDayBreakUpInstrument(String token, String brokerType) throws NotFoundException {
        String accountId = accountService.getAccountId(token, brokerType);
        if (StringUtil.isEmpty(token)) {
            throw new NotFoundException("account is empty");
        }
        Period period = getPeriodDateAll();

        LocalDateTime maxDate = period.getEndDate();
        LocalDateTime minDate = period.getStartDate();

        getNewOperations(token, accountId, maxDate, minDate);

        List<Object[]> figiNameInstruments = instrumentOperationRepository.getUniqueFigi();

        List<TradeInstrument> tradeInstruments = figiNameInstruments.stream().map(figiNameInstrument -> {
            String figi = (String) figiNameInstrument[0];
            String name = (String) figiNameInstrument[1];
            List<InstrumentOperation> instrumentOperationsList = instrumentOperationRepository.findByFigiOrderByDateOperationAsc(figi);

            List<BuyInstrument> buyInstrumentList = getBuyOperationByFigi(instrumentOperationsList);
            Map<String, SellInstrument> sellInstrumentMap = getSellOperationByFigi(instrumentOperationsList);
            TradeInstrument tradeInstrument = new TradeInstrument();
            if ((!buyInstrumentList.isEmpty()) && (!sellInstrumentMap.isEmpty())) {
                Map<String, SellInstrument> sellInstrumentsWithPercantage = getPercantageList(buyInstrumentList, sellInstrumentMap);

                tradeInstrument.setFigi(figi);
                tradeInstrument.setName(name);
                tradeInstrument.setBuyInstruments(buyInstrumentList);
                tradeInstrument.setSellInstrument(sellInstrumentsWithPercantage);
            }
            return tradeInstrument;
        }).collect(Collectors.toList());

        if (!tradeInstruments.isEmpty() ) {
            List<ReportInstrument> reportInstrumentList = tradeInstruments.stream().filter(ti -> ti.getFigi() != null)
                    .map(tradeInstrument -> setPercantageByInstrument(tradeInstrument.getSellInstrument(), tradeInstrument.getFigi(), tradeInstrument.getName())).collect(Collectors.toList());
            return new ReportAllDayBreakUpInstrumentResponse(reportInstrumentList);
        } else {
            return new ReportAllDayBreakUpInstrumentResponse("trade instruments is empty");
        }
    }

    private ReportInstrument setPercantageByInstrument(Map<String, SellInstrument> sellInstrument, String figi, String name) {
        int quantityAll = sellInstrument.entrySet().stream()
                .mapToInt(si -> si.getValue().getQuantitySell()).sum();
        double averageProfit = sellInstrument.entrySet().stream()
                .mapToDouble(si -> si.getValue().getProfit() * si.getValue().getQuantitySell() / quantityAll).sum();
        double averagePercentYear = sellInstrument.entrySet().stream()
                .mapToDouble(si -> si.getValue().getPercentProfitYear() * si.getValue().getQuantitySell() / quantityAll).sum();
        double averagePercent = sellInstrument.entrySet().stream()
                .mapToDouble(si -> si.getValue().getPercentProfit() * si.getValue().getQuantitySell() / quantityAll).sum();

        ReportInstrument reportInstrument = new ReportInstrument();
        reportInstrument.setFigi(figi);
        reportInstrument.setNameInstrument(name);
        reportInstrument.setAverageProfit(String.valueOf(Math.round(averageProfit * 100d) / 100d));
        reportInstrument.setAveragePercentProfit(Math.round(averagePercent * 100d) / 100d + "%");
        reportInstrument.setAveragePercentProfitYear(Math.round(averagePercentYear * 100d) / 100d + "%");
        return reportInstrument;
    }

    private Map<String, SellInstrument> getPercantageList(List<BuyInstrument> buyInstruments, Map<String, SellInstrument> sellInstrumentMap) {
        Map<String, SellInstrument> sellInstruments = new HashMap<>();
        sellInstrumentMap.entrySet().stream().forEach(sim -> {
            int sellQuantity = sim.getValue().getQuantitySell();
            BuyInstrument buyInstrument = buyInstruments.stream()
                    .filter(bim -> bim.getQuantityPortfolio() > 0).findFirst().get();
            int version = 0;
            do {
                if (sellQuantity == buyInstrument.getQuantityPortfolio()) {
                    SellInstrument sellInstrument = getPercantage(sim.getValue(), buyInstrument);
                    sellInstrument.setVersion(String.valueOf(version));
                    sellInstruments.put(sellInstrument.getId() + sellInstrument.getVersion(), sellInstrument);

                    buyInstrument.setQuantityPortfolio(0);

                } else if (sellQuantity < buyInstrument.getQuantityPortfolio()) {
                    SellInstrument sellInstrument = getPercantage(sim.getValue(), buyInstrument);
                    sellInstrument.setVersion(String.valueOf(version));
                    sellInstruments.put(sellInstrument.getId() + sellInstrument.getVersion(), sellInstrument);

                    buyInstrument.setQuantityPortfolio(buyInstrument.getQuantityPortfolio() - sellQuantity);

                } else {
                    SellInstrument sellInstrument = getPercantage(sim.getValue(), buyInstrument);
                    sellInstrument.setQuantitySell(buyInstrument.getQuantityPortfolio());
                    sellInstrument.setVersion(String.valueOf(version));
                    sellInstruments.put(sellInstrument.getId() + sellInstrument.getVersion(), sellInstrument);

                    version++;
                    buyInstrument.setQuantityPortfolio(0);
                }
            } while (buyInstrument.getQuantityPortfolio() > 0);
        });
        return sellInstruments;
    }

    private SellInstrument getPercantage(SellInstrument sellInstrument, BuyInstrument buyInstrument) {
        long betweenDay = ChronoUnit.DAYS.between(buyInstrument.getStartDate(), sellInstrument.getEndDate());
        if (betweenDay == 0) {
            sellInstrument.setCountDay(1);
        } else {
            sellInstrument.setCountDay(betweenDay);
        }
        double profit = sellInstrument.getSellCourse() - buyInstrument.getBuyCourse();
        sellInstrument.setProfit(profit);
        double persent = profit / buyInstrument.getBuyCourse() * 100;
        sellInstrument.setPercentProfit(persent);

        sellInstrument.setPercentProfitYear(persent * (365 / sellInstrument.getCountDay()));
        return sellInstrument;
    }

    private List<BuyInstrument> getBuyOperationByFigi(List<InstrumentOperation> instrumentOperationList) {
        return instrumentOperationList.stream()
                .filter(o -> OperationType.BUY.getDescription().equals(o.getOperationType())).map(buyInstrumentMapper::getBuyInstrumentFromInstrumentOperation)
                .sorted(Comparator.comparing(BuyInstrument::getStartDate)).collect(Collectors.toList());
    }

    private Map<String, SellInstrument> getSellOperationByFigi(List<InstrumentOperation> instrumentOperationList) {
        return instrumentOperationList.stream().filter(o -> ((OperationType.SELL.getDescription().equals(o.getOperationType())) && (!StatusType.DECLINE.getDescription().equals(o.getStatus()))))
                .map(sellInstrumentMapper::getSellInstrumentFromOperationInstrument).collect(Collectors.toMap(i -> i.getId() + "0", i -> i));
    }

    private Period getPeriodDateAll() {
        Period period = new Period();
        LocalDateTime minDate = instrumentOperationRepository.getMinDate(); /// тут не должно быть макс дня. макс день это localdatetime.now()
        LocalDateTime maxDate = LocalDateTime.now();

        period.setStartDate(minDate);
        period.setEndDate(maxDate);
        period.setDayOpen(ChronoUnit.DAYS.between(minDate, maxDate));
        return period;
    }

    private void getNewOperations(String token, String accountId, LocalDateTime maxDate, LocalDateTime minDate) {
        if (maxDate.toLocalDate().isBefore(LocalDate.now())) {
            String startPeriod = getStringFromLocalDateTime(maxDate);
            String endPeriod = getStringFromLocalDateTime(minDate);
            investmentTinkoffService.getOperations(startPeriod, endPeriod, accountId, token);
        }
    }

    private double getCurrentRateInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() + position.getExpectedYield().getValue() / position.getBalance();
    }

    private double getCurrentSumInstrument(Position position) {
        return position.getAveragePositionPrice().getValue() * position.getBalance() + position.getExpectedYield().getValue();
    }
}
 /*    private SellInstrument setSellCoursePersantageNow(SellInstrument sellInstrument, String figi, String token) {
        String fromDate = getStringFromLocalDateTime(LocalDateTime.now().minusMinutes(240));
        String toDate = getStringFromLocalDateTime(LocalDateTime.now().minusMinutes(180));
        double currentPrice = investmentTinkoffService.getCandles(figi, token, fromDate, toDate);

        sellInstrument.setSellCourse(currentPrice);
        sellInstrument.setEndDate(LocalDateTime.now());
        return sellInstrument;
    }
} */

