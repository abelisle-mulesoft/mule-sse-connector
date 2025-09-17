package org.mule.extension.sse.internal;

import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Represents the input parameters for the {@code Get Events} operation
 * of the SSE connector. This class encapsulates configurable properties
 * such as the SSE resource path, HTTP headers, and query parameters.
 */
public class GetEventsParameters {

    /**
     * The URI path or location of the SSE resource to subscribe to.
     * <p>
     * Optional; defaults to {@code "/"} if not provided.
     */
    @Parameter
    @Optional(defaultValue = "/")
    @DisplayName("URI Path")
    private String path = "";

    /**
     * HTTP headers to include in the request to the SSE endpoint.
     * <p>
     * Optional; defaults to an empty map if not provided.
     * <p>
     * Annotated with {@link Content} to allow dynamic expression evaluation
     * in Mule flows.
     */
    @Parameter
    @Optional
    @Content
    @DisplayName("HTTP Headers")
    private Map<String, String> headers = emptyMap();

    /**
     * Query parameters to include in the request to the SSE endpoint.
     * <p>
     * Optional; defaults to an empty map if not provided.
     * <p>
     * Annotated with {@link Content} to allow dynamic expression evaluation
     * in Mule flows.
     */
    @Parameter
    @Optional
    @Content
    @DisplayName("Query Parameters")
    private Map<String, String> queryParams = emptyMap();

    /**
     * Returns the HTTP headers to be sent with the SSE request.
     *
     * @return a map of HTTP header names and values
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the HTTP headers to be sent with the SSE request.
     *
     * @param headers a map of HTTP header names and values
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Returns the URI path of the SSE resource to subscribe to.
     *
     * @return the resource path as a string
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the URI path of the SSE resource to subscribe to.
     *
     * @param path the resource path as a string
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the query parameters to be sent with the SSE request.
     *
     * @return a map of query parameter names and values
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * Sets the query parameters to be sent with the SSE request.
     *
     * @param queryParams a map of query parameter names and values
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
