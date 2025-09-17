package org.mule.extension.sse.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;

/**
 * Entry point for the Server-Sent Events (SSE) Mule 4 connector.
 * <p>
 * This class defines the extension itself, registering its configurations,
 * operations, and XML namespace. It acts as the bootstrap for the connector
 * and tells Mule how to interpret the DSL elements in a Mule application.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Registers the extension with Mule runtime using the {@link Extension} annotation.</li>
 *   <li>Associates the connector with its configuration class
 *       ({@link SSEConfiguration}) using the {@link Configurations} annotation.</li>
 *   <li>Defines the XML namespace prefix (<code>sse</code>) for usage in Mule flows.</li>
 *   <li>Declares Java 17 as the minimum supported runtime for this connector.</li>
 * </ul>
 *
 * <p>
 * Example Mule configuration:
 * </p>
 * <pre>{@code
 * <sse:config name="sseConfig" baseUrl="http://localhost:8080/events" responseTimeout="60000"/>
 * }</pre>
 *
 * @since 1.0
 */
@Xml(prefix = "sse")                        // Defines XML namespace prefix: <sse:...> in Mule DSL
@Extension(name = "SSE Connector")          // Registers the extension in Mule runtime
@JavaVersionSupport(JAVA_17)                // Declares Java 17 compatibility
@Configurations(SSEConfiguration.class)     // Registers the connector's configuration class
public class SSEExtension {
    // No implementation needed here. Mule runtime uses annotations
    // to wire configurations, operations, and sources dynamically.
}
