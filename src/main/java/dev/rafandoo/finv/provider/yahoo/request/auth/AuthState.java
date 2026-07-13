package dev.rafandoo.finv.provider.yahoo.request.auth;

import java.time.Instant;

/**
 * Represents the authentication state for Yahoo Finance API requests, including the cookie, crumb, and their expiration details.
 *
 * @param cookie    the authentication cookie used for API requests.
 * @param crumb     the crumb token required for certain API requests.
 * @param expiresAt the timestamp when the authentication state expires.
 * @param createdAt the timestamp when the authentication state was created.
 */
public record AuthState(
    String cookie,
    String crumb,
    Instant expiresAt,
    Instant createdAt
) {

    /**
     * Checks if the authentication state is expired. It considers the state expired if the current time
     * is after the expiration time minus a 60-second buffer.
     *
     * @return {@code true} if the authentication state is expired, {@code false} otherwise.
     */
    boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt.minusSeconds(60L));
    }

    /**
     * Validates the authentication state by ensuring that both the cookie and crumb are present and that
     * the state is not expired.
     *
     * @return {@code true} if the authentication state is valid, {@code false} otherwise.
     */
    boolean isValid() {
        return cookie != null && crumb != null && !isExpired();
    }
}
