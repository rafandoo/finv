package dev.rafandoo.finv.core.exception;

/**
 * Exception thrown when a request to a market data provider fails.
 * Carries the HTTP status code (when available) so callers can distinguish
 * transport/network failures ({@code statusCode < 0}) from server-side errors.
 */
public class ProviderRequestException extends RuntimeException {

    private final int statusCode;

    public ProviderRequestException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ProviderRequestException(String message, Throwable cause) {
        this(message, -1, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Indicates whether the failure looks like an authentication/authorization error
     * (HTTP 401 or 403) and is therefore a candidate for a re-authentication retry.
     *
     * @return {@code true} if the status code is 401 or 403.
     */
    public boolean isAuthFailure() {
        return statusCode == 401 || statusCode == 403;
    }
}
