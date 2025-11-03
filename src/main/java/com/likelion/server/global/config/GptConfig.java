package com.likelion.server.global.config;

import com.theokanning.openai.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GptConfig {

    @Value("${openai.key}")
    private String openAiKey;

    @Value("${openai.timeout}")
    private int timeoutInSeconds;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openAiKey, Duration.ofSeconds(timeoutInSeconds));
    }
}

