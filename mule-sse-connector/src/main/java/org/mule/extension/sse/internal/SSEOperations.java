package org.mule.extension.sse.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Defines the operations available for the SSE (Server-Sent Events) Mule 4 connector.
 * <p>
 * This class contains methods that can be invoked within a Mule application.
 * Operations include invoking an SSE endpoint and parsing streamed events into
 * {@link SSEEvent} objects.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Builds properly configured HTTP requests for SSE endpoints.</li>
 *   <li>Constructs URLs by combining base URL, path, and query parameters.</li>
 *   <li>Executes blocking HTTP GET calls to SSE servers.</li>
 *   <li>Parses raw SSE event blocks into {@link SSEEvent} POJOs.</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> Current parsing logic is intentionally simplistic and optimized
 * for a proof-of-concept with the Constructor “Retrieve by intent” API.
 * It should be refactored for production-grade usage.
 * </p>
 *
 * @since 1.0
 */
@DisplayName("SSE Connector Operations")
public class SSEOperations {


    /**
     * Shared Jackson ObjectMapper for parsing SSE data payloads, which Constructor sends as JSON objects (thread-safe
     * after configuration).
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Shared HttpClient instance (thread-safe, supports connection pooling).
     */
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /**
     * Utility method to build a fully configured {@link HttpRequest} for sending HTTP GET requests.
     * <p>
     * This method is defensive against invalid input from user-provided data, including:
     * <ul>
     *     <li>{@code url} being null, blank, or malformed</li>
     *     <li>{@code headers} map being null or containing null/blank keys or values</li>
     *     <li>Invalid header names/values</li>
     *     <li>Negative or zero response timeout values</li>
     * </ul>
     * <p>
     * Headers with invalid names/values are silently skipped. If the URL is invalid or null/blank,
     * the method returns {@code null}.
     *
     * @param url             The target URL for the HTTP GET request. Must be non-null and non-blank.
     * @param headers         Optional map of HTTP headers to add. Keys and values must be non-null and non-blank.
     * @param responseTimeout Optional response timeout in milliseconds. Ignored if <= 0.
     * @return A configured {@link HttpRequest} object ready to be sent, or {@code null} if the URL is invalid.
     */
    private static HttpRequest buildGetRequest(String url, Map<String, String> headers, long responseTimeout) {
        // Defensive check: URL must not be null or blank
        if (url == null || url.isBlank()) {
            return null;
        }

        URI uri;
        try {
            // Parse the URL into a URI
            uri = URI.create(url.trim());
        } catch (IllegalArgumentException e) {
            // Malformed URL, cannot build request
            return null;
        }

        // Create a new HttpRequest.Builder with the target URI
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri);

        // Add headers if provided
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry == null) continue; // Extra safety, though standard Maps never contain null entries

                String key = entry.getKey();
                String value = entry.getValue();

                // Skip headers with null or blank keys/values
                if (key == null || key.isBlank() || value == null || value.isBlank()) continue;

                try {
                    // Add the header; trims whitespace from key and value
                    requestBuilder.header(key.trim(), value.trim());
                } catch (IllegalArgumentException ignored) {
                    // Silently skip headers with illegal characters
                }
            }
        }

        // Set the response timeout if specified
        if (responseTimeout > 0) {
            requestBuilder.timeout(Duration.ofMillis(responseTimeout));
        }

        // Build and return the GET request
        return requestBuilder.GET().build();
    }

    /**
     * Retrieves SSE events from the configured SSE endpoint.
     * <p>
     * This operation executes synchronously, blocking until:
     * <ul>
     *   <li>The SSE server closes the connection, or</li>
     *   <li>The configured timeout is exceeded.</li>
     * </ul>
     *
     * @param operationParameters A group of request parameters (headers, path, query params).
     * @param config      Connector-level configuration (base URL, response timeout).
     * @return A list of parsed {@link SSEEvent} objects. May be empty if no events are received.
     */
    @MediaType(value = MediaType.ANY, strict = false)
    @DisplayName("Get Events")
    public List<SSEEvent> getSSEEvents(
            @ParameterGroup(name = "Request") GetEventsParameters operationParameters,
            @Config SSEConfiguration config
    ) {
        List<SSEEvent> events = new ArrayList<>();

        // Build the SSE endpoint's URL using the base URL and path settings, and any provided query parameters
        String url = buildUrl(config.getSSEServerBaseURL(), operationParameters.getPath(), operationParameters.getQueryParams());

        // Create a new GET request for invoking the SSE endpoint. This request is intentionally synchronous, blocking
        // to receive all SSE events, waiting for the SSE endpoint to close the connection, or the specified timeout to
        // be exceeded.
        HttpRequest request = buildGetRequest(url, operationParameters.getHeaders(), config.getResponseTimeout());

        try {
            // Send the request and capture the entire response - i.e., all streamed events.
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // Get the response body
            String responseBody = response.body();

            if (responseBody != null && !responseBody.isBlank()) {
                // TODO: Refactor this code block to make it more generic and handle any SSE event. It is a temporary
                //       implementation specific to the Constructor Retrieve by intent API, which does not send the id:
                //       line as witnessed while testing with over 30 requests; it only sends the event: and data: lines.
                //       Hence, the regular expression below successfully splits the response into individual events
                //       during the proof of concept and demo.
                //
                // Split the response into individual events intentionally using the regular expression below, as some
                // of the search results may contain "\n\n", which causes parsing issues.
                String[] eventBlocks = responseBody.split("(?=\\nevent:)");

                // Loop through all events and parse them using the convenience method below.
                for (String eventBlock : eventBlocks) {
                    events.add(parseEvent(eventBlock));
                }
            }
        } catch (IOException ioe) {
            // Network issues, server unreachable, or broken connection.
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            // Thread interrupted waiting for response
            ie.printStackTrace();
        } catch (Exception e) {
            // Any other exception
            e.printStackTrace();
        }

        return events; // return List<SSEEvent> directly
    }

    /**
     * Builds a complete URL string by combining a base URL, an optional path,
     * and optional query parameters. Query parameters are appended only if
     * they are valid (non-null, non-blank key and value).
     * <p>
     * This method is defensive against:
     * <ul>
     *   <li>{@code queryParams} being {@code null} or empty</li>
     *   <li>Individual map entries being {@code null}</li>
     *   <li>Keys or values that are {@code null} or blank</li>
     * </ul>
     * <p>
     * Keys and values are URL-encoded using UTF-8 to ensure they are safe
     * for inclusion in a URL.
     *
     * @param baseUrl     The base URL (e.g., "https://localhost").
     * @param path        The resource path (e.g., "/api/contacts"). May be {@code null}.
     * @param queryParams A map of query parameters to append, may be {@code null} or empty.
     * @return A fully constructed URL string with query parameters appended if present.
     */
    private String buildUrl(String baseUrl, String path, Map<String, String> queryParams) {
        // Base URL is a required connector setting.
        // Checking regardless that it non-null and non-blank
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }

        // Prepare to append path and query parameters
        StringBuilder urlBuilder = new StringBuilder(baseUrl.trim());

        // Append path if provided
        if (path != null && !path.isBlank()) {
            if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append(path.trim());
        }

        // Combine base URL and path; if path is null, treat it as an empty string
        String url = baseUrl + (path != null ? path : "");

        // If there are no query parameters, return the URL as-is
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }

        // Append query parameters if provided
        if (queryParams != null && !queryParams.isEmpty()) {
            StringJoiner joiner = new StringJoiner("&", urlBuilder.toString().contains("?") ? "&" : "?", "");

            // Iterate over each query parameter
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (entry == null) continue; // Extra safety, though standard Maps never contain null entries

                String key = entry.getKey();
                String value = entry.getValue();

                // Skip invalid entries (null or blank keys/values)
                if (key == null || key.isBlank() || value == null || value.isBlank()) continue;

                // Encode key and value to ensure special characters are safely included
                joiner.add(URLEncoder.encode(key.trim(), StandardCharsets.UTF_8) +
                        "=" + URLEncoder.encode(value.trim(), StandardCharsets.UTF_8));
            }
            if (joiner.length() > 1) { // Only append if there are valid parameters
                urlBuilder.append(joiner);
            }
        }

        // Return the final URL string
        return urlBuilder.toString();
    }

    /**
     * Parses a raw Server-Sent Event (SSE) block string into an {@link SSEEvent} object.
     * <p>
     * <b>Important:</b> This implementation is rudimentary and was designed as a quick-and-dirty solution.
     * It may not handle all edge cases correctly, and performance could be improved. For example:
     * <ul>
     *     <li>The do-while loop logic may be inefficient and could risk an infinite loop.</li>
     *     <li>Substring operations assume a newline character exists; malformed events could throw exceptions.</li>
     * </ul>
     * <p>
     * The parser currently handles the following SSE fields:
     * <ul>
     *     <li><code>id:</code> ? sets {@link SSEEvent#setId(String)}</li>
     *     <li><code>event:</code> ? sets {@link SSEEvent#setEvent(String)}</li>
     *     <li><code>data:</code> ? attempts to parse JSON and sets {@link SSEEvent#setData(Object)}; falls back
     *     to raw string if parsing fails.</li>
     * </ul>
     *
     * @param eventBlock The raw SSE event block as a string.
     * @return An {@link SSEEvent} object with parsed fields. If the input is empty or malformed, some fields may be null.
     */
    private SSEEvent parseEvent(String eventBlock) {
        SSEEvent event = new SSEEvent();
        String str = eventBlock.trim();

        // Find the first newline to separate key:value
        int firstNewline = str.indexOf('\n');

        if (firstNewline > 0) {
            do {
                // Parse 'id:' field for sake of completeness
                // As stated above, the Constructor Retrieve by intent API does not send the id: line.
                if (str.startsWith("id:")) {
                    try {
                        event.setId(str.substring(3, firstNewline).trim());
                    } catch (StringIndexOutOfBoundsException e) {
                        System.err.println("Failed to parse SSE id field: " + str);
                    }
                    str = str.substring(firstNewline + 1).trim();

                    // Parse 'event:' field
                } else if (str.startsWith("event:")) {
                    try {
                        event.setEvent(str.substring(6, firstNewline).trim());
                    } catch (StringIndexOutOfBoundsException e) {
                        System.err.println("Failed to parse SSE event field: " + str);
                    }
                    str = str.substring(firstNewline + 1).trim();

                    // Parse 'data:' field
                } else if (str.startsWith("data:")) {
                    String dataStr = str.substring(5).trim();
                    try {
                        // Attempt to parse JSON data
                        Object dataObj = MAPPER.readValue(dataStr, Object.class);
                        event.setData(dataObj);
                    } catch (Exception e) {
                        // Fallback: store raw string if JSON parsing fails
                        System.err.println("Failed to parse data as JSON: " + dataStr);
                        event.setData(dataStr);
                    }
                    break;

                } else {
                    // Unknown field ? log and break to avoid infinite loop
                    System.err.println("Malformed SSE Event: " + eventBlock);
                    break;
                }

                // Recompute newline position for next iteration
                firstNewline = str.indexOf('\n');

            } while (str != null && !str.isEmpty());
        }

        return event;
    }
}
