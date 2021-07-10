package com.example.messenger.models;

public class User {
    private Integer id;
    private String nickname;
    private String login;
    private String password;
    private String chat_container_name;
    private String last_online;
    private String avatar_url;
    private Boolean is_online;

    public User(Integer id, String nickname, String login, String password, String chat_container_name, String last_online, String avatar_url, Boolean is_online) {
        this.id = id;
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.chat_container_name = chat_container_name;
        this.last_online = last_online;
        this.avatar_url = avatar_url;
        this.is_online = is_online;
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
        return chat_container_name;
    }

    public void setChatContainerName(String chat_container_name) {
        this.chat_container_name = chat_container_name;
    }

    public String getLastOnline() {
        return last_online;
    }

    public void setLastOnline(String last_online) {
        this.last_online = last_online;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Boolean getIsOnline() {
        return is_online;
    }

    public void setIsOnline(Boolean is_online) {
        this.is_online = is_online;
    }
}
