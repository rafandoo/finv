package finv.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHttp {

    private final URL url;
    private String method;
    private String contentType;

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
            throw new RuntimeException("Erro ao criar a instância da classe RequestHttp", e);
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
     * @param contentType the content type to set
     * @return the updated RequestHttp object
     */
    public RequestHttp contentType(String contentType) {
        this.contentType = contentType;
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

            if (contentType != null && !contentType.isEmpty()) {
                connection.setRequestProperty("Content-Type", contentType);
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
                System.err.println("Erro na requisição: " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
