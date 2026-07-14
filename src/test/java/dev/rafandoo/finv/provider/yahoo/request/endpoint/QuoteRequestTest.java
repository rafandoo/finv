package dev.rafandoo.finv.provider.yahoo.request.endpoint;

import dev.rafandoo.cup.request.UrlBuilder;
import dev.rafandoo.finv.provider.ResponseParser;
import dev.rafandoo.finv.provider.yahoo.request.model.QuoteData;
import dev.rafandoo.finv.provider.yahoo.request.parse.QuoteParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteRequestTest {

    @Test
    void shouldBuildUrlWithSymbol() {
        UrlBuilder url = new QuoteRequest("AAPL").url();
        String built = url.toString();
        assertThat(built).contains("AAPL");
        assertThat(built).contains("quote");
    }

    @Test
    void shouldUseQuoteParser() {
        ResponseParser<QuoteData> parser = new QuoteRequest("AAPL").parser();
        assertThat(parser).isInstanceOf(QuoteParser.class);
    }

    @Test
    void shouldRequireCrumbByDefault() {
        assertThat(new QuoteRequest("AAPL").requiresCrumb()).isTrue();
    }
}
