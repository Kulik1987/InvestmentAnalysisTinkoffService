package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public interface AccountService {
    void saveToken(String token, String chatId) throws NotFoundException;

    String getToken(String chatId) throws NotFoundException ;
}
