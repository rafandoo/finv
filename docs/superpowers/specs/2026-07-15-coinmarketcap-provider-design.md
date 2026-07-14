# CoinMarketCap Crypto Provider — Design

**Date:** 2026-07-15
**Status:** Approved (design)
**Scope:** New `coinmarketcap` provider for crypto quotes/metadata via the CoinMarketCap keyless public API, plus an optional `currency` field on the `Crypto` asset.

## Context

`finv` already has a `Crypto` asset type (`AssetType.CRYPTO`) and a `MarketDataProvider` abstraction, but no provider implements crypto yet. The only existing provider is `yahoo` (stocks/ETFs). This design adds a second provider that fetches **real-time crypto quotes** and **instrument metadata** from CoinMarketCap's keyless public API.

### CoinMarketCap keyless API constraints

The keyless public API (base host `https://pro-api.coinmarketcap.com/public-api`, **no API key / no auth header required**) exposes only:

- `GET /quotes/latest?symbol=BTC&convert=USD` — real-time quote (price, 24h volume, 24h percent change, last updated).
- `GET /info?symbol=BTC` — metadata (name, slug, description, category, logo, urls).
- `GET /cryptocurrency/map` — id/symbol map (not needed here).

It does **not** provide historical OHLCV candles, dividends, or splits in the keyless tier. Therefore the provider supports only `LATEST_QUOTE` and `INSTRUMENT` features; `history()`, `dividends()`, and `splits()` throw `UnsupportedOperationException`.

### Existing patterns (must be followed)

- `MarketDataProvider` interface: `metadata()`, `supports(AssetType)`, `instrument(Asset)`, `latest(Asset)`, `history(...)`, `dividends(...)`, `splits(...)`.
- `YahooMarketDataProvider` follows a layered structure: `request/` client → `endpoint/*Request` (url + parser) → `parse/*Parser` (JSON → model record) → `model/*` (model record → domain type).
- Parsers extend `provider/yahoo/request/parse/BaseParser` (Jackson `ObjectMapper`, error gating) and return `Optional<Model>`.
- HTTP is done via `dev.rafandoo.cup.request.HttpRequester` + `UrlBuilder`.
- Tests use `src/test/.../test/HttpResponseFixture.json(...)` + AssertJ, mirroring `provider/yahoo/.../*Test`.
- `ProviderRegistry` is manual: callers call `register(provider)`. No wiring change needed.

## Approach

Mirror the Yahoo provider structure under `provider/coinmarketcap/`. Reuse `cup`'s `HttpRequester` (GET, no auth). No auth/key layer (keyless).

## Part 1 — Asset model change

`Crypto` gains an **optional** `currency` field (nullable; defaults to `"USD"` when blank). This is the fiat used as the CMC `convert` parameter.

```java
public record Crypto(
    String base,      // e.g. "BTC"
    String quote,     // trading-pair quote, e.g. "USDT"
    String exchange,  // e.g. "Binance"
    String currency   // optional fiat for quoting, e.g. "USD" — CMC convert; null/""
) implements Asset {
    public Crypto {
        Objects.requireNonNull(base);
        Objects.requireNonNull(quote);
        Objects.requireNonNull(exchange);
        // currency stays nullable / optional
    }
    @Override public String symbol() { return base + "/" + quote; }
    @Override public AssetType type() { return AssetType.CRYPTO; }
}
```

- `symbol()` unchanged (`base/quote`).
- `currency` is optional (nullable). The provider treats blank/null as `"USD"`.
- Only `Crypto` changes; `Asset`, `Stock`, and other assets are untouched.

## Part 2 — Provider structure

```
provider/coinmarketcap/
├── CoinMarketCapMetadata.java            # ProviderMetadata
├── CoinMarketCapMarketDataProvider.java  # implements MarketDataProvider
└── request/
    ├── CoinMarketCapClient.java          # GET via cup HttpRequester (no auth)
    ├── model/
    │   ├── CmcQuote.java                  # /quotes/latest response model
    │   └── CmcInfo.java                   # /info response model
    ├── parse/
    │   ├── BaseParser.java                # shared: Jackson + CMC error-code gating
    │   ├── QuoteParser.java               # CmcQuote -> Quote
    │   └── InfoParser.java                # CmcInfo -> Instrument
    └── endpoint/
        ├── CoinMarketCapRequest.java      # interface: buildUrl() + parser()
        ├── LatestQuoteRequest.java        # /quotes/latest?symbol=&convert=
        └── InfoRequest.java               # /info?symbol=
```

- `CoinMarketCapClient` performs a plain GET against `https://pro-api.coinmarketcap.com/public-api` using `HttpRequester` — **no API key header** (keyless).
- CMC signals errors via `status.error_code != 0` in the JSON body. `BaseParser` checks this and throws `ParseException` with `status.error_message`.
- `latest()` maps `CmcQuote` → `Quote`.
- `instrument()` maps `CmcInfo` → `Instrument`.
- `history()` / `dividends()` / `splits()` throw `UnsupportedOperationException`.

### `CoinMarketCapRequest` interface

Analogous to `yahoo/request/endpoint/YahooRequest` (uses `cup`'s `UrlBuilder` and the shared `ResponseParser<T>` from `dev.rafandoo.finv.provider`):

```java
public interface CoinMarketCapRequest<T> {
    UrlBuilder url();
    ResponseParser<T> parser();
}
```

Each endpoint holds the path + query params (via `UrlBuilder`) and returns its `ResponseParser`. No crumb/auth is needed for the keyless API, so no `requiresCrumb()` method.

## Part 3 — Metadata, features, mapping

### `CoinMarketCapMetadata`

- `id`: `"coinmarketcap"` (stable string id).
- `supportedAssetTypes`: `Set.of(AssetType.CRYPTO)`.
- `supportedFeatures`: `Set.of(Feature.REALTIME)`.

> **Correção de spec:** o enum `Feature` existente só tem `HISTORY, REALTIME, DIVIDENDS, SPLITS, FUNDAMENTALS`. Não existem `LATEST_QUOTE` nem `INSTRUMENT`. A cotação em tempo real mapeia para `Feature.REALTIME`. `instrument()` é parte do contrato `MarketDataProvider` e permanece implementado e chamável, mas não há um valor de `Feature` que o represente para o `ProviderRegistry`; portanto não é declarado em `supportedFeatures`.

### `CmcQuote` → `Quote` (domain `Quote(symbol, price, change, changePercent, date)`)

From `/quotes/latest`, keyed by the `convert` currency:

- `data[symbol].quote[convert].price` → `Quote.price`
- `data[symbol].quote[convert].volume_24h` → (available on model; not in domain `Quote` — kept on `CmcQuote` for completeness)
- `data[symbol].quote[convert].percent_change_24h` → `Quote.changePercent`
- `data[symbol].quote[convert].last_updated` → `Quote.date` (ISO-8601 → `Instant`)
- `Quote.symbol` → `asset.symbol()` (e.g. `BTC/USD`).
- **Absolute change**: CMC returns only the 24h percent change, not an absolute change. Derive it: `change = price - price / (1 + percentChange24h/100)`. Store as `Quote.change` (rounded to the price scale).
- `convert` (the fiat used) is retained on `CmcQuote` for reference.

### `CmcInfo` → `Instrument` (domain `Instrument(symbol, name, fullName, assetType, exchange, currency)`)

From `/info`:

- `data[symbol].name` → `Instrument.name`
- `data[symbol].name` → `Instrument.fullName` (CMC has no separate full name; reuse `name`)
- `data[symbol].symbol` (slug) → `Instrument.symbol`
- `AssetType.CRYPTO` (fixed).
- `Instrument.exchange` → from the `Crypto.exchange` field (CMC `/info` is exchange-agnostic).
- `Instrument.currency` → `Currency.getInstance(crypto.currencyOrDefault())` (USD default).

### Error handling

- `status.error_code != 0` → `ParseException(status.error_message)`.
- Symbol not found / empty `data` for the symbol → parser returns `Optional.empty()` (consistent with Yahoo's empty-result handling).
- Network/IO errors propagate as `IOException` from the client (consistent with Yahoo).

## Part 4 — Tests and registration

### Tests (`src/test/java/dev/rafandoo/finv/provider/coinmarketcap/...`)

Mirror the Yahoo test layout; use `dev.rafandoo.finv.test.HttpResponseFixture.json(...)` + AssertJ:

- `CoinMarketCapMetadataTest` — id, supported asset types (`CRYPTO`), supported features (`REALTIME`).
- `request/parse/QuoteParserTest` — valid `/quotes/latest` JSON → correct `Quote` (price, changePercent, derived change, date, currency). Cases: valid; empty `data` → `Optional.empty()`; `status.error_code != 0` → `ParseException`.
- `request/parse/InfoParserTest` — valid `/info` JSON → correct `Instrument`.
- `CoinMarketCapMarketDataProviderTest` — `latest()` and `instrument()` work with a client backed by fixture JSON; `history()`, `dividends()`, `splits()` throw `UnsupportedOperationException`; `currency` null/default → `USD` convert.
- Update `core/market/asset/CryptoTest` — covers `currency` null and populated.

### Registration

`ProviderRegistry` is manual; no change required there. Document that consumers register the provider:

```java
registry.register(new CoinMarketCapMarketDataProvider());
```

## Out of scope (YAGNI)

- Historical OHLCV / candles (not in keyless API).
- Dividends / splits (not applicable to crypto).
- API-key / pro-tier endpoints.
- Generic `AbstractMarketDataProvider` refactor of the Yahoo provider.
- Listings / top-movers feeds (no clean fit with the per-asset interface).

## Acceptance criteria

1. `new Crypto("BTC", "USDT", "Binance", "USD")` resolves via `CoinMarketCapMarketDataProvider.latest()` to a `Quote` with correct price and `changePercent`.
2. `currency = null` defaults the CMC `convert` to `USD`.
3. `instrument()` returns an `Instrument` with `assetType = CRYPTO`.
4. `history()` / `dividends()` / `splits()` throw `UnsupportedOperationException`.
5. All new unit tests pass; existing tests remain green.
