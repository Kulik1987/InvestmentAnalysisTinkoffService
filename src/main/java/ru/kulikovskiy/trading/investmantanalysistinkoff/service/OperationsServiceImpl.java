package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.CurrencyOperationsMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.InstrumentOperationsMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.enums.OperationType;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.kulikovskiy.trading.HazelcastConst.CURRENCY;
import static ru.kulikovskiy.trading.investmantanalysistinkoff.Const.*;

@Service
@RequiredArgsConstructor


public class OperationsServiceImpl implements OperationsService {
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;
    @Autowired
    private CurrencyOperationsMapper currencyOperationsMapper;
    @Autowired
    private InstrumentOperationsMapper instrumentOperationsMapper;

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastInstance;

    private final String DONE = "Done";
    private final String TCS_FIGI_USD = "BBG005DXJS36";

    @Override
    public OperationDto getOperationsBetweenDate(String fromDate, String toDate, String token, String brokerType, String accountId) {
        OperationDto operationDto = new OperationDto();
        if (StringUtil.isEmpty(accountId)) {
            operationDto.setErrorMessage("accountId is empty");
            return operationDto;
        }
        List<Operations> operationsList = investmentTinkoffService.getOperations(fromDate, toDate, accountId, token);

        if (operationsList.isEmpty()) {
            operationDto.setErrorMessage("operationList is empty");
            return operationDto;
        }
        return getOperationDto(operationDto, operationsList);
    }

    @Override
    public OperationDto getOperationsBetweenDateByFigi(String startPeriod, String endPeriod, @NotNull String token, String brokerType, @NotNull String accountId, @NotNull String figi) throws NotFoundException {
        OperationDto operationDto = new OperationDto();
        if (StringUtil.isEmpty(accountId)) {
            operationDto.setErrorMessage("accountId is empty");
            return operationDto;
        }

        List<Operations> operationsList = investmentTinkoffService.getOperationsByFigi(startPeriod, endPeriod, accountId, token, figi);
        if (operationsList.size() == 0) {
            throw new NotFoundException("0 operation FIGI");
        }

        return getOperationDto(operationDto, operationsList);
    }

    @NotNull
    private OperationDto getOperationDto(OperationDto operationDto, List<Operations> operationsList) {
        List<InstrumentOperation> instrumentOperationList = new ArrayList<>();
        List<CurrencyOperation> currencyOperationList = new ArrayList<>();

        int countLoad = getOperationCurrencyAndInstrument(operationsList, instrumentOperationList, currencyOperationList);
        operationDto.setCurrencyOperationList(currencyOperationList);
        operationDto.setInstrumentOperationList(instrumentOperationList);
        operationDto.setCountLoadOperation(countLoad);
        return operationDto;
    }

    private int getOperationCurrencyAndInstrument(List<Operations> operationsList, List<InstrumentOperation> instrumentOperationList, List<CurrencyOperation> currencyOperationList) {
        String figi = operationsList.stream().findFirst().get().getFigi();
        if (TCS_FIGI_USD.equals(figi)) {
           String currency = String.valueOf(hazelcastInstance.getMap(CURRENCY).get(TCS));
           operationsList = operationsList.stream().filter(ol -> currency.equals(ol.getCurrency().name())).collect(Collectors.toList());
        }
        return operationsList.stream().filter(o -> DONE.equals(o.getStatus())).mapToInt(o -> {
            if ((OperationType.SELL.getDescription().equals(o.getOperationType())) ||
                    (OperationType.BUY.getDescription().equals(o.getOperationType())) ||
                    (OperationType.COUPON.getDescription().equals(o.getOperationType())) ||
                    (OperationType.DIVIDEND.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_BACK.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_COUPON.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_DIVIDEND.getDescription().equals(o.getOperationType()))) {
                InstrumentOperation instrumentOperation = instrumentOperationsMapper.toInstrumentOperation(o);
                instrumentOperationList.add(instrumentOperation);
                return 1;
            } else if ((OperationType.PAY_OUT.getDescription().equals(o.getOperationType())) || (OperationType.PAY_IN.getDescription().equals(o.getOperationType())) || (OperationType.CURRENCY.getDescription().equals(o.getOperationType())) ||
                    (OperationType.BROKER_COMISSION.getDescription().equals(o.getOperationType())) || (OperationType.SERVICE_COMMISSION.getDescription().equals(o.getOperationType()))) {
                CurrencyOperation currencyOperation = currencyOperationsMapper.toCurrencyOperation(o);
                currencyOperationList.add(currencyOperation);
                return 1;
            } else {
                return 0;
            }
        }).sum();
    }
}