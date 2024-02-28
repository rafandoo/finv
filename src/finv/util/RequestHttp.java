package finv.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class RequestHttp {

    private final URL url;
    private String method;
    private Map<String, String> headers;
    private static final Logger logger = LogConfig.getLogger();

    private RequestHttp(URL url) {
        this.url = url;
        LogConfig.configure();
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
            logger.warning("Error creating instance of RequestHttp class: " + e.getMessage());
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
        try {
            HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

            if (method != null && !method.isEmpty()) {
                connection.setRequestMethod(method);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response;
            } else {
                logger.warning("Failed to retrieve data from URL: " + url);
                logger.warning("Response code: " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (Exception e) {
            logger.warning("Error retrieving data from URL: " + e.getMessage());
        }
        return null;
    }
}
