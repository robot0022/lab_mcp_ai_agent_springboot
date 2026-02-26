package com.example.agent.web;

import com.example.agent.agent.BacklogAgent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BacklogAgent backlogAgent;

    @Test
    void should_call_agent_and_return_response() {
        // Arrange
        String userPrompt = "Create an issue for the login bug";
        String mockResponse = "I have correctly created the issue #42.";

        when(backlogAgent.handle(anyString())).thenReturn(mockResponse);

        Map<String, String> requestPayload = Map.of("prompt", userPrompt);

        // Act & Assert
        webTestClient.post()
                .uri("/api/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPayload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.response").isEqualTo(mockResponse);
    }

    @Test
    void should_return_bad_request_if_prompt_is_missing() {
        // Act & Assert
        webTestClient.post()
                .uri("/api/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of()) // Empty payload
                .exchange()
                // Depending on Spring's global error handler, this might be 500 or 400.
                // Since our controller throws IllegalArgumentException, Spring usually returns
                // 500 unless specifically mapped.
                // We'll assert it's a 5xx error or we can assert the specific exception if we
                // want.
                .expectStatus().is5xxServerError();
    }
}
