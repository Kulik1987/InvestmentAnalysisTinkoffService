package ru.kulikovskiy.trading.investmantanalysistinkoff.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface MessageHandler {
    SendMessage startMessage(Long chatId) throws NotFoundException;

    SendMessage getAllAnalise(Long chatId) throws NotFoundException;

}
