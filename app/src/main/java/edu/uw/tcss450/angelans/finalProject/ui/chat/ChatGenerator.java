package edu.uw.tcss450.angelans.finalProject.ui.chat;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.model.Chat;

public class ChatGenerator {
    private static final Chat[] CHATS;
    public static final int COUNT = 20;


    static {
        CHATS = new Chat[COUNT];
        for (int i = 0; i < CHATS.length; i++) {
            CHATS[i] = new Chat("C00lX0NT@CT", 1);
        }
    }

    public static List<Chat> getChatList() {
        return Arrays.asList(CHATS);
    }

    public static Chat[] getBLOGS() {
        return Arrays.copyOf(CHATS, CHATS.length);
    }

    private ChatGenerator() { }
}
