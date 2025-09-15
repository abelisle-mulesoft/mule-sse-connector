# Folder `mule-api-impl`

This folder, as the name implies, contains the implementation of the `Product Information System API`, which encapsulates and abstracts calling the Constructor [Retrieve by Intent API](https://docs.constructor.com/reference/v1-asa-retrieve-intent) and [Retrieve by Query API](https://docs.constructor.com/reference/v1-search-get-search-results), hiding some of the complexity (e.g., SSE), for example. This system API embodies a reusable, composable asset that provides a single, agent-friendly API surface for Salesforce and Agentforce.

> [!NOTE]
>
> - The Mule project contained in this subfolder was implemented and tested using MuleSoft Anypoint Studio 7.21, Mule runtime 4.9.9, and Java 17.
> - The API specification is included in the subfolder `product-information-sapi/src/main/resources/api` to remove the dependency on the version published in Anypoint Exchange, which is not publicly available. 
