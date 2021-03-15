package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.CurrencyOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.entity.InstrumentOperation;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.CurrencyOperationsMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.mapper.InstrumentOperationsMapper;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.OperationType;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.CurrencyOperationRepository;
import ru.kulikovskiy.trading.investmantanalysistinkoff.repository.InstrumentOperationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor


public class OperationsServiceImpl implements OperationsService {
    @Autowired
    private InvestmentTinkoffService investmentTinkoffService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private InstrumentOperationRepository instrumentOperationRepository;
    @Autowired
    private CurrencyOperationRepository currencyOperationRepository;
    @Autowired
    private CurrencyOperationsMapper currencyOperationsMapper;
    @Autowired
    private InstrumentOperationsMapper instrumentOperationsMapper;

    @Override
    public OperationDto getOperationsBetweenDate(String fromDate, String toDate, String token, String brokerType) throws NotFoundException {
        String accountId = accountService.getAccountId(token, brokerType);
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

        int countLoad = operationsList.stream().mapToInt(o -> {
            if ((OperationType.SELL.getDescription().equals(o.getOperationType())) ||
                    (OperationType.BUY.getDescription().equals(o.getOperationType())) ||
                    (OperationType.COUPON.getDescription().equals(o.getOperationType())) ||
                    (OperationType.DIVIDEND.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_BACK.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_COUPON.getDescription().equals(o.getOperationType())) ||
                    (OperationType.TAX_DIVIDEND.getDescription().equals(o.getOperationType()))) {
                InstrumentOperation instrumentOperation = instrumentOperationsMapper.toInstrumentOperation(o);
                instrumentOperationRepository.save(instrumentOperation);
                return 1;
            } else if ((OperationType.PAY_OUT.getDescription().equals(o.getOperationType())) || (OperationType.PAY_IN.getDescription().equals(o.getOperationType())) || (OperationType.CURRENCY.getDescription().equals(o.getOperationType())) ||
                    (OperationType.BROKER_COMISSION.getDescription().equals(o.getOperationType())) || (OperationType.SERVICE_COMMISSION.getDescription().equals(o.getOperationType()))) {
                CurrencyOperation currencyOperation = currencyOperationsMapper.toCurrencyOperation(o);
                currencyOperationRepository.save(currencyOperation);
                return 1;
            } else {
                return 0;
            }
        }).sum();
        operationDto.setCountLoadOperation(countLoad);
        return operationDto;
    }
}
