package edu.uw.tcss450.angelans.finalProject.model;

public class Chat {
    final String mName;
    final int mId;
    public Chat(String name, int chatId){
        this.mId = chatId;
        this.mName = name;
    }

    public String getName(){
        return mName;
    }
    public int getId(){
        return this.mId;
    }
}

