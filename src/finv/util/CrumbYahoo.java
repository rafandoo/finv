package finv.util;

import finv.Finv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CrumbYahoo {

    private static final Logger logger = LogConfig.getLogger();
    private static String cookie;
    private static String crumb;

    private CrumbYahoo() {
        LogConfig.configure();
    }

    private static void obtainYahooCookie() {
        if (cookie == null) {
            logger.info("Obtaining Yahoo auth cookie, this may take a few minutes...");
            int responseCode = 0;
            try {
                String userAgentKey = "User-Agent";
                String userAgentValue = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

                URL url = new URL(Finv.YAHOO_COOKIE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty(userAgentKey, userAgentValue);

                responseCode = connection.getResponseCode();
                Map<String, List<String>> cookies = connection.getHeaderFields();
                if (cookies != null && !cookies.isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : cookies.entrySet()) {
                        if ("Set-Cookie".equalsIgnoreCase(entry.getKey())) {
                            cookie = entry.getValue().get(0);
                            break;
                        }
                    }
                }
            } catch (IOException e) {;
                logger.warning("Failed to obtain Yahoo auth cookie, response code: " + responseCode + ", error: " + e.getMessage());
            }
            logger.info("Yahoo auth cookie obtained");
        }
    }

    private static void obtainYahooCrumb() {
        if (crumb == null) {
            logger.info("Obtaining Yahoo crumb, this may take a few minutes...");
            try {
                String userAgentKey = "User-Agent";
                String userAgentValue = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

                URL url = new URL(Finv.YAHOO_CRUMB);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty(userAgentKey, userAgentValue);
                connection.setRequestProperty("Cookie", cookie);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        crumb = reader.readLine();
                    }
                } else {
                    throw new IOException("Failed to retrieve Yahoo crumb. Response code: " + responseCode);
                }
            } catch (IOException e) {
                logger.warning("Failed to retrieve Yahoo crumb: " + e.getMessage());
            }
            logger.info("Yahoo crumb obtained");
        }
    }

    /**
     * Gets cookie.
     *
     * @return the cookie
     */
    public static String getCookie() {
        obtainYahooCookie();
        return cookie;
    }

    /**
     * Gets crumb.
     *
     * @return the crumb
     */
    public static String getCrumb() {
        obtainYahooCookie();
        obtainYahooCrumb();
        return crumb;
    }
}
