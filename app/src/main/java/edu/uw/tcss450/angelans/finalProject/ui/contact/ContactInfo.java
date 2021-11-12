package edu.uw.tcss450.angelans.finalProject.ui.contact;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private final String mUsername;
    private final String mName;
    private final int mImageURL;

    public static class Builder {
        private final String mUsername;
        private final String mName;
        private final int mImageURL;

        public Builder(String mUsername, String mName, int mImageURL) {
            this.mUsername = mUsername;
            this.mName = mName;
            this.mImageURL = mImageURL;
        }

        public ContactInfo info() {
            return new ContactInfo(this);
        }
    }

    private ContactInfo(final Builder info) {
        this.mImageURL = info.mImageURL;
        this.mUsername = info.mUsername;
        this.mName = info.mName;
    }

    public String getmName() {
        return mName;
    }

    public int getmImageURL() {
        return mImageURL;
    }

    public String getmUsername() {
        return mUsername;
    }
}
