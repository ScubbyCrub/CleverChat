package edu.uw.tcss450.angelans.finalProject.ui.contact;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private final String mUsername;
    private final String mName;

    public ContactInfo(String mUsername, String mName) {
        this.mUsername = mUsername;
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    public String getmUsername() {
        return mUsername;
    }
}
