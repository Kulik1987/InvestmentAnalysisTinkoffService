package ru.kulikovskiy.trading.investmantanalysistinkoff.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kulikovskiy.trading.investmantanalysistinkoff.config.ClientConfig;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.kulikovskiy.trading.Util.checkEmptyToken;
import static ru.kulikovskiy.trading.investmantanalysistinkoff.TelegramConst.*;

@Service
public class
MessageHandlerImpl implements MessageHandler {
    String BROKER_TYPE = "TinkoffIis";
    @Autowired
    private AnalyzePortfolioService analyzePortfolioService;
    @Autowired
    private ClientConfig clientConfig;

    @Override
    public SendMessage startMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello. This is analise appplication.");
        return message;
    }

    @Override
    public SendMessage getToken(Long chatId, String token) throws NotFoundException {
        checkEmptyToken(token);
        clientConfig.setToken(token);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Token add successful");
        return message;

    }

    @Override
    public SendMessage getAllSeparatePayIn(Long chatId) throws NotFoundException {
        String token = clientConfig.getToken();
        checkEmptyToken(token);
        AllMoneyReportDto response = analyzePortfolioService.getReportAllDayAllInstrumentSeparatePayIn(token, BROKER_TYPE);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE_AVG + getText(response));
        return message;
    }

    @Override
    public SendMessage getTickerCloseOper(Long chatId, String ticker) throws NotFoundException {
        String token = clientConfig.getToken();
        checkEmptyToken(token);
        OneTickerCloseOperationReportDto response = analyzePortfolioService.getReportAllDayByTickerCloseOperation(token, BROKER_TYPE, ticker);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE_FIGI + getTextByTicker(response));
        return message;
    }

    @Override
    public SendMessage getAllTickerCloseOper(Long chatId) throws NotFoundException {
        String token = clientConfig.getToken();
        checkEmptyToken(token);
        AllTickerCloseOperationReportDto response = analyzePortfolioService.getAllTickerCloseOperationReportDto(token, BROKER_TYPE);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ALL_TICKER + getTextAllTicker(response));
        return message;
    }

    @Override
    public SendMessage getAll(Long chatId) throws NotFoundException {
        String token = clientConfig.getToken();
        checkEmptyToken(token);
        AllMoneyReportDto response = analyzePortfolioService.getReportAllDayAllInstrument(token, BROKER_TYPE);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE + getText(response));
        return message;
    }

    @NotNull
    private String getTextAllTicker(AllTickerCloseOperationReportDto response) {

        List<String> texts = response.getReportInstrument().stream().map(i -> NAME + i.getNameInstrument() + "\n" +
                PERIOD_AVG_ALL + i.getAverageCountDay() + "\n" +
                PROFIT_AVG_ALL + i.getAverageProfit() + "\n" +
                PERCENT_ALL + i.getAveragePercentProfit()+ "\n"+ "\n").collect(Collectors.toList());
        String text = "Общий доход: " + response.getAllSumProfit() + "\n" + texts.stream().collect(Collectors.joining());
        return text;
    }

    @NotNull
    private String getText(AllMoneyReportDto response) {
        String text =
                START_DATE + response.getReportInstrument().getStartDate() + "\n" +
                        END_DATE + response.getReportInstrument().getEndDdate() + "\n" +
                        PERIOD + response.getReportInstrument().getPeriod() + "\n" +
                        PERIOD_AVG + response.getReportInstrument().getPeriodAvg() + "\n" +
                        PAY_IN + response.getReportInstrument().getPayInAll() + "\n" +
                        PAY_OUT + response.getReportInstrument().getPayOutAll() + "\n" +
                        CURRENT_SUM + response.getReportInstrument().getCurrentSum() + "\n" +
                        PERCENT + response.getReportInstrument().getPercentProfit() + "\n" +
                        PERCENT_YEAR + response.getReportInstrument().getPercentProfitYear();
        return text;
    }

    @NotNull
    private String getTextByTicker(OneTickerCloseOperationReportDto response) {
        String text =
                TICKER + response.getReportInstrument().getFigi() + "\n" +
                        NAME + response.getReportInstrument().getNameInstrument() + "\n" +
                        PERIOD_AVG + response.getReportInstrument().getAverageCountDay() + "\n" +
                        PROFIT_AVG + response.getReportInstrument().getAverageProfit() + "\n" +
                        PERCENT + response.getReportInstrument().getAveragePercentProfit();
        return text;
    }
}
