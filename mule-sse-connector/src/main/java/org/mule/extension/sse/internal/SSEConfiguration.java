package org.mule.extension.sse.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * Configuration class for the SSE (Server-Sent Events) Mule 4 connector.
 * <p>
 * This class defines the configuration parameters that are shared across
 * multiple operations within the connector. These values represent core
 * settings such as the base URL of the SSE server and the request timeout.
 * </p>
 *
 * <p>
 * The parameters in this class are exposed to the Mule configuration DSL and
 * can be set by the user in their Mule application.
 * </p>
 *
 * @since 1.0
 */
@Operations(SSEOperations.class)
public class SSEConfiguration {

    /**
     * The base URL of the SSE server to connect to.
     * <p>
     * This value is required and should point to the root endpoint of the
     * SSE service. It will be used by all connector operations that establish
     * an SSE connection.
     * </p>
     */
    @Parameter
    @DisplayName("Base URL")
    @Summary("Base URL of the SSE server")
    private String sseServerBaseURL;

    /**
     * The maximum time, in milliseconds, that the connector will block
     * while waiting for an SSE HTTP response.
     * <p>
     * If the server does not respond within this period, the request will
     * timeout. This parameter is optional, and if not set, defaults to
     * 60,000 ms (1 minute).
     * </p>
     */
    @Parameter
    @Optional(defaultValue = "60000")  // Default is 1 minute timeout
    @DisplayName("Response timeout")
    @Summary("Maximum time in milliseconds that the request will block the execution of the flow waiting for the HTTP response (default: 60,000 milliseconds).")
    private long responseTimeout;

    /**
     * Gets the configured base URL of the SSE server.
     *
     * @return the SSE server base URL as a {@link String}
     */
    public String getSSEServerBaseURL() {
        return this.sseServerBaseURL;
    }

    /**
     * Gets the configured response timeout.
     *
     * @return the response timeout in milliseconds
     */
    public long getResponseTimeout() {
        return this.responseTimeout;
    }
}
