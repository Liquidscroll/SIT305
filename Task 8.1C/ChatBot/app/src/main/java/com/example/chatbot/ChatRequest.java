package com.example.chatbot;

import java.util.List;

public class ChatRequest
{
    private String userMessage;
    private List<ChatHistory> chatHistory;

    public ChatRequest(String userMessage, List<ChatHistory> chatHistory) {
        this.userMessage = userMessage;
        this.chatHistory = chatHistory;
    }
}
