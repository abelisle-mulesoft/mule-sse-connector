# Folder `mule-api-spec`

This folder, as the name implies, contains the specification of the `Product Information System API`, which encapsulates and abstracts calling the Constructor [Retrieve by Intent API](https://docs.constructor.com/reference/v1-asa-retrieve-intent) and [Retrieve by Query API](https://docs.constructor.com/reference/v1-search-get-search-results), hiding some of the complexity (e.g., SSE), for example. This system API embodies a reusable, composable asset that provides a single, agent-friendly API surface for Salesforce and Agentforce.

The `api-doc` subfolder includes a copy of the API documentation from Anypoint Exchange, which was generated using Einstein Generative AI. 

## Product Information System API Specification (`openapi.yaml`)

The Product Information System API, ultimately, implements a Salesforce Agentforce topic and actions. [As required by Salesforce](https://help.salesforce.com/s/articleView?id=platform.external_services_schema_def_definition.htm&type=5), this API specification was authored using the OpenAPI Specification (OAS) version 3.0. As you will notice, the API specification includes Salesforce custom properties (e.g., `x-sfdc`) to specify Agentforce-specific settings and configurations.

Naturally, this API specification was first published on Anypoint Exchange and synchronized to the Salesforce API Catalog to make it available in Agentforce Studio as a topic and actions.

