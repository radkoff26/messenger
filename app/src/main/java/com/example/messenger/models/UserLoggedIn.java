package com.example.messenger.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.DeviceCrypt;

import static com.example.messenger.models.Constants.*;

public class UserLoggedIn {

    public static User getUser(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        if (!sp.contains(ID)) {
            return null;
        }
        Crypt crypt = new Crypt(DeviceCrypt.KEY);
        Integer id = sp.getInt(ID, 0);
        String login = sp.getString(LOGIN, null);
        String password = sp.getString(PASSWORD, null);
        String nickname = sp.getString(NICKNAME, null);
        String chat_container_name= sp.getString(CHAT_CONTAINER_NAME, null);
        String avatar_url = sp.getString(AVATAR_URL, null);
        return new User(
                id,
                nickname,
                login,
                crypt.decode(password),
                chat_container_name,
                null,
                avatar_url,
                true);
    }
}
