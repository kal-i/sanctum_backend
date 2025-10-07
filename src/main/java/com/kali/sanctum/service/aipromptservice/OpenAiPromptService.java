package com.kali.sanctum.service.aipromptservice;

import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiPromptService implements IAiPromptService {

    private final OpenAIClient openAIClient;

    @Override
    public String generatePrompt(String prompt) {
        try {
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addUserMessage(prompt)
                    .model("gpt-4o-mini")
                    .build();

            ChatCompletion response = openAIClient.chat().completions().create(params);
            return response.choices().get(0).message().content().get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
