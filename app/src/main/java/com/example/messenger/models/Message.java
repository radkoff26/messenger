package com.example.messenger.models;

public class Message {
    private Integer id;
    private String text;
    private Integer author_id;
    private Boolean is_watched;
    private Boolean is_removed;
    private String time;

    public Message(Integer id, String text, Integer author_id, Boolean is_watched, Boolean is_removed, String time) {
        this.id = id;
        this.text = text;
        this.author_id = author_id;
        this.is_watched = is_watched;
        this.is_removed = is_removed;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getAuthorId() {
        return author_id;
    }

    public void setAuthorId(Integer author_id) {
        this.author_id = author_id;
    }

    public Boolean getIsWatched() {
        return is_watched;
    }

    public void setIsWatched(Boolean is_watched) {
        this.is_watched = is_watched;
    }

    public Boolean getIsRemoved() {
        return is_removed;
    }

    public void setIsRemoved(Boolean is_removed) {
        this.is_removed = is_removed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
