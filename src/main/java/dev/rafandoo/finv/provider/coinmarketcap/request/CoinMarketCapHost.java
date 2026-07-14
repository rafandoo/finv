package dev.rafandoo.finv.provider.coinmarketcap.request;

/**
 * CoinMarketCap API hosts. The keyless public API requires no authentication.
 */
public enum CoinMarketCapHost {
    PUBLIC("pro-api.coinmarketcap.com");

    private final String host;

    CoinMarketCapHost(String host) {
        this.host = host;
    }

    public String host() {
        return host;
    }
}
