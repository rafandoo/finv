package dev.rafandoo.finv.core.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderRequestExceptionTest {

    @Test
    void shouldCarryMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        ProviderRequestException ex = new ProviderRequestException("boom", cause);
        assertThat(ex).hasMessage("boom").hasCause(cause);
        assertThat(ex.getStatusCode()).isEqualTo(-1);
    }

    @Test
    void shouldCarryMessageStatusCodeAndCause() {
        Throwable cause = new RuntimeException("root");
        ProviderRequestException ex = new ProviderRequestException("boom", 403, cause);
        assertThat(ex).hasMessage("boom").hasCause(cause);
        assertThat(ex.getStatusCode()).isEqualTo(403);
        assertThat(ex.isAuthFailure()).isTrue();
    }

    @Test
    void shouldNotBeAuthFailureForNonAuthStatusCode() {
        ProviderRequestException ex = new ProviderRequestException("boom", 500, new RuntimeException());
        assertThat(ex.isAuthFailure()).isFalse();
    }
}
