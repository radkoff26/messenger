package com.example.messenger.models;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id;
    private String nickname;
    private String login;
    private String password;
    private String chatContainerName;
    private String lastOnline;
    private String avatarUrl;
    private Boolean isOnline;

    public User(Integer id, String nickname, String login, String password, String chatContainerName, String lastOnline, String avatarUrl, Boolean isOnline) {
        this.id = id;
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.chatContainerName = chatContainerName;
        this.lastOnline = lastOnline;
        this.avatarUrl = avatarUrl;
        this.isOnline = isOnline;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String nickname, String login, String password) {
        this.nickname = nickname;
        this.login = login;
        this.password = password;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChatContainerName() {
        return chatContainerName;
    }

    public void setChatContainerName(String chat_container_name) {
        this.chatContainerName = chat_container_name;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String last_online) {
        this.lastOnline = last_online;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatarUrl = avatar_url;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean is_online) {
        this.isOnline = is_online;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", chat_container_name='" + chatContainerName + '\'' +
                ", last_online='" + lastOnline + '\'' +
                ", avatar_url='" + avatarUrl + '\'' +
                ", is_online=" + isOnline +
                '}';
    }
}
