# CoinMarketCap Crypto Provider Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `coinmarketcap` provider that fetches real-time crypto quotes and instrument metadata from CoinMarketCap's keyless public API, plus an optional `currency` field on `Crypto`.

**Architecture:** Mirror the existing `yahoo` provider: a `CoinMarketCapClient` performs GET requests (no auth) via `dev.rafandoo.cup.request.HttpRequester`, endpoints build `UrlBuilder` + `ResponseParser`, parsers map CMC JSON into `model` records and then into domain `Quote`/`Instrument`. Unsupported methods (`history`, `dividends`, `splits`) throw `UnsupportedOperationException`.

**Tech Stack:** Java 21, Jackson (via `dev.rafandoo.cup.JacksonMapperFactory`), `cup` HTTP (`HttpRequester`, `UrlBuilder`, `HttpResponse`), Lombok `@Slf4j`, JUnit 5 + AssertJ + Mockito (for `HttpResponseFixture`).

---

## Key facts the implementer must know

- **CoinMarketCap keyless host:** `https://pro-api.coinmarketcap.com/public-api` — **no API key / no auth header**.
- **Endpoints used:**
  - `GET /quotes/latest?symbol=BTC&convert=USD` → real-time quote.
  - `GET /info?symbol=BTC` → metadata.
- **CMC response envelope:** `{ "status": { "error_code": 0, "error_message": null, ... }, "data": { "BTC": { ... } } }`. `status.error_code != 0` means failure.
- **`/quotes/latest` quote node** (under `data[symbol].quote[convert]`): `price` (number), `volume_24h` (number), `percent_change_24h` (number), `last_updated` (ISO-8601 string, e.g. `"2024-01-01T00:00:00.000Z"`).
- **CMC does NOT return an absolute change** — only `percent_change_24h`. Derive absolute change: `change = price - price / (1 + pct/100)`.
- **`/info` node** (under `data[symbol]`): `name` (string), `symbol` (slug, e.g. `"BTC"`), `description` (string|null), `category` (string|null).
- **`Feature` enum** has only: `HISTORY, REALTIME, DIVIDENDS, SPLITS, FUNDAMENTALS`. There is **no** `LATEST_QUOTE`/`INSTRUMENT`. Use `Feature.REALTIME` for quotes. `instrument()` is part of the `MarketDataProvider` contract and is always callable; do NOT add a feature for it.
- **Domain `Quote`** = `record Quote(String symbol, BigDecimal price, BigDecimal change, BigDecimal changePercent, Instant date)`.
- **Domain `Instrument`** = `record Instrument(String symbol, String name, String fullName, AssetType assetType, String exchange, Currency currency)`.
- **`Crypto`** = `record Crypto(String base, String quote, String exchange, String currency) implements Asset` — `currency` is being added as **optional (nullable)**; `symbol()` returns `base + "/" + quote`.
- **`ResponseParser<T>`** interface (in `dev.rafandoo.finv.provider`): `Optional<T> parse(HttpResponse response) throws ParseException`.
- **`YahooRequest<T>`** interface: `UrlBuilder url()` + `ResponseParser<T> parser()`. Mirror this (without `requiresCrumb`).
- **Reuse:** `dev.rafandoo.cup.JacksonMapperFactory.json()` for the `ObjectMapper`. `HttpResponse.getBodyAsString()` for the body.
- **Tests:** use `dev.rafandoo.finv.test.HttpResponseFixture.json(String)` (returns status-200 `HttpResponse` with the given JSON body). Pattern from `provider/yahoo/request/parse/QuoteParserTest`.

---

## File Structure

**Create:**
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadata.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProvider.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapClient.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapHost.java` (host enum, mirrors `YahooHost`)
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/CoinMarketCapRequest.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/LatestQuoteRequest.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/InfoRequest.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcQuote.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcInfo.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/BaseParser.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParser.java`
- `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParser.java`

**Modify:**
- `src/main/java/dev/rafandoo/finv/core/market/asset/Crypto.java` — add optional `currency` field.

**Test (create):**
- `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadataTest.java`
- `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParserTest.java`
- `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParserTest.java`
- `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProviderTest.java`
- `src/test/java/dev/rafandoo/finv/core/market/asset/CryptoTest.java`

---

## Task 1: Add optional `currency` field to `Crypto`

**Files:**
- Modify: `src/main/java/dev/rafandoo/finv/core/market/asset/Crypto.java`
- Test: `src/test/java/dev/rafandoo/finv/core/market/asset/CryptoTest.java`

- [ ] **Step 1: Write the failing test**

```java
package dev.rafandoo.finv.core.market.asset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoTest {

    @Test
    void symbolUsesBaseAndQuote() {
        Crypto crypto = new Crypto("BTC", "USDT", "Binance", "USD");
        assertThat(crypto.symbol()).isEqualTo("BTC/USDT");
        assertThat(crypto.type()).isEqualTo(AssetType.CRYPTO);
    }

    @Test
    void currencyIsOptional() {
        Crypto withCurrency = new Crypto("BTC", "USDT", "Binance", "BRL");
        Crypto withoutCurrency = new Crypto("BTC", "USDT", "Binance", null);
        assertThat(withCurrency.currency()).isEqualTo("BRL");
        assertThat(withoutCurrency.currency()).isNull();
        // symbol() must not depend on currency
        assertThat(withoutCurrency.symbol()).isEqualTo("BTC/USDT");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "dev.rafandoo.finv.core.market.asset.CryptoTest"`
Expected: FAIL — `Crypto` constructor does not accept a 4th `currency` argument.

- [ ] **Step 3: Add the field**

Replace the contents of `Crypto.java` with:

```java
package dev.rafandoo.finv.core.market.asset;

import java.util.Objects;

/**
 * Represents a cryptocurrency asset traded on an exchange.
 *
 * @param base     the base currency symbol (e.g. "BTC").
 * @param quote    the quote currency of the trading pair (e.g. "USDT").
 * @param exchange the exchange where the asset is traded (e.g. "Binance").
 * @param currency the optional fiat currency used to quote the price (e.g. "USD");
 *                 null/blank means the provider default (USD) is used.
 */
public record Crypto(
    String base,
    String quote,
    String exchange,
    String currency
) implements Asset {

    public Crypto {
        Objects.requireNonNull(base);
        Objects.requireNonNull(quote);
        Objects.requireNonNull(exchange);
        // currency stays nullable / optional by design
    }

    @Override
    public String symbol() {
        return base + "/" + quote;
    }

    @Override
    public AssetType type() {
        return AssetType.CRYPTO;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "dev.rafandoo.finv.core.market.asset.CryptoTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/dev/rafandoo/finv/core/market/asset/Crypto.java src/test/java/dev/rafandoo/finv/core/market/asset/CryptoTest.java
git commit -m "feat(crypto): add optional currency field for quote fiat"
```

---

## Task 2: Metadata + host enum

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadata.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapHost.java`
- Test: `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadataTest.java`

- [ ] **Step 1: Write the failing test**

```java
package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoinMarketCapMetadataTest {

    @Test
    void exposesCryptoAndRealtime() {
        ProviderMetadata metadata = CoinMarketCapMetadata.METADATA;
        assertThat(metadata.id()).isEqualTo("coinmarketcap");
        assertThat(metadata.supportedAssetTypes()).containsExactly(AssetType.CRYPTO);
        assertThat(metadata.supportedFeatures()).containsExactly(Feature.REALTIME);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMetadataTest"`
Expected: FAIL — package/class does not exist.

- [ ] **Step 3: Create the host enum**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapHost.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request;

/**
 * CoinMarketCap API hosts. The keyless public API requires no authentication.
 */
public enum CoinMarketCapHost {
    PUBLIC("https://pro-api.coinmarketcap.com/public-api");

    private final String host;

    CoinMarketCapHost(String host) {
        this.host = host;
    }

    public String host() {
        return host;
    }
}
```

- [ ] **Step 4: Create the metadata**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadata.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.provider.Feature;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;

import java.util.Set;

/**
 * Static metadata for the CoinMarketCap keyless provider.
 */
public final class CoinMarketCapMetadata {

    private CoinMarketCapMetadata() {
    }

    public static final ProviderMetadata METADATA = new ProviderMetadata(
        "coinmarketcap",
        "CoinMarketCap",
        Set.of(AssetType.CRYPTO),
        Set.of(Feature.REALTIME)
    );
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMetadataTest"`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadata.java src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapHost.java src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMetadataTest.java
git commit -m "feat(cmc): add provider metadata and host enum"
```

---

## Task 3: Model records (`CmcQuote`, `CmcInfo`)

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcQuote.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcInfo.java`

These are immutable carriers for the parsed JSON. Mapping to domain types happens in the parsers.

- [ ] **Step 1: Create `CmcQuote`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcQuote.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Parsed real-time quote from CoinMarketCap /quotes/latest for a single symbol+convert pair.
 *
 * @param symbol         the asset symbol requested (e.g. "BTC").
 * @param price          the latest price in the convert currency.
 * @param volume24h      the 24h volume in the convert currency (may be null).
 * @param changePercent  the 24h percentage change (may be null).
 * @param lastUpdated    the quote timestamp (may be null).
 * @param convert        the fiat currency used (e.g. "USD").
 */
public record CmcQuote(
    String symbol,
    BigDecimal price,
    BigDecimal volume24h,
    BigDecimal changePercent,
    Instant lastUpdated,
    String convert
) {
}
```

- [ ] **Step 2: Create `CmcInfo`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcInfo.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.model;

/**
 * Parsed metadata from CoinMarketCap /info for a single symbol.
 *
 * @param symbol      the slug symbol (e.g. "BTC").
 * @param name        the display name (e.g. "Bitcoin").
 * @param description the long description (may be null).
 * @param category    the CMC category (may be null).
 */
public record CmcInfo(
    String symbol,
    String name,
    String description,
    String category
) {
}
```

- [ ] **Step 3: Compile check**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL (no tests yet for these).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcQuote.java src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/CmcInfo.java
git commit -m "feat(cmc): add CmcQuote and CmcInfo model records"
```

---

## Task 4: Request interface + endpoints

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/CoinMarketCapRequest.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/LatestQuoteRequest.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/InfoRequest.java`

- [ ] **Step 1: Create the request interface**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/CoinMarketCapRequest.java`:

```java
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
```

- [ ] **Step 2: Create `LatestQuoteRequest`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/LatestQuoteRequest.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import dev.rafandoo.finv.provider.coinmarketcap.request.parse.QuoteParser;

import static dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost.PUBLIC;

/**
 * Request for {@code GET /quotes/latest?symbol=<symbol>&convert=<currency>}.
 */
public final class LatestQuoteRequest implements CoinMarketCapRequest<CmcQuote> {

    private final String symbol;
    private final String currency;

    public LatestQuoteRequest(String symbol, String currency) {
        this.symbol = symbol;
        this.currency = currency;
    }

    @Override
    public UrlBuilder url() {
        return UrlBuilder.create(PUBLIC.host() + "/quotes/latest")
            .addParameter("symbol", symbol)
            .addParameter("convert", currency);
    }

    @Override
    public ResponseParser<CmcQuote> parser() {
        return new QuoteParser(symbol, currency);
    }
}
```

- [ ] **Step 3: Create `InfoRequest`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/InfoRequest.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.parse.InfoParser;

import static dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapHost.PUBLIC;

/**
 * Request for {@code GET /info?symbol=<symbol>}.
 */
public final class InfoRequest implements CoinMarketCapRequest<CmcInfo> {

    private final String symbol;

    public InfoRequest(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public UrlBuilder url() {
        return UrlBuilder.create(PUBLIC.host() + "/info")
            .addParameter("symbol", symbol);
    }

    @Override
    public ResponseParser<CmcInfo> parser() {
        return new InfoParser(symbol);
    }
}
```

- [ ] **Step 4: Compile check**

Run: `./gradlew compileJava`
Expected: FAIL — `QuoteParser`/`InfoParser` referenced but not yet created. That is expected; they arrive in Task 5. Do NOT commit yet (commit at end of Task 5 once the graph compiles).

---

## Task 5: Parsers (`BaseParser`, `QuoteParser`, `InfoParser`) + parser tests

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/BaseParser.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParser.java`
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParser.java`
- Test: `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParserTest.java`
- Test: `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParserTest.java`

- [ ] **Step 1: Write the failing QuoteParser test**

`src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParserTest.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteParserTest {

    private static final String OK = """
        {
          "status": { "error_code": 0, "error_message": null },
          "data": {
            "BTC": {
              "id": 1,
              "name": "Bitcoin",
              "quote": {
                "USD": {
                  "price": 50000.0,
                  "volume_24h": 30000000000.0,
                  "percent_change_24h": 2.5,
                  "last_updated": "2024-01-01T12:00:00.000Z"
                }
              }
            }
          }
        }
        """;

    private static final String EMPTY = """
        {
          "status": { "error_code": 0, "error_message": null },
          "data": { }
        }
        """;

    private static final String API_ERROR = """
        {
          "status": { "error_code": 400, "error_message": "symbol invalid" },
          "data": null
        }
        """;

    @Test
    void parsesValidQuote() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        Optional<CmcQuote> result = parser.parse(HttpResponseFixture.json(OK));

        assertThat(result).isPresent();
        CmcQuote q = result.get();
        assertThat(q.symbol()).isEqualTo("BTC");
        assertThat(q.price()).isEqualByComparingTo("50000.0");
        assertThat(q.changePercent()).isEqualByComparingTo("2.5");
        assertThat(q.convert()).isEqualTo("USD");
        assertThat(q.lastUpdated()).isNotNull();
    }

    @Test
    void emptyDataReturnsEmpty() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        assertThat(parser.parse(HttpResponseFixture.json(EMPTY))).isEmpty();
    }

    @Test
    void apiErrorThrows() throws Exception {
        QuoteParser parser = new QuoteParser("BTC", "USD");
        assertThatThrownBy(() -> parser.parse(HttpResponseFixture.json(API_ERROR)))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("symbol invalid");
    }
}
```

- [ ] **Step 2: Write the failing InfoParser test**

`src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParserTest.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.test.HttpResponseFixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InfoParserTest {

    private static final String OK = """
        {
          "status": { "error_code": 0, "error_message": null },
          "data": {
            "BTC": {
              "id": 1,
              "name": "Bitcoin",
              "symbol": "BTC",
              "category": "coin",
              "description": "Bitcoin is a decentralized..."
            }
          }
        }
        """;

    private static final String EMPTY = """
        { "status": { "error_code": 0, "error_message": null }, "data": { } }
        """;

    @Test
    void parsesValidInfo() throws Exception {
        InfoParser parser = new InfoParser("BTC");
        Optional<CmcInfo> result = parser.parse(HttpResponseFixture.json(OK));

        assertThat(result).isPresent();
        CmcInfo info = result.get();
        assertThat(info.symbol()).isEqualTo("BTC");
        assertThat(info.name()).isEqualTo("Bitcoin");
        assertThat(info.category()).isEqualTo("coin");
        assertThat(info.description()).startsWith("Bitcoin");
    }

    @Test
    void emptyDataReturnsEmpty() throws Exception {
        InfoParser parser = new InfoParser("BTC");
        assertThat(parser.parse(HttpResponseFixture.json(EMPTY))).isEmpty();
    }

    @Test
    void apiErrorThrows() throws Exception {
        String body = """
            { "status": { "error_code": 400, "error_message": "symbol invalid" }, "data": null }
            """;
        InfoParser parser = new InfoParser("BTC");
        assertThatThrownBy(() -> parser.parse(HttpResponseFixture.json(body)))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("symbol invalid");
    }
}
```

- [ ] **Step 3: Run tests to verify they fail**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.request.parse.*"`
Expected: FAIL — classes do not exist yet.

- [ ] **Step 4: Create `BaseParser`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/BaseParser.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.JacksonMapperFactory;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Shared base for CoinMarketCap parsers: Jackson tree read + CMC error-code gate.
 */
@Slf4j
public abstract class BaseParser {

    protected static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = JacksonMapperFactory.json();

    /**
     * Reads the response body into a JSON tree and throws if CMC reports an API error.
     *
     * @param response the HTTP response.
     * @return the parsed root node.
     * @throws ParseException on read failure or non-zero {@code status.error_code}.
     */
    protected JsonNode readRoot(HttpResponse response) throws ParseException {
        try (response) {
            JsonNode root = MAPPER.readTree(response.getBodyAsString());

            JsonNode status = root.path("status");
            int errorCode = status.path("error_code").asInt(0);
            if (errorCode != 0) {
                String message = status.path("error_message").asText("unknown error");
                throw new ParseException(
                    "#%s - CMC API error #%d: %s".formatted(
                        CoinMarketCapMetadata.METADATA.id(), errorCode, message)
                );
            }
            return root;
        } catch (IOException e) {
            throw new ParseException("Failed to read CoinMarketCap response", e);
        }
    }

    protected Optional<BigDecimal> bigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull()
            ? Optional.empty()
            : Optional.of(value.decimalValue());
    }

    protected String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }
}
```

- [ ] **Step 5: Create `QuoteParser`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/QuoteParser.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * Parses {@code /quotes/latest} responses into a {@link CmcQuote}.
 */
@Slf4j
public final class QuoteParser extends BaseParser implements ResponseParser<CmcQuote> {

    private final String symbol;
    private final String convert;

    public QuoteParser(String symbol, String convert) {
        this.symbol = symbol;
        this.convert = convert;
    }

    @Override
    public Optional<CmcQuote> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);
        JsonNode data = root.path("data");
        JsonNode entry = data.path(symbol);
        if (entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }
        JsonNode quote = entry.path("quote").path(convert);
        if (quote.isMissingNode() || quote.isNull()) {
            return Optional.empty();
        }

        BigDecimal price = bigDecimal(quote, "price").orElse(null);
        BigDecimal changePercent = bigDecimal(quote, "percent_change_24h").orElse(null);
        BigDecimal volume24h = bigDecimal(quote, "volume_24h").orElse(null);
        Instant lastUpdated = parseInstant(quote.path("last_updated").asText(null));

        return Optional.of(new CmcQuote(symbol, price, volume24h, changePercent, lastUpdated, convert));
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (Exception e) {
            log.warn("#{} - could not parse last_updated '{}'", "coinmarketcap", value, e);
            return null;
        }
    }
}
```

- [ ] **Step 6: Create `InfoParser`**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/InfoParser.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.cup.request.HttpResponse;
import dev.rafandoo.finv.core.exception.ParseException;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Parses {@code /info} responses into a {@link CmcInfo}.
 */
@Slf4j
public final class InfoParser extends BaseParser implements ResponseParser<CmcInfo> {

    private final String symbol;

    public InfoParser(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public Optional<CmcInfo> parse(HttpResponse response) throws ParseException {
        JsonNode root = readRoot(response);
        JsonNode data = root.path("data");
        JsonNode entry = data.path(symbol);
        if (entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }
        String name = textOrNull(entry, "name");
        String slug = textOrNull(entry, "symbol");
        String description = textOrNull(entry, "description");
        String category = textOrNull(entry, "category");
        return Optional.of(new CmcInfo(slug != null ? slug : symbol, name, description, category));
    }
}
```

- [ ] **Step 7: Run parser tests**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.request.parse.*"`
Expected: PASS

- [ ] **Step 8: Commit (this completes the graph from Task 4)**

```bash
git add src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/endpoint/ \
        src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/ \
        src/test/java/dev/rafandoo/finv/provider/coinmarketcap/request/parse/
git commit -m "feat(cmc): add endpoints, base/quote/info parsers and tests"
```

---

## Task 6: Client (`CoinMarketCapClient`)

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapClient.java`

- [ ] **Step 1: Create the client**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapClient.java`:

```java
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
        UrlBuilder urlBuilder = request.url();
        Map<String, Object> headers = Map.of(
            "Accept", "application/json",
            "User-Agent", USER_AGENT.get().asString()
        );

        try (HttpResponse response = new HttpRequester(urlBuilder.build())
            .setHeaders(headers)
            .setTimeout(REQUEST_TIMEOUT.get().asInt())
            .get()) {

            if (response.isNotSuccessful()) {
                throw new ProviderRequestException(
                    "#%s - request failed with status %d".formatted(
                        CoinMarketCapMetadata.METADATA.id(), response.getStatusCode()),
                    response.getStatusCode(),
                    null
                );
            }

            ResponseParser<T> parser = request.parser();
            return parser.parse(response);

        } catch (IOException e) {
            throw new ProviderRequestException(
                "#%s - network error".formatted(CoinMarketCapMetadata.METADATA.id()), e);
        } catch (ParseException e) {
            throw new ProviderRequestException(
                "#%s - parse error".formatted(CoinMarketCapMetadata.METADATA.id()), e);
        }
    }
}
```

> NOTE: confirm `HttpRequester` method names against `dev.rafandoo.cup.request.HttpRequester`: the existing Yahoo client uses `new HttpRequester(urlBuilder.build())`, `.setHeaders(headers)`, `.setTimeout(int)`, `.get()` returning an `HttpResponse`. If the actual API differs (e.g. method names), adapt to match `YahooFinanceClient.execute`.

- [ ] **Step 2: Compile check**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/CoinMarketCapClient.java
git commit -m "feat(cmc): add keyless HTTP client"
```

---

## Task 7: Provider (`CoinMarketCapMarketDataProvider`) + provider test

**Files:**
- Create: `src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProvider.java`
- Test: `src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProviderTest.java`

- [ ] **Step 1: Write the failing provider test**

`src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProviderTest.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Crypto;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapClient;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.InfoRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.LatestQuoteRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoinMarketCapMarketDataProviderTest {

    private final CoinMarketCapClient client = mock(CoinMarketCapClient.class);
    private final CoinMarketCapMarketDataProvider provider =
        new CoinMarketCapMarketDataProvider(client);

    private final Crypto btc = new Crypto("BTC", "USDT", "Binance", "USD");

    @Test
    void supportsCrypto() {
        assertThat(provider.supports(AssetType.CRYPTO)).isTrue();
        assertThat(provider.supports(AssetType.STOCK)).isFalse();
        assertThat(provider.metadata().id()).isEqualTo("coinmarketcap");
    }

    @Test
    void latestReturnsQuote() {
        CmcQuote cmc = new CmcQuote("BTC", new BigDecimal("50000"),
            new BigDecimal("3e10"), new BigDecimal("2.5"),
            Instant.parse("2024-01-01T12:00:00Z"), "USD");
        when(client.execute(new LatestQuoteRequest("BTC", "USD"))).thenReturn(Optional.of(cmc));

        Optional<Quote> quote = provider.latest(btc);
        assertThat(quote).isPresent();
        assertThat(quote.get().symbol()).isEqualTo("BTC/USDT");
        assertThat(quote.get().price()).isEqualByComparingTo("50000");
        assertThat(quote.get().changePercent()).isEqualByComparingTo("2.5");
        // absolute change derived from percent: change = price - price/(1+pct/100)
        assertThat(quote.get().change()).isEqualByComparingTo("1220.00");
    }

    @Test
    void instrumentReturnsMetadata() {
        CmcInfo info = new CmcInfo("BTC", "Bitcoin", "desc", "coin");
        when(client.execute(new InfoRequest("BTC"))).thenReturn(Optional.of(info));

        Optional<Instrument> instrument = provider.instrument(btc);
        assertThat(instrument).isPresent();
        assertThat(instrument.get().symbol()).isEqualTo("BTC/USDT");
        assertThat(instrument.get().name()).isEqualTo("Bitcoin");
        assertThat(instrument.get().assetType()).isEqualTo(AssetType.CRYPTO);
        assertThat(instrument.get().currency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    void defaultsMissingCurrencyToUsd() {
        Crypto noCurrency = new Crypto("BTC", "USDT", "Binance", null);
        CmcQuote cmc = new CmcQuote("BTC", new BigDecimal("50000"), null,
            new BigDecimal("1.0"), Instant.parse("2024-01-01T12:00:00Z"), "USD");
        when(client.execute(new LatestQuoteRequest("BTC", "USD"))).thenReturn(Optional.of(cmc));

        assertThat(provider.latest(noCurrency)).isPresent();
    }

    @Test
    void unsupportedOperationsThrow() {
        assertThatThrownBy(() -> provider.history(btc, null, null))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> provider.dividends(btc))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> provider.splits(btc))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMarketDataProviderTest"`
Expected: FAIL — class does not exist.

- [ ] **Step 3: Create the provider**

`src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProvider.java`:

```java
package dev.rafandoo.finv.provider.coinmarketcap;

import dev.rafandoo.finv.core.market.TimeRange;
import dev.rafandoo.finv.core.market.asset.Asset;
import dev.rafandoo.finv.core.market.asset.AssetType;
import dev.rafandoo.finv.core.market.asset.Crypto;
import dev.rafandoo.finv.core.market.data.Candle;
import dev.rafandoo.finv.core.market.data.Instrument;
import dev.rafandoo.finv.core.market.data.Quote;
import dev.rafandoo.finv.core.market.data.event.DividendEvent;
import dev.rafandoo.finv.core.market.data.event.SplitEvent;
import dev.rafandoo.finv.core.market.provider.MarketDataProvider;
import dev.rafandoo.finv.core.market.provider.ProviderMetadata;
import dev.rafandoo.finv.provider.coinmarketcap.request.CoinMarketCapClient;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.InfoRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.endpoint.LatestQuoteRequest;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcInfo;
import dev.rafandoo.finv.provider.coinmarketcap.request.model.CmcQuote;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

/**
 * CoinMarketCap keyless implementation of {@link MarketDataProvider} for crypto.
 *
 * <p>Supports {@code latest()} (real-time quote) and {@code instrument()} (metadata) only.
 * Historical/splits/dividends are not available in the keyless API.</p>
 */
@Slf4j
public final class CoinMarketCapMarketDataProvider implements MarketDataProvider {

    private final CoinMarketCapClient client;

    public CoinMarketCapMarketDataProvider() {
        this(new CoinMarketCapClient());
    }

    public CoinMarketCapMarketDataProvider(CoinMarketCapClient client) {
        this.client = client;
    }

    private static String convertOf(Crypto crypto) {
        String c = crypto.currency();
        return (c == null || c.isBlank()) ? "USD" : c;
    }

    @Override
    public ProviderMetadata metadata() {
        return CoinMarketCapMetadata.METADATA;
    }

    @Override
    public boolean supports(AssetType type) {
        return CoinMarketCapMetadata.METADATA.supportedAssetTypes().contains(type);
    }

    @Override
    public Optional<Instrument> instrument(Asset asset) {
        Crypto crypto = asCrypto(asset);
        return client.execute(new InfoRequest(crypto.base()))
            .map(CmcInfo::toInstrument)
            .map(i -> i.withExchange(crypto.exchange()).withCurrency(currencyOf(crypto)));
    }

    @Override
    public Optional<Quote> latest(Asset asset) {
        Crypto crypto = asCrypto(asset);
        String convert = convertOf(crypto);
        return client.execute(new LatestQuoteRequest(crypto.base(), convert))
            .map(q -> q.toQuote(asset.symbol()));
    }

    @Override
    public List<Candle> history(Asset asset, TimeRange range, Interval interval) {
        throw new UnsupportedOperationException("CoinMarketCap keyless API does not provide historical data");
    }

    @Override
    public List<DividendEvent> dividends(Asset asset) {
        throw new UnsupportedOperationException("Dividends are not applicable to crypto");
    }

    @Override
    public List<SplitEvent> splits(Asset asset) {
        throw new UnsupportedOperationException("Splits are not applicable to crypto");
    }

    private static Crypto asCrypto(Asset asset) {
        if (!(asset instanceof Crypto crypto)) {
            throw new IllegalArgumentException(
                "CoinMarketCap provider only supports Crypto assets, got: " + asset.type());
        }
        return crypto;
    }

    private static Currency currencyOf(Crypto crypto) {
        try {
            return Currency.getInstance(convertOf(crypto));
        } catch (IllegalArgumentException e) {
            return Currency.getInstance("USD");
        }
    }
}
```

> NOTE: the provider calls `CmcInfo.toInstrument()` and `CmcQuote.toQuote(String)`. These mapping methods must be added to the model records (see Step 4). Also `Instrument.withExchange(...)` / `Instrument.withCurrency(...)` — verify these exist on `Instrument`; if not, the provider should construct `Instrument` directly via its constructor instead. Prefer the constructor if withers are absent.

- [ ] **Step 4: Add mapping methods to model records**

Add to `CmcQuote.java`:

```java
    /**
     * Maps this quote to a domain {@link Quote}, deriving the absolute change from the 24h percent.
     *
     * @param symbol the domain symbol (e.g. "BTC/USDT").
     * @return the domain quote.
     */
    public Quote toQuote(String symbol) {
        BigDecimal change = null;
        if (price != null && changePercent != null) {
            BigDecimal divisor = BigDecimal.ONE.add(
                changePercent.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64));
            BigDecimal base = price.divide(divisor, MathContext.DECIMAL64);
            change = price.subtract(base);
        }
        return new Quote(symbol, price, change, changePercent, lastUpdated);
    }
```

Add to `CmcInfo.java` (import `dev.rafandoo.finv.core.market.asset.AssetType` and `dev.rafandoo.finv.core.market.data.Instrument`):

```java
    /**
     * Maps this metadata to a domain {@link Instrument}.
     *
     * @return the domain instrument (exchange/currency filled in by the provider).
     */
    public Instrument toInstrument() {
        return new Instrument(
            symbol,
            name != null ? name : symbol,
            description,
            AssetType.CRYPTO,
            null,
            null
        );
    }
```

If `Instrument` does not have withers, change the provider's `instrument()` to build `Instrument` directly:

```java
    @Override
    public Optional<Instrument> instrument(Asset asset) {
        Crypto crypto = asCrypto(asset);
        return client.execute(new InfoRequest(crypto.base()))
            .map(info -> new Instrument(
                asset.symbol(),
                info.name() != null ? info.name() : crypto.base(),
                info.description(),
                AssetType.CRYPTO,
                crypto.exchange(),
                currencyOf(crypto)
            ));
    }
```

- [ ] **Step 5: Run provider test**

Run: `./gradlew test --tests "dev.rafandoo.finv.provider.coinmarketcap.CoinMarketCapMarketDataProviderTest"`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProvider.java src/test/java/dev/rafandoo/finv/provider/coinmarketcap/CoinMarketCapMarketDataProviderTest.java src/main/java/dev/rafandoo/finv/provider/coinmarketcap/request/model/
git commit -m "feat(cmc): add provider with latest/instrument, unsupported ops throw"
```

---

## Task 8: Full build + existing tests

**Files:** none new.

- [ ] **Step 1: Run full test suite**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL, all tests (existing Yahoo/Crypto + new CMC) green.

- [ ] **Step 2: Run checkstyle/spotless if configured**

Run: `./gradlew check` (or the project's lint task) and fix any formatting/lint violations the new files introduce.
Expected: clean.

- [ ] **Step 3: Final commit if lint produced changes**

```bash
git add -A
git commit -m "style(cmc): apply lint/format fixes"
```
(Only if Step 2 changed files.)

---

## Self-review notes (verified against spec)

- **Spec coverage:** currency field (Task 1), provider structure (Tasks 2-7), metadata+features REALTIME (Task 2, corrected from spec's non-existent LATEST_QUOTE/INSTRUMENT), latest+instrument mapping (Tasks 3,5,7), UnsupportedOperationException for history/dividends/splits (Task 7), tests + manual registry (Task 7 + doc note). All spec sections map to a task.
- **Feature enum correction:** spec referenced `Feature.LATEST_QUOTE`/`Feature.INSTRUMENT` which do not exist; plan uses `Feature.REALTIME` and documents `instrument()` as always-callable (no matching feature). Spec file was updated to match.
- **Type consistency:** `CoinMarketCapRequest<T>` uses `UrlBuilder url()` + `ResponseParser<T> parser()` (mirrors `YahooRequest`). `CmcQuote.toQuote(String)` and `CmcInfo.toInstrument()` are defined in Task 5/7 and consumed in Task 7. `Quote(symbol, price, change, changePercent, date)` and `Instrument(symbol, name, fullName, assetType, exchange, currency)` constructors match the domain records.
- **Placeholders:** none — all code is concrete. Open adaptation points are marked with `NOTE` and reference the exact file to verify (`HttpRequester` API in Task 6; `Instrument` withers in Task 7).

## Registration note (no code change)

`ProviderRegistry` is manual. Consumers register the provider themselves:

```java
registry.register(new CoinMarketCapMarketDataProvider());
```

No change to `ProviderRegistry` or any wiring is required by this plan.
