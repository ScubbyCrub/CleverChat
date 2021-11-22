package edu.uw.tcss450.angelans.finalProject.ui.contact;

import androidx.annotation.Nullable;

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


    //Avoid duplicate
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if(other instanceof ContactInfo) {
            result = mUsername.equals(((ContactInfo) other).mUsername);
        }
        return result;
    }
}
