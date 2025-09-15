# SSE Fundamentals

TO DO: add introduction that describes the purpose of this document.

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

Here is an example of a complete response from the Retrieve by intent API with three SSE events that returned no search results. 

```text
event: start
data: {"intent_result_id":"5b64da3b-1b54-4160-902c-828c2fd63a8b","thread_id":"d8522a55-feac-47ea-995a-5c5f3dc8cc52","request":{"intent":"query","key":"key_bvtZwrAmLzWlq0gA","thread_id":"d8522a55-feac-47ea-995a-5c5f3dc8cc52","domain":"explorer","guard":false,"num_results_per_event":10}}

event: message
data: {"intent_result_id":"5b64da3b-1b54-4160-902c-828c2fd63a8b","thread_id":"d8522a55-feac-47ea-995a-5c5f3dc8cc52","text":"Welcome! Every home has the potential to inspire. How can I help you discover the perfect piece for your space today? Let me know what you're looking for—lighting, furniture, hardware, or something else—and I'll provide tailored recommendations."}

event: end
data: {"intent_result_id":"5b64da3b-1b54-4160-902c-828c2fd63a8b","thread_id":"d8522a55-feac-47ea-995a-5c5f3dc8cc52","search_result_count":0}


```

