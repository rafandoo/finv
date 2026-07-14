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
