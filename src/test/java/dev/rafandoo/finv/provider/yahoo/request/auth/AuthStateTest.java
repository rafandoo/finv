package dev.rafandoo.finv.provider.yahoo.request.auth;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuthStateTest {

    @Test
    void shouldBeValidWhenCookieCrumbPresentAndNotExpired() {
        Instant now = Instant.now();
        AuthState state = new AuthState("c", "b", now.plusSeconds(3600), now);
        assertThat(state.isValid()).isTrue();
        assertThat(state.isExpired()).isFalse();
    }

    @Test
    void shouldBeInvalidWhenCrumbMissing() {
        Instant now = Instant.now();
        AuthState state = new AuthState("c", null, now.plusSeconds(3600), now);
        assertThat(state.isValid()).isFalse();
    }

    @Test
    void shouldBeInvalidWhenCookieMissing() {
        Instant now = Instant.now();
        AuthState state = new AuthState(null, "b", now.plusSeconds(3600), now);
        assertThat(state.isValid()).isFalse();
    }

    @Test
    void shouldBeExpiredWhenPastExpiryMinusBuffer() {
        Instant now = Instant.now();
        AuthState state = new AuthState("c", "b", now.minusSeconds(10), now);
        assertThat(state.isExpired()).isTrue();
        assertThat(state.isValid()).isFalse();
    }
}
