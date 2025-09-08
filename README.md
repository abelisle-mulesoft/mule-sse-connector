# Mule SSE Connector

This custom Mule 4 connector was explicitly created to act as a Server-Sent Events (SSE) client in the context of an ongoing consulting engagement. In the current revision, the implementation is limited to supporting a demo to showcase the art of the possible when powering Salesforce Agentforce with the MuleSoft Anypoint Platform to encapsulate the [Constructor Retrieve by intent API](https://docs.constructor.com/reference/v1-asa-retrieve-intent), an SSE endpoint. The current revision was created under tremendous pressure to deliver something quickly because of an urgent need. Consider this revision as a quick and dirty implementation that will be improved gradually over the next few weeks.

> [!CAUTION]
>
> The current revision represents a quick and dirty implementation. As examples:
>
> 1. It does not include proper error handling.
> 2. It uses `System.out.println` and `System.err.println` instead of a more robust logger.
> 3. It does not properly open, maintain, and close the connection to the SSE endpoint. Also, it does not implement the `SSEConnection` and `SSEConnectionProvider` classes.
> 4. It uses a rudimentary algorithm to parse the SSE events and the lines they contain.
> 5. It was installed in a local Maven repository instead of publishing it to Anypoint Exchange.

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
