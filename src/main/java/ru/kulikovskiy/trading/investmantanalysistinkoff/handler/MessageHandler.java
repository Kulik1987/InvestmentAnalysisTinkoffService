package ru.kulikovskiy.trading.investmantanalysistinkoff.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface MessageHandler {
    SendMessage startMessage(Long chatId) throws NotFoundException;

    SendMessage getToken(Long chatId, String token) throws NotFoundException;

    SendMessage getAll(Long chatId) throws NotFoundException;

    SendMessage getAllSeparatePayIn(Long chatId) throws NotFoundException;

    SendMessage getTickerCloseOper(Long chatId, String ticker) throws NotFoundException;

    SendMessage getAllTickerCloseOper(Long chatId) throws NotFoundException;


}
