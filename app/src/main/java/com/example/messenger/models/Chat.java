package com.example.messenger.models;

public class Chat {
    private String id;
    private Integer chatter1;
    private Integer chatter2;
    private Boolean is_removed;

    public Chat(String id, Integer chatter1, Integer chatter2, Boolean is_removed) {
        this.id = id;
        this.chatter1 = chatter1;
        this.chatter2 = chatter2;
        this.is_removed = is_removed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getChatter1() {
        return chatter1;
    }

    public void setChatter1(Integer chatter1) {
        this.chatter1 = chatter1;
    }

    public Integer getChatter2() {
        return chatter2;
    }

    public void setChatter2(Integer chatter2) {
        this.chatter2 = chatter2;
    }

    public Boolean getIsRemoved() {
        return is_removed;
    }

    public void setIsRemoved(Boolean is_removed) {
        this.is_removed = is_removed;
    }
}
