package dev.rafandoo.finv.provider.coinmarketcap.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;

/**
 * A CoinMarketCap keyless API request.
 *
 * @param <T> the parsed response type.
 */
public interface CoinMarketCapRequest<T> {

    /**
     * @return a {@link UrlBuilder} configured with the endpoint path and query parameters.
     */
    UrlBuilder url();

    /**
     * @return the {@link ResponseParser} that converts the raw response into {@code T}.
     */
    ResponseParser<T> parser();
}
