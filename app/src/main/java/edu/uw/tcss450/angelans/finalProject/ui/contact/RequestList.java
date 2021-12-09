package edu.uw.tcss450.angelans.finalProject.ui.contact;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Contains serializable data of the user's contacts list
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class RequestList implements Serializable {
    private final String mUsername;
    private final String mName;

    /**
     * Constructor for RequestList
     *
     * @param mUsername The username of the user in the contact request list
     * @param mName The name of the user in the contact request list
     */
    public RequestList(String mUsername, String mName) {
        this.mUsername = mUsername;
        this.mName = mName;
    }

    /**
     * Getter for the name of the single contact request
     *
     * @return The name of the single contact request
     */
    public String getmName() {
        return mName;
    }

    public String getmUsername() {
        return mUsername;
    }


    /**
     * Prevents displaying duplicate entries in the request list
     *
     * @param other The contact to display
     * @return True if the passed request is the same as the current request, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if(other instanceof RequestList) {
            result = mUsername.equals(((RequestList) other).mUsername);
        }
        return result;
    }
}
