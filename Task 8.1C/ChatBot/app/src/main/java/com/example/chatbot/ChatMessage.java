package com.example.chatbot;

public class ChatMessage
{
    private String message;
    private boolean isSentByAI;
    private String userName;

    public ChatMessage(String message, boolean isSentByAI, String userName)
    {
        this.message = message;
        this.isSentByAI = isSentByAI;
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }
    public String getUserName() {
        return userName;
    }
    public boolean isSentByAI() {
        return isSentByAI;
    }
}
