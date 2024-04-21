package br.dev.rplus.finv.util;

import br.dev.rplus.cup.log.LoggerCup;
import br.dev.rplus.finv.config.Paramters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CrumbYahoo {
    private static String cookie;
    private static String crumb;

    private static void obtainYahooCookie() {
        if (cookie == null) {
            LoggerCup.info("Obtaining Yahoo auth cookie, this may take a few minutes...");
            int response = 0;
            HttpURLConnection conn = null;
            try {
                String userAgentKey = "User-Agent";
                String userAgentValue = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

                URL url = new URL(Paramters.YAHOO_COOKIE);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty(userAgentKey, userAgentValue);

                response = conn.getResponseCode();
                Map<String, List<String>> cookies = conn.getHeaderFields();
                if (cookies != null && !cookies.isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : cookies.entrySet()) {
                        if ("Set-Cookie".equalsIgnoreCase(entry.getKey())) {
                            cookie = entry.getValue().get(0);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                LoggerCup.warn("Failed to obtain Yahoo auth cookie, response code: %s.", response, e);
            } finally {
                assert conn != null;
                conn.disconnect();
            }
            LoggerCup.debug("Yahoo auth cookie obtained: {%s}", cookie);
        }
    }

    private static void obtainYahooCrumb() {
        if (crumb == null) {
            LoggerCup.info("Obtaining Yahoo crumb, this may take a few minutes...");
            int response = 0;
            HttpURLConnection conn = null;
            try {
                String userAgentKey = "User-Agent";
                String userAgentValue = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

                URL url = new URL(Paramters.YAHOO_CRUMB);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty(userAgentKey, userAgentValue);
                conn.setRequestProperty("Cookie", cookie);

                response = conn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        crumb = reader.readLine();
                    }
                }

            } catch (IOException e) {
                LoggerCup.warn("Failed to retrieve Yahoo crumb, response code: %s", response, e);
            } finally {
                assert conn != null;
                conn.disconnect();
            }
            LoggerCup.debug("Yahoo crumb obtained: {%s}", crumb);
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
