package dev.rafandoo.finv;

import dev.rafandoo.finv.core.market.Interval;
import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Crypto;
import dev.rafandoo.finv.core.market.asset.Stock;
import dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMarketDataProvider;
import dev.rafandoo.finv.provider.yahoo.YahooMarketDataProvider;

import java.time.Instant;
import java.util.Currency;
import java.util.Locale;

public class Sandbox {


    public static void main(String[] args) {
        YahooMarketDataProvider provider = new YahooMarketDataProvider();

//        provider.instrument(new Stock("AAPL", "NASDAQ", Currency.getInstance(Locale.of("pt", "BR")))).ifPresent(System.out::println);
//        provider.latest(new Stock("AAPL", "NASDAQ", Currency.getInstance(Locale.of("pt", "BR")))).ifPresent(System.out::println);

//        provider.history(
//            new Stock("AAPL", "NASDAQ", Currency.getInstance(Locale.of("pt", "BR"))),
//            new TimeRange(
//                Instant.parse("2023-01-01T00:00:00Z"),
//                Instant.parse("2023-01-31T23:59:59Z"),
//                Interval.DAILY
//            ),
//            true
//        ).forEach(System.out::println);

        CoinMarketCapMarketDataProvider coinMarketCapProvider = new CoinMarketCapMarketDataProvider();

        coinMarketCapProvider.latest(
            new Crypto("BTC", "USDT", "Binance", Currency.getInstance(Locale.of("pt", "BR")))
        ).ifPresent(System.out::println);

//        provider.dividends(
//            new Stock("AAPL", "NASDAQ", Currency.getInstance(Locale.of("pt", "BR"))),
//            new TimeRange(
//                Instant.parse("2023-01-01T00:00:00Z"),
//                Instant.parse("2023-12-31T23:59:59Z"),
//                null
//            )).ifPresent(stream -> stream.forEach(System.out::println));
//
//        provider.splits(
//            new Stock("AAPL", "NASDAQ", Currency.getInstance(Locale.of("pt", "BR"))),
//            new TimeRange(
//                Instant.parse("2000-01-01T00:00:00Z"),
//                Instant.parse("2023-12-31T23:59:59Z"),
//                null
//            )).ifPresent(stream -> stream.forEach(System.out::println));
    }
}
