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
/*
    @RequestMapping(value = "account", method = RequestMethod.GET)
    public ResponseEntity getAccount() {
        try {
            String token = clientConfig.getToken();
            List<AccountDto> accountDto = accountService.saveClientAccount(token, );
            return ResponseEntity.ok(accountDto);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
*/
    @RequestMapping(value = "instruments", method = RequestMethod.GET)
    public ResponseEntity<InstrumentDto> getnstruments() {
        InstrumentDto response = new InstrumentDto();
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
    public ResponseEntity<OperationDto> getAnalyzePortfolio(@NotNull @RequestParam("brokerType") String brokerType,
                                                            @NotNull @RequestParam("from") String fromDate,
                                                            @RequestParam("to") String toDate) {
        OperationDto operationDto = new OperationDto();
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            if (toDate == null) {
                toDate = getStringFromLocalDateTime(LocalDateTime.now());
            }
            operationDto = operationsService.getOperationsBetweenDate(fromDate, toDate, token, brokerType);
            return ResponseEntity.ok(operationDto);
        } catch (NotFoundException e) {
            operationDto.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(operationDto);
        }
    }
/*
    @RequestMapping(value = "reportAllDayAllInstrument", method = RequestMethod.GET)
    public ResponseEntity<AllMoneyReportDto> getReportAllDayAllInstrument(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok(analyzePortfolioService.getReportAllDayAllInstrument(, brokerType));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new AllMoneyReportDto(e.getMessage()));
        }
    }

    @RequestMapping(value = "reportAllDayAllInstrumentSeparatePayIn", method = RequestMethod.GET)
    public ResponseEntity<AllMoneyReportDto> getReportAllDayAllInstrumentSeparatePayIn(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok(analyzePortfolioService.getReportAllDayAllInstrumentSeparatePayIn(token, brokerType));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new AllMoneyReportDto(e.getMessage()));
        }
    }

    @RequestMapping(value = "reportAllDayByTickerCloseOperation", method = RequestMethod.GET)
    public ResponseEntity<OneTickerCloseOperationReportDto> getReportAllDayAllInstrumentCloseOperation(@NotNull @RequestParam("brokerType") String brokerType, @NotNull @RequestParam("ticker") String ticker) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok(analyzePortfolioService.getReportAllDayByTickerCloseOperation(token, brokerType, ticker));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new OneTickerCloseOperationReportDto(e.getMessage()));
        }
    }


    @RequestMapping(value = "reportAllDayBreakUpSellInstrument", method = RequestMethod.GET)
    public ResponseEntity<BreakUpSellInstrumentReportDto> getReportAllDayBreakUpInstrument(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new BreakUpSellInstrumentReportDto(e.getMessage()));
        }
    }

    @RequestMapping(value = "reportAllTickerCloseOperationReportDto", method = RequestMethod.GET)
    public ResponseEntity<AllTickerCloseOperationReportDto> getAllTickerCloseOperationReportDto(@NotNull @RequestParam("brokerType") String brokerType) {
        try {
            String token = clientConfig.getToken();
            checkEmptyToken(token);
            return ResponseEntity.ok(analyzePortfolioService.getAllTickerCloseOperationReportDto(token, brokerType));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(new AllTickerCloseOperationReportDto(e.getMessage()));
        }
    }

 */
}
