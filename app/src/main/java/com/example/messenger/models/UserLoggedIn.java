package com.example.messenger.models;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.messenger.models.Constants.*;

public class UserLoggedIn {

    private User user;

    public static User getUser(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        Integer id = sp.getInt(ID, 0);
        String login = sp.getString(LOGIN, null);
        String password = sp.getString(PASSWORD, null);
        String nickname = sp.getString(NICKNAME, null);
        String chat_container_name= sp.getString(CHAT_CONTAINER_NAME, null);
        String avatar_url = sp.getString(AVATAR_URL, null);
        return login == null ? null : new User(
                id,
                nickname,
                login,
                password,
                chat_container_name,
                null,
                avatar_url,
                true);
    }
}
