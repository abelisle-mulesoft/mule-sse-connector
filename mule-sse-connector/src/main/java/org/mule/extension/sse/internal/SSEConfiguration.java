package org.mule.extension.sse.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(SSEOperations.class)
public class SSEConfiguration {
    @Parameter
    @DisplayName("Base URL")
    @Summary("Base URL of the SSE server")
    private String sseServerBaseURL;

    @Parameter
    @Optional(defaultValue = "60000")     // Default is 1 minute timeout
    @DisplayName("Response timeout")
    @Summary("Maximum time in milliseconds that the request will block the execution of the flow waiting for the HTTP response (default: 60,000 milliseconds).")
    private long responseTimeout;

    public String getSSEServerBaseURL() {
        return this.sseServerBaseURL;
    }

    public long getResponseTimeout() {
        return this.responseTimeout;
    }
}
