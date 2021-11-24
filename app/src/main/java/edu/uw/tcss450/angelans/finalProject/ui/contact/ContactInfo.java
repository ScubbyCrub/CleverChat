package edu.uw.tcss450.angelans.finalProject.ui.contact;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Contains serializable data of the user's contacts list
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class ContactInfo implements Serializable {
    private final String mUsername;
    private final String mName;

    /**
     * Constructor for ContactInfo
     *
     * @param mUsername The username of the user in the contact list
     * @param mName The name of the user in the contact list
     */
    public ContactInfo(String mUsername, String mName) {
        this.mUsername = mUsername;
        this.mName = mName;
    }

    /**
     * Getter for the name of the single contact
     *
     * @return The name of the single contact
     */
    public String getmName() {
        return mName;
    }

    public String getmUsername() {
        return mUsername;
    }


    /**
     * Prevents displaying duplicate entries in the contacts list
     *
     * @param other The contact to display
     * @return True if the passed contact is the same as the current contact, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if(other instanceof ContactInfo) {
            result = mUsername.equals(((ContactInfo) other).mUsername);
        }
        return result;
    }
}
