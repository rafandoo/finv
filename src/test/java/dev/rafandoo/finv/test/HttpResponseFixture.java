package dev.rafandoo.finv.test;

import dev.rafandoo.cup.request.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builds {@link HttpResponse} instances from raw JSON strings without performing a real network call.
 *
 * <p>The {@link HttpResponse} constructor only accepts an {@link HttpURLConnection}, so this helper
 * mocks one and wires its input stream to the supplied JSON body. This is the only way to feed the
 * Yahoo parsers deterministic fixtures.</p>
 */
public final class HttpResponseFixture {

    private HttpResponseFixture() {
    }

    /**
     * Creates an {@link HttpResponse} whose body is the given JSON string.
     *
     * @param json the response body.
     * @return a response carrying {@code json} as its body, with status 200.
     * @throws IOException if the mocked connection rejects the stubbed calls.
     */
    public static HttpResponse json(String json) throws IOException {
        HttpURLConnection connection = mock(HttpURLConnection.class);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getInputStream()).thenReturn(body);
        when(connection.getErrorStream()).thenReturn(null);
        when(connection.getHeaderFields()).thenReturn(Map.of());
        when(connection.getContentLength()).thenReturn(-1);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");

        return new HttpResponse(connection);
    }

    /**
     * Convenience overload accepting a pre-formatted JSON resource.
     *
     * @param statusCode the HTTP status code to simulate.
     * @param json       the response body.
     * @return a response carrying {@code json} and the given status.
     * @throws IOException if the mocked connection rejects the stubbed calls.
     */
    public static HttpResponse json(int statusCode, String json) throws IOException {
        HttpURLConnection connection = mock(HttpURLConnection.class);
        InputStream body = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        when(connection.getResponseCode()).thenReturn(statusCode);
        if (statusCode >= 400) {
            when(connection.getErrorStream()).thenReturn(body);
            when(connection.getInputStream()).thenThrow(new IOException("status " + statusCode));
        } else {
            when(connection.getInputStream()).thenReturn(body);
            when(connection.getErrorStream()).thenReturn(null);
        }
        when(connection.getHeaderFields()).thenReturn(Map.of("Content-Type", List.of("application/json")));
        when(connection.getContentLength()).thenReturn(json.length());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");

        return new HttpResponse(connection);
    }
}
