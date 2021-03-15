package ru.kulikovskiy.trading.investmantanalysistinkoff.service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.config.TelegramConfig;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;
import ru.kulikovskiy.trading.investmantanalysistinkoff.handler.MessageHandler;

import java.util.Arrays;
import java.util.List;

import static ru.kulikovskiy.trading.investmantanalysistinkoff.TelegramConst.*;

/**
 * Main Telegram bot class
 *
 * @author whiskels
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {
    final int RECONNECT_PAUSE = 10000;

    @Autowired
    private TelegramConfig telegramConfig;

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        List<String> inputText = Arrays.asList(update.getMessage().getText().split(" "));

        SendMessage message = new SendMessage();

        try {
            if (inputText.get(0).equals(START)) {
                message = messageHandler.startMessage(chatId);
            } else if (inputText.get(0).equals(TOKEN)) {
                message = messageHandler.getToken(chatId, inputText.get(1));
            } else if (inputText.get(0).equals(ALL)) {
                message = messageHandler.getAll(chatId);
            } else if (inputText.get(0).equals(SEPARATE_PAY_IN)) {
                message = messageHandler.getAllSeparatePayIn(chatId);
            } else if (inputText.get(0).equals(TICKER_CLOSE_OPERATION)) {
                message = messageHandler.getTickerCloseOper(chatId, inputText.get(1));
            } else if (inputText.get(0).equals(ALL_TICKER_CLOSE_OPERATION)) {
                message = messageHandler.getAllTickerCloseOper(chatId);
            }
            execute(message);
        } catch (NotFoundException e) {
            e.printStackTrace();
            message.setText(e.getMessage());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getName();
    }

    @Override
    public String getBotToken() {
        return telegramConfig.getToken();
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Look for messages");
        } catch (TelegramApiRequestException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}
