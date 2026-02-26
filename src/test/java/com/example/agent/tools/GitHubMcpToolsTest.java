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
        McpHttpClient mcpClientMock = mock(McpHttpClient.class);
        GitHubMcpTools tools = new GitHubMcpTools(mcpClientMock, "test-owner", "test-repo");

        Map<String, Object> mockResponse = Map.of(
                "content", "Issue #42 created");
        when(mcpClientMock.callTool(eq("create_issue"), any(Map.class)))
                .thenReturn(Mono.just(mockResponse));

        // Act
        String result = tools.createIssue("Fix Login", "Login is broken on Chrome");

        // Assert
        assertThat(result).contains("Issue #42 created");

        ArgumentCaptor<Map<String, Object>> argsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mcpClientMock, times(1)).callTool(eq("create_issue"), argsCaptor.capture());

        Map<String, Object> capturedArgs = argsCaptor.getValue();
        assertThat(capturedArgs).containsEntry("owner", "test-owner");
        assertThat(capturedArgs).containsEntry("repo", "test-repo");
        assertThat(capturedArgs).containsEntry("title", "Fix Login");
        assertThat(capturedArgs).containsEntry("body", "Login is broken on Chrome");
    }
}
