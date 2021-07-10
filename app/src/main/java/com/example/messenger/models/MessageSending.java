package com.example.messenger.models;

public class MessageSending {
    private Integer senderId;
    private Integer receiverId;
    private String text;

    public MessageSending(Integer senderId, Integer receiverId, String text) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
