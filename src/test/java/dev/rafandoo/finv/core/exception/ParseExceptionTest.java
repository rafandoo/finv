package dev.rafandoo.finv.core.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParseExceptionTest {

    @Test
    void shouldCarryMessage() {
        assertThat(new ParseException("bad")).hasMessage("bad").hasNoCause();
    }

    @Test
    void shouldCarryMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        assertThat(new ParseException("bad", cause)).hasMessage("bad").hasCause(cause);
    }

    @Test
    void shouldCarryCauseOnly() {
        Throwable cause = new RuntimeException("root");
        assertThat(new ParseException(cause)).hasCause(cause);
    }
}
