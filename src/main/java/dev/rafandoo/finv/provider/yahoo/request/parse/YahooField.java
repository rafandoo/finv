package dev.rafandoo.finv.provider.yahoo.request.parse;

import lombok.experimental.UtilityClass;

/**
 * Constant names of the JSON fields returned by the Yahoo Finance API.
 * Centralizing these avoids silent typos across the parsers.
 */
@UtilityClass
final class YahooField {

    static final String QUOTE_RESPONSE = "quoteResponse";
    static final String QUOTE_SUMMARY = "quoteSummary";
    static final String RESULT = "result";
    static final String ERROR = "error";
    static final String PRICE = "price";
    static final String SYMBOL = "symbol";
    static final String SHORT_NAME = "shortName";
    static final String LONG_NAME = "longName";
    static final String CURRENCY = "currency";
    static final String EXCHANGE_NAME = "exchangeName";
    static final String FULL_EXCHANGE_NAME = "fullExchangeName";
    static final String QUOTE_TYPE = "quoteType";
    static final String REGULAR_MARKET_PRICE = "regularMarketPrice";
    static final String REGULAR_MARKET_DAY_HIGH = "regularMarketDayHigh";
    static final String REGULAR_MARKET_DAY_LOW = "regularMarketDayLow";
    static final String REGULAR_MARKET_OPEN = "regularMarketOpen";
    static final String REGULAR_MARKET_PREVIOUS_CLOSE = "regularMarketPreviousClose";
    static final String REGULAR_MARKET_VOLUME = "regularMarketVolume";
    static final String REGULAR_MARKET_CHANGE = "regularMarketChange";
    static final String REGULAR_MARKET_CHANGE_PERCENT = "regularMarketChangePercent";
    static final String REGULAR_MARKET_TIME = "regularMarketTime";
    static final String CHART = "chart";
    static final String META = "meta";
    static final String INDICATORS = "indicators";
    static final String QUOTE = "quote";
    static final String ADJCLOSE = "adjclose";
    static final String TIMESTAMP = "timestamp";
    static final String OPEN = "open";
    static final String HIGH = "high";
    static final String LOW = "low";
    static final String CLOSE = "close";
    static final String VOLUME = "volume";
    static final String EVENTS = "events";
    static final String DIVIDENDS = "dividends";
    static final String SPLITS = "splits";
    static final String AMOUNT = "amount";
    static final String SPLIT_RATIO = "splitRatio";
    static final String DATE = "date";
    static final String CODE = "code";
    static final String DESCRIPTION = "description";
}
