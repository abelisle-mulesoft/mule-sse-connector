package org.mule.extension.sse.internal;

import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class SSESettings {

    /**
     * URI path or location of the resource on the SSE server.
     */
    @Parameter
    @Optional(defaultValue = "/")
    @DisplayName("URI Path")
    private String path = "";

    /**
     * Any HTTP headers required for invoking the SSE endpoint.
     */
    @Parameter
    @Optional
    @Content
    @DisplayName("HTTP Headers")
    private Map<String, String> headers = emptyMap();

    /**
     * Any query parameters required for invoking the SSE endpoint.
     */
    @Parameter
    @Optional
    @Content
    @DisplayName("Query Parameters")
    private Map<String, String> queryParams = emptyMap();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
