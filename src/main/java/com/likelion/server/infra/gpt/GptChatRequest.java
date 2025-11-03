package com.likelion.server.infra.gpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * OpenAI 통신에 사용되는 Dto
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GptChatRequest {
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private List<ChatMessage> messages;

    // OpenAI 라이브러리 사용을 위해 적절한 객체로 변환
    public ChatCompletionRequest toRequest() {
        return ChatCompletionRequest.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .messages(messages)
                .build();
    }
}
