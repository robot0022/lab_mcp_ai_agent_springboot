package com.example.agent.tools;

import com.example.agent.mcp.McpHttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GitHubMcpToolsTest {

    @Test
    @SuppressWarnings("unchecked")
    void testCreateIssueDelegatesToMcpClient() {
        // Arrange
        Map<String, Object> mockResponse = Map.of("content", "Issue #42 created");

        final java.util.List<Map<String, Object>> capturedArgs = new java.util.ArrayList<>();

        McpHttpClient fakeClient = new McpHttpClient() {
            @Override
            public Mono<Map> callTool(String toolName, Map<String, Object> arguments) {
                if ("create_issue".equals(toolName)) {
                    capturedArgs.add(arguments);
                    return Mono.just(mockResponse);
                }
                return Mono.error(new RuntimeException("Unexpected tool call"));
            }
        };

        GitHubMcpTools tools = new GitHubMcpTools(fakeClient, "test-owner", "test-repo");

        // Act
        String result = tools.createIssue("Fix Login", "Login is broken on Chrome");

        // Assert
        assertThat(result).contains("Issue #42 created");
        assertThat(capturedArgs).hasSize(1);

        Map<String, Object> args = capturedArgs.get(0);
        assertThat(args).containsEntry("owner", "test-owner");
        assertThat(args).containsEntry("repo", "test-repo");
        assertThat(args).containsEntry("title", "Fix Login");
        assertThat(args).containsEntry("body", "Login is broken on Chrome");
    }
}
