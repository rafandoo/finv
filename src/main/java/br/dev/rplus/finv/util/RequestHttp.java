package br.dev.rplus.finv.util;

import br.dev.rplus.cup.log.LoggerCup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RequestHttp {
    private final URL url;
    private String method;
    private Map<String, String> headers;

    private RequestHttp(URL url) {
        this.url = url;
    }

    /**
     * Creates a new instance of the `RequestHttp` class from a given URL string.
     *
     * @param urlString the URL string to create the `RequestHttp` instance from
     * @return the created `RequestHttp` instance
     */
    public static RequestHttp fromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return new RequestHttp(url);
        } catch (Exception e) {
            LoggerCup.error("Error creating instance of RequestHttp class.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the HTTP method for the request.
     *
     * @param method the HTTP method to be set
     * @return the updated RequestHttp object
     */
    public RequestHttp method(String method) {
        this.method = method;
        return this;
    }

    /**
     * Sets the content type of the HTTP request.
     *
     * @param headers the content type to set
     * @return the updated RequestHttp object
     */
    public RequestHttp headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Retrieves the response from the specified URL using an HTTP GET request.
     *
     * @return a StringBuilder object containing the response from the URL
     */
    public StringBuilder get() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) this.url.openConnection();

            if (method != null && !method.isEmpty()) {
                conn.setRequestMethod(method);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response;
            } else {
                LoggerCup.warn("Failed to retrieve data from URL: %s", url);
                LoggerCup.warn("Response code: %s.", conn.getResponseCode());
            }
        } catch (Exception e) {
            LoggerCup.error("Error retrieving data from URL.", e);
        } finally {
            assert conn != null;
            conn.disconnect();
        }
        return null;
    }
}
