package edu.uw.tcss450.angelans.finalProject.ui.chat;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.model.Chat;

/**
 * This class generates mock chats to display on the list for testing purposes
 * @Author Vlad Tregubov
 * @version 1
 */
public class ChatGenerator {
    private static final Chat[] CHATS;
    public static final int COUNT = 20;


    static {
        CHATS = new Chat[COUNT];
        for (int i = 0; i < CHATS.length; i++) {
            CHATS[i] = new Chat("C00lX0NT@CT", 1);
        }
    }

    /**
     * Returns a list of mock chats
     * @return list of chat objects
     */
    public static List<Chat> getChatList() {
        return Arrays.asList(CHATS);
    }

    private ChatGenerator() { }
}
