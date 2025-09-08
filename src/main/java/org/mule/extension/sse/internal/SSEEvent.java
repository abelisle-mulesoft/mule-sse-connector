package org.mule.extension.sse.internal;

/**
 * Represents a parsed Server-Sent Event (SSE).
 * <p>
 * An SSE event typically consists of:
 * <ul>
 *   <li><b>id</b>: A unique identifier for the event, if provided by the server.</li>
 *   <li><b>event</b>: The event type, if specified (defaults to "message" if not provided).</li>
 *   <li><b>data</b>: The payload of the event. Stored as an {@link Object} to remain flexible
 *       and opaque for consumers. This may be JSON, plain text, or another format depending on
 *       the SSE stream.</li>
 * </ul>
 * <p>
 * This POJO is designed for use in Mule 4 connectors where metadata introspection should not
 * attempt to expand the {@code data} field. Consumers of this class are expected to handle
 * serialization or transformation of {@code data} as needed.
 */
public class SSEEvent {

    /**
     * The unique identifier of the event.
     * <p>
     * This value corresponds to the SSE {@code id:} field. If the server does not provide
     * an event ID, this field may be {@code null}.
     */
    private String id;

    /**
     * The type of the event.
     * <p>
     * This value corresponds to the SSE {@code event:} field. If no event type is provided,
     * it often defaults to {@code "message"}.
     */
    private String event;

    /**
     * The payload of the event.
     * <p>
     * This value corresponds to the SSE {@code data:} field. It is declared as {@link Object}
     * so that Mule does not attempt to introspect or derive metadata from it. At runtime,
     * it may contain a JSON structure, plain text, or other serializable data.
     */
    private Object data;

    /**
     * Returns the unique identifier of this event.
     *
     * @return the event ID, or {@code null} if not provided by the server
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this event.
     *
     * @param id the event ID to assign
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the type of this event.
     *
     * @return the event type, or {@code null} if not provided
     */
    public String getEvent() {
        return event;
    }

    /**
     * Sets the type of this event.
     *
     * @param event the event type to assign
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Returns the payload of this event.
     *
     * @return the event payload as an {@link Object}, which may be JSON, text, or another format
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the payload of this event.
     *
     * @param data the event payload to assign
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Returns a string representation of this SSE event.
     * <p>
     * The format includes the {@code id}, {@code event}, and {@code data} values.
     *
     * @return a string representation of the SSE event
     */
    @Override
    public String toString() {
        return "SSEEvent{" +
                "id='" + id + '\'' +
                ", event='" + event + '\'' +
                ", data=" + data +
                '}';
    }
}
