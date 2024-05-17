package com.example.chatbot;

public class ChatHistory {
    private String User;
    private String Llama;

    public ChatHistory(String user, String llama) {
        this.User = user;
        this.Llama = llama;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public String getLlama() {
        return Llama;
    }

    public void setLlama(String llama) {
        this.Llama = llama;
    }
}
