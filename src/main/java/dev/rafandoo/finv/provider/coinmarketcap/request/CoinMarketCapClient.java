package dev.rafandoo.finv.provider.coinmarketcap.request;

import dev.rafandoo.cup.request.HttpRequester;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.core.exception.ProviderRequestException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMetadata;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.CoinMarketCapRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static dev.rafandoo.finv.enums.Parameters.REQUEST_TIMEOUT;
import static dev.rafandoo.finv.enums.Parameters.USER_AGENT;

/**
 * Executes CoinMarketCap keyless requests. No authentication is required.
 */
@Slf4j
public final class CoinMarketCapClient {

    public <T> Optional<T> execute(CoinMarketCapRequest<T> request) {
        Map<String, Object> headers = Map.of(
            "Accept", "application/json",
            "User-Agent", USER_AGENT.get().asString()
        );

        UrlBuilder urlBuilder = request.url();
        String url = urlBuilder.toString();

        log.debug("#{} - Executing request: {}", CoinMarketCapMetadata.METADATA.id(), url);

        HttpResponse response;
        try {
            response = HttpRequester.builder()
                .url(url)
                .headers(headers)
                .connectionTimeout(REQUEST_TIMEOUT.get().asInteger())
                .readTimeout(REQUEST_TIMEOUT.get().asInteger())
                .get();
        } catch (IOException e) {
            throw new ProviderRequestException(
                "#%s - Request failed".formatted(CoinMarketCapMetadata.METADATA.id()),
                -1,
                e
            );
        }

        if (!response.isSuccessful()) {
            int statusCode = response.getStatusCode();
            response.close();
            throw new ProviderRequestException(
                "#%s - request failed with status %d".formatted(
                    CoinMarketCapMetadata.METADATA.id(), statusCode),
                statusCode,
                null
            );
        }

        try {
            ResponseParser<T> parser = request.parser();
            return parser.parse(response);
        } catch (ParseException e) {
            response.close();
            throw new ProviderRequestException(
                "#%s - parse error".formatted(CoinMarketCapMetadata.METADATA.id()),
                e);
        }
    }
}
