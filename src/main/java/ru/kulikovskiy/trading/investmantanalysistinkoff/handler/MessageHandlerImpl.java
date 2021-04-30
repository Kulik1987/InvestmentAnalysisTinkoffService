package ru.kulikovskiy.trading.investmantanalysistinkoff.handler;

import com.hazelcast.core.HazelcastInstance;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AllMoneyReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OneTickerCloseOperationReportDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService;
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AnalyzePortfolioService;

import static ru.kulikovskiy.trading.HazelcastConst.TOKENS;
import static ru.kulikovskiy.trading.Util.checkEmptyToken;
import static ru.kulikovskiy.trading.investmantanalysistinkoff.TelegramConst.*;

@Service
public class
MessageHandlerImpl implements MessageHandler {

    @Autowired
    private AnalyzePortfolioService analyzePortfolioService;
    @Autowired
    private AccountService accountService;
    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public SendMessage startMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет! Я бот, который поможет тебе оценить свои доходы в инвестициях." + "\n" +
                "вот что я умею:" + "\n" +
                "Для начала тебе надо залогиниться " + "\n" +
                "/token <token> - в данную команду надо передать токен из тиньков инвестиций" + "\n" +
                "Теперь можно смотреть сколько денег ты заработал на бирже:" + "\n" +
                "/all - получить отчет по общему доходу по всем акциям, Етф, облигациям" + "\n" +
                "/separatePayIn - тут при расчете дохода в % годовых учитывается дата пополнения счета" + "\n" +
                "/tickerCloseOper <ticker> - выводит информацию по закрытым операциям по 1 тикеру " + "\n" +
                "" + "\n" + "\n" +
                "<> - указывает что это параметр. При наборе команды ставить эти символы не нужно. Нарпимер для получения " +
                        "информации по тикеру надо набрать команду /tickerCloseOper JD" +
                "\n" + "\n" +
                "Пока я в начале пути анализа доходности инвестиций, но со временем, обязательно многому научусь" );
        return message;
    }

    @Override
    public SendMessage getToken(Long chatId, String token) throws NotFoundException {
        checkEmptyToken(token);
        accountService.saveToken(token, String.valueOf(chatId));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Токен успешно добавлен. Теперь можно получать отчеты!");
        return message;

    }

    @Override
    public SendMessage getAllSeparatePayIn(Long chatId) throws NotFoundException {
        String token = getToken(chatId);
        checkEmptyToken(token);
        AllMoneyReportDto response = analyzePortfolioService.getReportAllDayAllInstrumentSeparatePayIn(token);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE_AVG + getTextSeparatePayIn(response));
        return message;
    }

    @Override
    public SendMessage getTickerCloseOper(Long chatId, String ticker) throws NotFoundException {
        String token = getToken(chatId);
        checkEmptyToken(token);
        OneTickerCloseOperationReportDto response = analyzePortfolioService.getReportAllDayByTickerCloseOperation(token, ticker);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE_FIGI + getTextByTicker(response));
        return message;
    }

    @Override
    public SendMessage getAll(Long chatId) throws NotFoundException {
        String token = getToken(chatId);
        checkEmptyToken(token);
        AllMoneyReportDto response = analyzePortfolioService.getReportAllDayAllInstrument(token);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TITLE + getTextAllPayIn(response));
        return message;
    }

    @NotNull
    private String getTextAllPayIn(AllMoneyReportDto response) {
        String text =
                START_DATE + response.getReportInstrument().getStartDate() + "\n" +
                        END_DATE + response.getReportInstrument().getEndDdate() + "\n" +
                        PERIOD + response.getReportInstrument().getPeriod() + "\n" +
                        PAY_IN + response.getReportInstrument().getPayInAll() + "\n" +
                        PAY_OUT + response.getReportInstrument().getPayOutAll() + "\n" +
                        COMMISSION_ALL + response.getReportInstrument().getComissionAll() + "\n" +
                        CURRENT_SUM + response.getReportInstrument().getCurrentSum() + "\n" +
                        PERCENT + response.getReportInstrument().getPercentProfit() + "\n" +
                        PERCENT_YEAR + response.getReportInstrument().getPercentProfitYear();
        return text;
    }
    @NotNull
    private String getTextSeparatePayIn(AllMoneyReportDto response) {
        String text =
                START_DATE + response.getReportInstrument().getStartDate() + "\n" +
                        END_DATE + response.getReportInstrument().getEndDdate() + "\n" +
                        PERIOD + response.getReportInstrument().getPeriod() + "\n" +
                        PERIOD_AVG_ALL + response.getReportInstrument().getPeriodAvg() + "\n" +
                        PAY_IN + response.getReportInstrument().getPayInAll() + "\n" +
                        PAY_OUT + response.getReportInstrument().getPayOutAll() + "\n" +
                        COMMISSION_ALL + response.getReportInstrument().getComissionAll() + "\n" +
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
                        QUANTITY + response.getReportInstrument().getQuantityAll() + "\n" +
                        PERIOD_AVG + response.getReportInstrument().getAverageCountDay() + "\n" +
                        PROFIT_AVG + response.getReportInstrument().getAverageProfit() + "\n" +
                        PERCENT_AVG_TICKER + response.getReportInstrument().getAveragePercentProfit();
        return text;
    }

    private String getToken(Long chatId) throws NotFoundException {
        String token = (String) hazelcastInstance.getMap(TOKENS).get(String.valueOf(chatId));
        checkEmptyToken(token);
        return token;
    }
}
