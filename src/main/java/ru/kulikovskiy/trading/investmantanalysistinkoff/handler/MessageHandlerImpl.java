package ru.kulikovskiy.trading.investmantanalysistinkoff.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kulikovskiy.trading.investmantanalysistinkoff.config.ClientConfig;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.ReportAllDayAllMoneyResponse;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioService;

import static ru.kulikovskiy.trading.Util.checkEmptyToken;

@Service
public class
MessageHandlerImpl implements MessageHandler {
    String BROKER_TYPE = "TinkoffIis";
    @Autowired
    private AnalyzePortfolioService analyzePortfolioService;
    @Autowired
    private ClientConfig clientConfig;

    @Override
    public SendMessage startMessage(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello. This is analise appplication.");
        return message;
    }

    @Override
    public SendMessage getAllAnalise(Long chatId) throws NotFoundException  {
        String token = clientConfig.getToken();
        checkEmptyToken(token);
        ReportAllDayAllMoneyResponse response = analyzePortfolioService.getReportAllDayAllInstrument(token, BROKER_TYPE);

        String text = "This is all day report:  \n" +
                "start date: " + response.getReportInstrument().getStartDate() + "\n" +
                "finish date: " + response.getReportInstrument().getEndDdate() + "\n" +
                "period day: " + response.getReportInstrument().getPeriod() + "\n" +
                "all pay in: " + response.getReportInstrument().getPayInAll() + "\n" +
                "all pay out: " + response.getReportInstrument().getPayOutAll() + "\n" +
                "current sum: " + response.getReportInstrument().getCurrentSum() + "\n" +
                "percent profit: " + response.getReportInstrument().getPercentProfit() + "\n" +
                "percent profit year:" + response.getReportInstrument().getPercentProfitYear();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
}
