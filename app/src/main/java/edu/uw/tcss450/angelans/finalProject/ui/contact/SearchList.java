package edu.uw.tcss450.angelans.finalProject.ui.contact;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class SearchList implements Serializable {
    private final String mUsername;
    private final String mName;

    public SearchList(String mUsername, String mName) {
        this.mUsername = mUsername;
        this.mName = mName;
    }

    /**
     * Getter for the name of the single search
     *
     * @return The name of the single search
     */
    public String getmName() {
        return mName;
    }

    public String getmUsername() {
        return mUsername;
    }


    /**
     * Prevents displaying duplicate entries in the search list
     *
     * @param other The search to display
     * @return True if the passed search is the same as the current search, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if(other instanceof SearchList) {
            result = mUsername.equals(((SearchList) other).mUsername);
        }
        return result;
    }
}
