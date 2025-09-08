package org.mule.extension.sse.internal;

import org.mule.runtime.api.connection.*;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * REMARK: This class was created via the Maven archetype when setting up the project. As per the README file, the
 *         current revision of the custom Mule SSE connector does not maintain a connection to the SSE endpoint, which,
 *         arguably, is against best practices. In other words, this custom Mule SSE connector does not implement this
 *         class and the SSEConnection class.
 */
public class SSEConnectionProvider implements PoolingConnectionProvider<SSEConnection> {

    private final Logger LOGGER = LoggerFactory.getLogger(SSEConnectionProvider.class);

    /**
     * A parameter that is always required to be configured.
     */
    @DisplayName("ID")
    @Parameter
    private String id;

    @Override
    public SSEConnection connect() throws ConnectionException {
        return new SSEConnection(id);
    }

    @Override
    public void disconnect(SSEConnection connection) {
        try {
            connection.invalidate();
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public ConnectionValidationResult validate(SSEConnection connection) {
        return ConnectionValidationResult.success();
    }
}
