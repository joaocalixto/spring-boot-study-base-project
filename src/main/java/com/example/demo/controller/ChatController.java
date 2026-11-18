package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring AI — ChatClient unificado.
 *
 * Mesmo código funciona com OpenAI, Anthropic, Ollama, Bedrock.
 * Basta trocar o starter no pom.xml.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chat;

    public ChatController(ChatClient.Builder builder) {
        this.chat = builder
                .defaultSystem("Voce e um assistente tecnico amigavel, responde em portugues.")
                .build();
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String reply) {}

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String content = chat.prompt()
                .user(request.message())
                .call()
                .content();
        return new ChatResponse(content);
    }
}
