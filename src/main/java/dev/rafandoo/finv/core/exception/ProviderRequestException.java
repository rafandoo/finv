package dev.rafandoo.finv.core.exception;

/**
 * Exception thrown when an error occurs while making a request to a provider.
 */
public class ProviderRequestException extends RuntimeException {

    public ProviderRequestException(String message) {
        super(message);
    }

    public ProviderRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderRequestException(Throwable cause) {
        super(cause);
    }
}
