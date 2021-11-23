package edu.uw.tcss450.angelans.finalProject.model;

/**
 * This class is a model of a chat
 * @Author Vlad Tregubov
 */
public class Chat {
    final String mName;
    final int mId;

    /**
     * Creates an instance of the chat
     * @param name name of the chat
     * @param chatId id of the chat
     */
    public Chat(String name, int chatId){
        this.mId = chatId;
        this.mName = name;
    }

    /**
     * returns the name of the chat
     * @return name of the chat
     */
    public String getName(){
        return mName;
    }

    /**
     * returns the id of the chat
     * @return the chatid
     */
    public int getId(){
        return this.mId;
    }
}

