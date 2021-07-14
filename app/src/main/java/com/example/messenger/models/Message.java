package com.example.messenger.models;

public class Message {
    private Integer id;
    private String text;
    private Integer authorId;
    private Boolean isWatched;
    private Boolean isRemoved;
    private String time;

    public Message(Integer id, String text, Integer authorId, Boolean isWatched, Boolean isRemoved, String time) {
        this.id = id;
        this.text = text;
        this.authorId = authorId;
        this.isWatched = isWatched;
        this.isRemoved = isRemoved;
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
        return authorId;
    }

    public void setAuthorId(Integer author_id) {
        this.authorId = author_id;
    }

    public Boolean getIsWatched() {
        return isWatched;
    }

    public void setIsWatched(Boolean is_watched) {
        this.isWatched = is_watched;
    }

    public Boolean getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(Boolean is_removed) {
        this.isRemoved = is_removed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", author_id=" + authorId +
                ", is_watched=" + isWatched +
                ", is_removed=" + isRemoved +
                ", time='" + time + '\'' +
                '}';
    }
}
