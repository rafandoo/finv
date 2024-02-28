package finv.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

public class UrlBuilder {

    private static final Logger logger = LogConfig.getLogger();
    private final StringBuilder url;

    private UrlBuilder(String url) {
        LogConfig.configure();
        this.url = new StringBuilder(url);
    }

    /**
     * Creates a new instance of the UrlBuilder class from the given URL.
     *
     * @param url the URL string representing the initial state of the UrlBuilder
     * @return a new instance of the UrlBuilder class
     */
    public static UrlBuilder from(String url) {
        return new UrlBuilder(url);
    }

    /**
     * Adds the given parameters to the URL.
     *
     * @param params a map of key-value pairs representing the parameters to add
     * @return the updated UrlBuilder object
     */
    public UrlBuilder addParameter(Map<String, String> params) {
        if (url.indexOf("?") == -1) {
            url.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (url.indexOf("?") != url.length() - 1) {
                url.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                key = URLEncoder.encode(key, "UTF-8");
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.warning("Failed to encode parameter: " + key + ", value: " + value + ", error: " + e.getMessage());
                continue;
            }
            url.append(String.format("%s=%s", key, value));
        }
        return this;
    }

    /**
     * Builds the URL and returns it as a string.
     *
     * @return the URL as a string
     */
    public String build() {
        return url.toString();
    }
}
