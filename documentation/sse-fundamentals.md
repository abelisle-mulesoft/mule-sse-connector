# SSE Fundamentals

This document introduces the fundamentals of Server-Sent Events (SSE), a lightweight protocol for unidirectional, server-to-client event streaming over HTTP. It explains the SSE specification, message format, and key fields (id, event, data, retry) with illustrative examples. The document also examines Constructor’s Retrieve by intent API implementation of SSE, which adheres to the standard while streaming JSON payloads with specific event types. Ultimately, this overview is intended to help readers understand general SSE concepts and, most importantly, help you understand the underlying implementation of the custom Mule 4 SSE connector.

## SSE Overview

Server-Sent Events (SSE) is a messaging protocol that operates over HTTP. Introduced in the HTML5 specification as a W3C Working Draft in 2009, SSE provides a straightforward way for servers to push events to clients. Now maintained in the [WHATWG HTML Living Standard](https://html.spec.whatwg.org/multipage/server-sent-events.html), SSE follows the client-server paradigm with some unique characteristics.

- The client initiates the connection by sending an HTTP GET request with the header Accept: text/event-stream. Once the connection is established, SSE functions as a unidirectional communication channel, allowing the server to stream text-based events to the client over HTTP continuously.

- An SSE event consists of multiple plain text lines encoded in UTF-8. Each line ends with a single line feed (LF) character, a single carriage return (CR) character, or both (CRLF). When parsing the stream, the client must process lines in the order they are received.

  - An empty line (containing only LF, CR, or CRLF) indicates the end of an SSE event, which the client should then process.

  - Lines that start with a colon (:) are considered comments and should be ignored.

  - Lines containing colon characters (but not starting with one) are interpreted as field-value pairs: the text before the first colon becomes the field name, and the text after the colon becomes the field value (excluding any trailing line terminators).

  - Lines that are not empty and do not contain a colon are interpreted as fields with an empty string ("") as their value.

- The field names specified in the WHATWG HTML Living Standard include: `id`, `event`, `data`, and `retry`. The client should ignore any other field name.

  - The field `id` identifies the event. The client can use this value to resume the stream if the connection is lost, by sending it in the `Last-Event-ID` header in a subsequent request.

  - The field `event` specifies the type or name of the event. The client can use this to handle different event types with separate listeners.

  - The field `data` contains the actual content or payload of the event. An event can contain multiple data lines, which the client concatenates with newline characters during processing.

  - The field `retry` suggests the reconnection time in milliseconds if the connection is lost. The client may adjust its reconnection behavior based on this value.

Here is a simple example of an SSE event in the plain text format that a server might send to a client:

```text
id: 42
event: message
data: Hello, world!
data: This is a second line of data.
```

Here is a more complex SSE event example that includes all standard fields:

```text
id: 105
event: user_update
data: {
data: "username": "alice123",
data: "status": "online",
data: "lastLogin": "2025-09-09T10:00:00Z"
data: }
retry: 5000
```

Some software vendors expose a `POST` endpoint while others send chunked JSON over HTTP or newline-delimited JSON (NDJSON) instead of plain text. Their solutions are said to behave like SSE but are not spec-compliant.

## Constructor's SSE Implementation Overview

Generally speaking, the Constructor Retrieve by intent API is compliant with the SSE specification:

- It exposes a GET method and expects all information and data as URI/path and query parameters.
- As witnessed in more than 30 requests while testing the custom Mule 4 SSE connector, it does not implement any non-standard field. However, the SSE events it returns only contain lines with the `event` and `data` fields. It does not send a line with the `id` field.
- Finally, the actual content or payload sent with the data field is a JSON object, which is plain text - i.e., it is not chunked or newline-delimited.

Here is an example of the typical response from the Retrieve by intent API with five SSE events that returned two `search_result` events. 

```text
event: start
data: {"intent_result_id":"23d67f56-1a04-4dc9-b4b7-8c57b3b32c8e","request":{"intent":"I'm+looking+for+a+coffee+or+espresso+maker+under+$150","query_params":{"key":"key_9BhS51IOFNhJejk4","domain":"housewares","guard":false}}}

event: message
data: {"intent_result_id":"23d67f56-1a04-4dc9-b4b7-8c57b3b32c8e","text":"Looking for a great coffee or espresso maker under $150? Here are some excellent, lightweight options—whether you want classic drip, espresso, or a manual brew method, there’s something here for every taste and budget."}

event: search_result
data: {"intent_result_id":"23d67f56-1a04-4dc9-b4b7-8c57b3b32c8e","result_id":"9498b958-fb7b-4d43-869e-961fb25532fd","title":"Espresso & Stovetop Espresso Makers","response":{"search_request":{"display_name":"Espresso & Stovetop Espresso Makers","search_term":"Espresso & Stovetop Espresso Makers","params":{}},"alternative_search_requests":[],"results":["**values removed for readability**"]},"request":{"num_results_per_page":20,"ids":["**values removed for readability**"],"term":"","page":1,"fmt_options":{"groups_start":"current","groups_max_depth":1,"show_hidden_facets":false,"show_hidden_fields":false,"show_protected_facets":false},"sort_by":"relevance","sort_order":"descending","section":"Products","features":{"query_items":true,"a_a_test":false,"auto_generated_refined_query_rules":true,"manual_searchandizing":true,"personalization":true,"filter_items":true,"use_reranker_service_for_search":true,"use_reranker_service_for_browse":true,"use_reranker_service_for_all":false,"custom_autosuggest_ui":false,"disable_test_only_global_rules_search":false,"disable_test_only_global_rules_browse":false,"use_enriched_attributes_as_fuzzy_searchable":false},"feature_variants":{"query_items":"query_items_ctr_enriched_prefix_and_ctr_and_ctr_ss","a_a_test":null,"auto_generated_refined_query_rules":"default_rules","manual_searchandizing":null,"personalization":"default_personalization","filter_items":"filter_items_w_atcs_and_purchases","use_reranker_service_for_search":null,"use_reranker_service_for_browse":null,"use_reranker_service_for_all":null,"custom_autosuggest_ui":null,"disable_test_only_global_rules_search":null,"disable_test_only_global_rules_browse":null,"use_enriched_attributes_as_fuzzy_searchable":null}}}

event: search_result
data: {"intent_result_id":"23d67f56-1a04-4dc9-b4b7-8c57b3b32c8e","result_id":"7bb25152-9aa5-44c2-9f33-4c1c92cf97eb","title":"Compact & Programmable Drip Coffee Makers","response":{"search_request":{"display_name":"Compact & Programmable Drip Coffee Makers","search_term":"Compact & Programmable Drip Coffee Makers","params":{}},"alternative_search_requests":[],"results":["**values removed for readability**"]},"request":{"num_results_per_page":20,"ids":["**values removed for readability**"],"term":"","page":1,"fmt_options":{"groups_start":"current","groups_max_depth":1,"show_hidden_facets":false,"show_hidden_fields":false,"show_protected_facets":false},"sort_by":"relevance","sort_order":"descending","section":"Products","features":{"query_items":true,"a_a_test":false,"auto_generated_refined_query_rules":true,"manual_searchandizing":true,"personalization":true,"filter_items":true,"use_reranker_service_for_search":true,"use_reranker_service_for_browse":true,"use_reranker_service_for_all":false,"custom_autosuggest_ui":false,"disable_test_only_global_rules_search":false,"disable_test_only_global_rules_browse":false,"use_enriched_attributes_as_fuzzy_searchable":false},"feature_variants":{"query_items":"query_items_ctr_enriched_prefix_and_ctr_and_ctr_ss","a_a_test":null,"auto_generated_refined_query_rules":"default_rules","manual_searchandizing":null,"personalization":"default_personalization","filter_items":"filter_items_w_atcs_and_purchases","use_reranker_service_for_search":null,"use_reranker_service_for_browse":null,"use_reranker_service_for_all":null,"custom_autosuggest_ui":null,"disable_test_only_global_rules_search":null,"disable_test_only_global_rules_browse":null,"use_enriched_attributes_as_fuzzy_searchable":null}}}

event: end
data: {"intent_result_id":"23d67f56-1a04-4dc9-b4b7-8c57b3b32c8e","search_result_count":2}

```

Although not illustrated in the example above, each `search_result` event included several suggested products. As implied in the example, the typical response includes events of type `start`, `message`, `search_result`, and `end`. However, the Retrieve by intent API documentation also specifies the following additional event types: `article_reference`, `image_meta`, `recipe_info`, `recipe_instructions`, and `server_error`.

> [!NOTE]
>
> You can find a [complete Retrieve by intent API response example here](constructor-retrieve-by-intent-api-response-example.txt). 
