package br.dev.rplus.finv.provider.yahoo;

import br.dev.rplus.cup.log.Logger;
import br.dev.rplus.cup.request.HttpRequester;
import br.dev.rplus.finv.enums.RequestParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code CrumbYahoo} class is responsible for obtaining the Yahoo Finance authentication cookie and crumb.
 * These are required to make authenticated requests to the Yahoo Finance APIs.
 * <p>
 * This class uses a lazy initialization strategy to retrieve and cache the cookie and crumb on first access.
 */
public class CrumbYahoo {

    private static volatile String cookie;
    private static volatile String crumb;
    private static final Logger logger = Logger.getInstance();

    /**
     * Private constructor to prevent instantiation.
     */
    private CrumbYahoo() {}

    /**
     * Returns the Yahoo authentication cookie.
     * If the cookie has not been obtained yet, it will be retrieved from Yahoo Finance.
     *
     * @return the Yahoo authentication cookie.
     */
    public static synchronized String getCookie() {
        if (cookie == null) {
            obtainYahooCookie();
        }
        return cookie;
    }

    /**
     * Returns the Yahoo crumb.
     * If the crumb has not been obtained yet, it will be retrieved from Yahoo Finance.
     *
     * @return the Yahoo crumb.
     */
    public static synchronized String getCrumb() {
        if (crumb == null) {
            obtainYahooCrumb();
        }
        return crumb;
    }

    /**
     * Obtains the Yahoo authentication cookie if not already retrieved.
     */
    private static void obtainYahooCookie() {
        logger.info("Obtaining Yahoo authentication cookie...");

        try {
            HttpURLConnection conn = sendRequest(RequestParams.YAHOO_COOKIE.get().asString(), Map.of(
                "User-Agent", getUserAgent()
            ));

            Optional.ofNullable(conn.getHeaderFields())
                .map(headers -> headers.get("Set-Cookie"))
                .flatMap(list -> list.stream().findFirst())
                .ifPresentOrElse(
                    c -> {
                        cookie = c;
                        logger.debug("Yahoo auth cookie obtained: %s", cookie);
                    },
                    () -> logger.warn("No 'Set-Cookie' header found in the response.")
                );

            conn.disconnect();
        } catch (IOException e) {
            logger.warn("Failed to obtain Yahoo authentication cookie.", e);
        }
    }

    /**
     * Obtains the Yahoo crumb if not already retrieved.
     */
    private static void obtainYahooCrumb() {
        if (cookie == null) {
            obtainYahooCookie();
        }

        logger.info("Obtaining Yahoo crumb...");

        try {
            HttpURLConnection conn = sendRequest(RequestParams.YAHOO_CRUMB.get().asString(), Map.of(
                "User-Agent", getUserAgent(),
                "Cookie", cookie
            ));

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    crumb = reader.readLine();
                    logger.debug("Yahoo crumb obtained: %s", crumb);
                }
            } else {
                logger.warn("Failed to retrieve Yahoo crumb. Response code: %d", conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            logger.warn("Failed to retrieve Yahoo crumb.", e);
        }
    }

    /**
     * Sends an HTTP request with the specified headers.
     */
    private static HttpURLConnection sendRequest(String url, Map<String, String> headers) throws IOException {
        return HttpRequester.builder()
            .url(url)
            .method(HttpRequester.HttpMethod.GET)
            .connectionTimeout(RequestParams.TIMEOUT.get().asInteger())
            .headers(headers)
            .sendRaw();
    }

    /**
     * Returns the User-Agent string used for HTTP requests.
     */
    private static String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";
    }
}
