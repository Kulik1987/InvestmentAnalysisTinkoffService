package ru.kulikovskiy.trading.investmantanalysistinkoff;

public interface TelegramConst {
    // Command
    String START = "/start";
    String TOKEN = "/token";
    String ALL = "/all";
    String SEPARATE_PAY_IN = "/separatePayIn";
    String TICKER_CLOSE_OPERATION = "/tickerCloseOper";
    String ALL_TICKER_CLOSE_OPERATION = "/allTickerCloseOper";
    //Text message
    String TITLE = "Доходность за весь период:  \n";
    String TITLE_AVG = "Доходность за весь период с учетом даты пополнений:  \n";
    String TITLE_FIGI = "Доходность за весь период по 1 позиции по закрытым операциям:  \n";
    String ALL_TICKER = "Доходность по всем закрытым операциям: \n";
    String START_DATE = "Начало анализируемого периода: ";
    String END_DATE = "Окончание анализируемого периода: ";
    String PERIOD = "Продолжительность анализируемого периода: ";
    String PERIOD_AVG = "Средний период нахождение 1 акции на счете: ";
    String PAY_IN = "Сумма пополнений: ";
    String PAY_OUT = "Сумма выведенных денег: ";
    String CURRENT_SUM = "Текущая сумма на счете: ";
    String PERCENT = "Доходность за период: ";
    String PERCENT_YEAR = "Доходность за период в % годовых: ";
    String TICKER = "Ticker: ";
    String NAME = "Инструмент: ";
    String PROFIT_AVG = "Средний доход с продажи инструмента: ";
    String QUANTITY = "Продано акций: ";
    String ALL_PROFIT = "Итого доход по инструменту: ";

    String PERIOD_AVG_ALL = "Ср дней на счете: ";
    String PROFIT_AVG_ALL = "Ср доход: ";
    String PERCENT_ALL = "Ср %: ";
}
