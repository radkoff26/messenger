package com.example.messenger.models;

import java.util.ArrayList;

public class TestData {

    public static final ArrayList<Chat> chats = new ArrayList<>();

    static {
        Chat chat = new Chat(
                "1_2",
                1,
                2,
                false
        );
        chat.setLastMessage(new Message(
                1,
                "Hello!",
                1,
                false,
                false,
                DateConverter.now()
        ));
        chat.setChatterLocalNickname("Slava");
        chats.add(chat);
        Chat chat1 = new Chat(
                "1_3",
                1,
                3,
                false
        );
        chat1.setLastMessage(new Message(
                1,
                "Hello!",
                1,
                true,
                false,
                DateConverter.now()
        ));
        chat1.setChatterLocalNickname("Person");
        chats.add(chat1);
    }
}
