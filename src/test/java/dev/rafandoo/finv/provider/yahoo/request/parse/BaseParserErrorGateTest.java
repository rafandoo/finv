package dev.rafandoo.finv.provider.yahoo.request.parse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.rafandoo.finv.core.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseParserErrorGateTest {

    /** Concrete subclass so the protected helpers can be exercised directly. */
    private static final class Probe extends BaseParser {
    }

    private static JsonNode json(String body) throws Exception {
        return Probe.MAPPER.readTree(body);
    }

    @Test
    void shouldReportApiErrorWhenTopLevelErrorPresent() throws Exception {
        JsonNode root = json("{ \"error\": { \"description\": \"bad\" } }");
        assertThat(new Probe().hasApiError(root)).isTrue();
    }

    @Test
    void shouldReportApiErrorWhenChartErrorPresent() throws Exception {
        JsonNode root = json("{ \"chart\": { \"error\": { \"description\": \"bad\" }, \"result\": [] } }");
        assertThat(new Probe().hasApiError(root)).isTrue();
    }

    @Test
    void shouldReportApiErrorWhenResultEmpty() throws Exception {
        JsonNode root = json("{ \"quoteResponse\": { \"result\": [], \"error\": null } }");
        assertThat(new Probe().hasApiError(root)).isTrue();
    }

    @Test
    void shouldNotReportApiErrorForValidPayload() throws Exception {
        JsonNode root = json("{ \"quoteResponse\": { \"result\": [ { \"symbol\": \"AAPL\" } ], \"error\": null } }");
        assertThat(new Probe().hasApiError(root)).isFalse();
    }

    @Test
    void shouldReportApiErrorWhenRootIsNull() {
        assertThat(new Probe().hasApiError(null)).isTrue();
    }

    @Test
    void shouldReturnFirstElementOfArray() throws Exception {
        JsonNode root = json("[ { \"a\": 1 }, { \"a\": 2 } ]");
        Optional<JsonNode> first = new Probe().firstElement(root);
        assertThat(first).isPresent();
        assertThat(first.get().path("a").asInt()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyWhenFirstElementCalledOnNonArray() throws Exception {
        JsonNode root = json("{ \"a\": 1 }");
        assertThat(new Probe().firstElement(root)).isEmpty();
    }

    @Test
    void shouldThrowWhenRequiredTextMissing() {
        BaseParser probe = new Probe();
        assertThatThrownBy(() -> probe.getRequiredText(json("{ \"a\": 1 }"), "missing"))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldReturnEmptyWhenOptionalTextMissing() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalText(json("{ \"a\": 1 }"), "missing")).isEmpty();
    }

    @Test
    void shouldReadApiErrorMessageFromTopLevelError() throws Exception {
        JsonNode root = json("{ \"error\": { \"code\": \"bad\", \"description\": \"nope\" } }");
        assertThat(new Probe().apiErrorMessage(root).description()).isEqualTo("nope");
    }

    @Test
    void shouldReadApiErrorMessageFromChartError() throws Exception {
        JsonNode root = json("{ \"chart\": { \"error\": { \"description\": \"chart bad\" } } }");
        assertThat(new Probe().apiErrorMessage(root).description()).isEqualTo("chart bad");
    }

    @Test
    void shouldReturnNullApiErrorMessageWhenNoError() throws Exception {
        JsonNode root = json("{ \"quoteResponse\": { \"result\": [] } }");
        assertThat(new Probe().apiErrorMessage(root)).isNull();
    }

    @Test
    void shouldReturnValueWhenRequiredTextPresent() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getRequiredText(json("{ \"a\": \"hello\" }"), "a")).isEqualTo("hello");
    }

    @Test
    void shouldReturnValueWhenOptionalTextPresent() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalText(json("{ \"a\": \"hello\" }"), "a")).contains("hello");
    }

    @Test
    void shouldThrowWhenRequiredDecimalMissing() {
        BaseParser probe = new Probe();
        assertThatThrownBy(() -> probe.getRequiredDecimal(json("{ \"a\": 1 }"), "missing"))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldReturnValueWhenOptionalDecimalPresent() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalDecimal(json("{ \"a\": 1.5 }"), "a"))
            .contains(new java.math.BigDecimal("1.5"));
    }

    @Test
    void shouldReturnEmptyWhenOptionalDecimalMissing() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalDecimal(json("{ \"a\": 1 }"), "missing")).isEmpty();
    }

    @Test
    void shouldThrowWhenRequiredArrayMissing() {
        BaseParser probe = new Probe();
        assertThatThrownBy(() -> probe.getRequiredArray(json("{ \"a\": 1 }"), "missing"))
            .isInstanceOf(ParseException.class);
    }

    @Test
    void shouldReturnArrayWhenOptionalArrayPresent() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalArray(json("{ \"a\": [1, 2] }"), "a")).isPresent();
    }

    @Test
    void shouldReturnEmptyWhenOptionalArrayNotArray() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalArray(json("{ \"a\": 1 }"), "a")).isEmpty();
    }

    @Test
    void shouldReadDecimalAtIndexOrNull() throws Exception {
        BaseParser probe = new Probe();
        JsonNode array = json("[1.5, null]");
        assertThat(probe.getDecimal(array, 0)).isEqualTo(new java.math.BigDecimal("1.5"));
        assertThat(probe.getDecimal(array, 1)).isNull();
        assertThat(probe.getDecimal(array, 2)).isNull();
    }

    @Test
    void shouldReportMissingForNullAndMissingNodes() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.isMissing(null)).isTrue();
        assertThat(probe.isMissing(json("null"))).isTrue();
        assertThat(probe.isMissing(json("{}").path("nope"))).isTrue();
        assertThat(probe.isMissing(json("{ \"a\": 1 }").path("a"))).isFalse();
    }

    @Test
    void shouldReturnValueWhenOptionalBigIntegerPresent() throws Exception {
        BaseParser probe = new Probe();
        assertThat(probe.getOptionalBigInteger(json("{ \"a\": 100 }"), "a"))
            .contains(java.math.BigInteger.valueOf(100));
    }
}
