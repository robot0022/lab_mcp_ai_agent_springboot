package com.example.agent.config;

import com.example.agent.agent.BacklogAgent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {

    @Bean
    ChatLanguageModel chatLanguageModel(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model}") String modelName,
            @Value("${anthropic.timeout-seconds:60}") Integer timeoutSeconds) {

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    BacklogAgent backlogAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(BacklogAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
