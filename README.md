# Mule SSE Connector

This repository contains a custom Mule 4 connector developed to support an ongoing consulting engagement involving a Salesforce Agentforce agent for searching products by intent via the [Retrieve by Intent API](https://docs.constructor.com/reference/v1-asa-retrieve-intent) from [Constructor](https://constructor.com/). As per Constructor's documentation, the Retrieve by Intent API returns results in small batches through a real-time Server-Sent Events (SSE) stream. Since Agentforce does not currently support direct SSE calls, this connector functions as the SSE client, handling the real-time stream from the Constructor Retrieve by Intent API.

In the repository's current revision:

- The [documentation folder](documentation/) includes a brief overview of the Server-Sent Events (SSE) specification and Constructor's own implementation. Please review the [SSE Fundamentals document](documentation/sse-fundamentals.md) first if you are not familiar with the SSE specification, the Constructor Retrieve by Intent API, or both. This short overview should help you understand the underlying implementation of the custom Mule 4 SSE connector.
- The [mule-sse-connector folder](mule-sse-connector/) contains the Java source code of this custom Mule 4 SSE Connector.

> [!CAUTION]
>
> The current revision was developed under tight deadlines to meet an urgent need. It should be considered a "quick and dirty" implementation, with planned improvements and refinements over the coming weeks. As examples, this custom Mule 4 SSE connector:
>
> 1. Does not include proper error handling.
> 2. Uses `System.out.println` and `System.err.println` instead of a more robust logger.
> 3. Does not properly open, maintain, and close the connection to the SSE endpoint. Also, it does not implement the `SSEConnection` and `SSEConnectionProvider` classes.
> 4. Uses a rudimentary algorithm to parse the SSE events and the lines they contain.
> 5. Was installed in a local Maven repository instead of publishing it to Anypoint Exchange.

## Getting Started

### Installation Instructions

1. Clone this repository.
2. Change directory to the `mule-sse-connector` folder.
3. Install this connector to your local Maven repository using the following command:

```sh
mvn clean install
```

3. Add the following dependency to the project `pom.xml` file of your Mule application:

```xml
<dependency>
  <groupId>org.mule.extension</groupId>
  <artifactId>sse-connector</artifactId>
  <version>0.1.1</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

> [!NOTE]
>
> As of this writing, the current revision of this connector is `0.1.1`. Adjust it in the dependency snippet if and as needed.
