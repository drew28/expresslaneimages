package com.atreid.expresslanesimages;

import package com.atreid.expresslanesimages.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * A utility class that provides methods to make HTTP requests and RESTful webservice calls.
 *
 * The methods in this class are implemented using HttpURLConnection
 * @see HttpURLConnection
 *
 * @author midris
 */
public class HttpClient {

    /**
     * Make an HTTP request
     *
     * @param method      The HTTP request method to be used, e.g. GET, PUT, POST, DELETE, etc
     * @param url         The full URL that the HTTP request should be made to
     * @param contentType The request content type
     * @param headers     The headers that should be set for the HTTP request
     * @param payload     The payload for the REST service response. This will be sent in the HTTP request body
     * @return the http request response
     * @throws IOException if something goes wrong while making the request. The method can throw specifically throw
     * MalformedURLException as well ProtocolException along with IOException.
     */
    public HttpResponse makeHttpRequest(String url, String method, String contentType, Map<String, String> headers, String payload)
            throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        OutputStreamWriter writer = null;
        try {
            log.info("Making HTTP " + method + " request...");
            log.debug("URL=" + url);

            // Create connection
            conn = (HttpURLConnection) new URL(url).openConnection();
            // Set method
            conn.setRequestMethod(method);
            // Set content type
            conn.setRequestProperty(HttpRequestProperties.CONTENT_TYPE_HEADER, contentType);

            // Add request properties
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    if (header.getKey() != null && header.getValue() != null) {
                        conn.setRequestProperty(header.getKey(), header.getValue());
                    }
                }
            }

            // Write payload
            if (payload != null && !"".equals(payload.trim())) {
                conn.setDoOutput(true);
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(payload);
                writer.flush();
            }

            // Get response code
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            String responseBody = null;
            log.debug("HTTP request completed with responseCode=" + responseCode + ", and responseMessage=" + responseMessage);

            // Read response if request was successful
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                responseBody = sb.toString();
            }

            // Return HTTP Response
            return new HttpResponse(responseCode, responseMessage, responseBody);
        }
        finally {
            log.debug("Cleaning up connection resources...");
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    log.warn("IOException while cleaning up resources", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    log.warn("IOException while cleaning up resources", e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
