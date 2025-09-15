package org.mule.extension.sse.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@DisplayName("SSE Connector Operations")
public class SSEOperations {

    private final ObjectMapper mapper = new ObjectMapper();

    @MediaType(value = MediaType.ANY, strict = false)
    @DisplayName("Get Events")
    public List<SSEEvent> getSSEEvents(
            @Config SSEConfiguration config,
            @DisplayName("Query") String query,
            @DisplayName("Path") String path,
            @DisplayName("Domain") String domain
    ) {
        List<SSEEvent> events = new ArrayList<>();

        // Translate the query (or prompt) into application/x-www-form-urlencoded format, which is safe to include in
        // the URL path.
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // Create URL as per Constructor's format - i.e., query as part of the path and adding required key and domain
        // as query parameters. For example:
        // https://agent.cnstrc.com/v1/intent/<encoded query>?key=<API key>&domain=<domain>
        String url = config.getSSEServerBaseURL() + path + encodedQuery + "?key=" + config.getAPIKey() + "&domain=" + domain;

        HttpClient client = HttpClient.newHttpClient();

        // Create a new GET request for invoking the Constructor Retrieve by intent API.
        // Remark: As implied in the code below, this request is intentionally synchronous, blocking to receive all SSE
        //         events, waiting for the Constructor Retrieve by intent API to close the connection, or the specified
        //         timeout to be exceeded.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofMillis(config.getResponseTimeout()))
                .GET()
                .build();

        try {
            // Send the request and capture the entire response - i.e., all streamed events.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Split the response into individual events intentionally using the regular expression below, as some of
            // the search results may contain "\n\n", which causes parsing issues.
            // Note: As witnessed while testing with over 30 calls to the Retrieve by intent API, Constructor does not
            //       send the id: line; it only sends the event: and data: lines. Hence, the regular expression below
            //       successfully split the response into individual events during the proof of concept and demo.
            String[] eventBlocks = response.body().split("(?=\\nevent:)");

            // Loop through all events and parse them using the convenience method below.
            for (String eventBlock : eventBlocks) {
                events.add(parseEvent(eventBlock));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events; // return List<SSEEvent> directly
    }

    // This method implements a rudimentary parsing algorithm that should be optimized and improved. It was
    // intentionally implemented as a quick and dirty solution for the sake of delivering this custom connector as
    // quickly as possible. For example, the logical expression for the do while loop is not the most efficient and
    // could cause an infinite loop.
    private SSEEvent parseEvent(String eventBlock) {
        SSEEvent event = new SSEEvent();
        String str = eventBlock.trim();

        int firstNewline = str.indexOf('\n');

        if (firstNewline > 0) {
            do {
                if (str.startsWith("id:")) {
                    event.setId(str.substring(3, firstNewline).trim());
                    str = str.substring(firstNewline + 1).trim();
                } else if (str.startsWith("event:")) {
                    event.setEvent(str.substring(6, firstNewline).trim());
                    str = str.substring(firstNewline + 1).trim();
                } else if (str.startsWith("data:")) {
                    String dataStr = str.substring(5).trim();
                    try {
                        Object dataObj = mapper.readValue(dataStr, Object.class);
                        event.setData(dataObj);
                    } catch (Exception e) {
                        System.err.println("Failed to parse data as JSON: " + dataStr);
                        event.setData(dataStr); // fallback to raw string
                    }
                    break;
                } else {
                    System.err.println("Something is wrong with the SSE Event: " + eventBlock);
                    break;
                }
            } while (str != null && !str.isEmpty());
        }

        return event;
    }
}
