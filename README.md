# Mule SSE Connector

This custom Mule 4 connector was developed to support an ongoing consulting engagement involving an Agentforce agent for searching products by intent via the [Retrieve by Intent API](https://docs.constructor.com/reference/v1-asa-retrieve-intent) from [Constructor](https://constructor.com/). The API returns results in small batches through a real-time Server-Sent Events (SSE) stream. Since Agentforce does not currently support direct SSE calls, this connector functions as the SSE client, handling the real-time stream from the Constructor API.

The current revision was developed under tight deadlines to meet an urgent need. It should be considered a "quick and dirty" implementation, with planned improvements and refinements over the coming weeks.

> [!CAUTION]
>
> As examples, this custom Mule 4 SSE connector:
>
> 1. Does not include proper error handling.
> 2. Uses `System.out.println` and `System.err.println` instead of a more robust logger.
> 3. Does not properly open, maintain, and close the connection to the SSE endpoint. Also, it does not implement the `SSEConnection` and `SSEConnectionProvider` classes.
> 4. Uses a rudimentary algorithm to parse the SSE events and the lines they contain.
> 5. Was installed in a local Maven repository instead of publishing it to Anypoint Exchange.

## Getting Started

If you are not familiar with the SSE specification, the Constructor Retrieve by Intent API, or both, please review the [SSE Fundamentals document](documentation/sse-fundamentals.md) first. This document provides a brief overview of the Server-Sent Events (SSE) specification and the Constructor's own implementation. Ultimately, this short overview should help you appreciate the underlying implementation of this custom Mule 4 SSE connector. 

## Installation Instructions

1. Clone this repository.
2. Install this connector to your local Maven repository using the following command:

```sh
mvn clean install
```

3. Add the following dependency to your project `pom.xml` file:

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
