package ru.kulikovskiy.trading.investmantanalysistinkoff.restcontroller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kulikovskiy.trading.investmantanalysistinkoff.config.ClientConfig;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.*;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioService;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.InstrumentsService;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.OperationsService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.kulikovskiy.trading.DateUtil.getStringFromLocalDateTime;
import static ru.kulikovskiy.trading.Util.checkEmptyToken;

@RestController
@RequestMapping(value = "investmentAnalysis")
@RequiredArgsConstructor
public class InvestmentAnalysisTinkoff {
    private final AccountService accountService;
    private final InstrumentsService instrumentsService;
    private final ClientConfig clientConfig;
    private final OperationsService operationsService;
    private final AnalyzePortfolioService analyzePortfolioService;

    @RequestMapping(value = "account", method = RequestMethod.GET)
    public ResponseEntity getAccount() {
        try {
            String token = clientConfig.getToken();
            List<AccountDto> accountDto = accountService.saveClientAccount(token);
            return ResponseEntity.ok(accountDto);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "instruments", method = RequestMethod.GET)
    public ResponseEntity<InstrumentResponse> getnstruments() {
        InstrumentResponse response = new InstrumentResponse();
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            response = instrumentsService.getInstruments(token);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "operations", method = RequestMethod.GET)
    public ResponseEntity<OperationResponse> getAnalyzePortfolio(@NotNull @RequestParam("brokerType") String brokerType,
                                              @NotNull @RequestParam("from") String fromDate,
                                              @RequestParam("to") String toDate) {
        OperationResponse operationResponse = new OperationResponse();
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            if (toDate == null) {
                toDate = getStringFromLocalDateTime(LocalDateTime.now());
            }
            operationResponse = operationsService.getOperationsBetweenDate(fromDate, toDate, token, brokerType);
            return ResponseEntity.ok(operationResponse);
        } catch (NotFoundException e) {
            operationResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @RequestMapping(value = "reportAllDayAllInstrument", method = RequestMethod.GET)
    public ResponseEntity<ReportAllDayAllMoneyResponse> getReportAllDayAllInstrument(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok(analyzePortfolioService.getReportAllDayAllInstrument(token, brokerType));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new ReportAllDayAllMoneyResponse(e.getMessage()));
        }
    }

    @RequestMapping(value = "reportAllDayBreakUpSellInstrument", method = RequestMethod.GET)
    public ResponseEntity<ReportAllDayBreakUpInstrumentResponse> getReportAllDayBreakUpInstrument(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new ReportAllDayBreakUpInstrumentResponse(e.getMessage()));
        }
    }
}
