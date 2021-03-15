package ru.kulikovskiy.trading;

import org.eclipse.jetty.util.StringUtil;
import ru.kulikovskiy.trading.investmantanalysistinkoff.exception.NotFoundException;

public class Util {
    public static void  checkEmptyToken(String token) throws NotFoundException {
        if (StringUtil.isEmpty(token)) {
            throw new NotFoundException("token is empty");
        }
    }
}
