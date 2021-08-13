package com.example.messenger.models;

public class ChatState {
    private String chatId;
    private String url;
    private Boolean isOnline;

    public ChatState(String chatId, String url, Boolean isOnline) {
        this.chatId = chatId;
        this.url = url;
        this.isOnline = isOnline;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "ChatState{" +
                "chatId='" + chatId + '\'' +
                ", url='" + url + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
