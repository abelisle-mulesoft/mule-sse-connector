package org.mule.extension.sse.internal;

/**
 * This class represents an extension connection.
 * <p>
 * REMARK: This class was created via the Maven archetype when setting up the project. As per the README file, the
 *         current revision of the custom Mule SSE connector does not maintain a connection to the SSE endpoint, which,
 *         arguably, is against best practices. In other words, this custom Mule SSE connector does not implement this
 *         class and the SSEConnectionProvider class.
 */
public final class SSEConnection {

    private final String id;

    public SSEConnection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void invalidate() {
        // do something to invalidate this connection!
    }
}
