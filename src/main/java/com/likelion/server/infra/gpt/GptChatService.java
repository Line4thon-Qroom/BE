package com.likelion.server.infra.gpt;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptChatService {
    private final OpenAiService openAiService;

    public String chatSinglePrompt(String fullPrompt) {
        List<ChatMessage> messages = new ArrayList<>();

        messages.add(new ChatMessage("user", fullPrompt));

        GptChatRequest request = GptChatRequest.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .maxTokens(1000)
                .messages(messages)
                .build();

        return openAiService.createChatCompletion(request.toRequest())
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }

    public String chat(String prompt, String question) {
        List<ChatMessage> messages = new ArrayList<>();

        // 시스템 프롬프트 추가
        // ex. 당신은 [과목 이름]을 가르치는 전문 교수입니다...
        messages.add(new ChatMessage("system", prompt));

        GptChatRequest request = GptChatRequest.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .maxTokens(1000)
                .messages(messages)
                .build();

        return openAiService.createChatCompletion(request.toRequest())
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
