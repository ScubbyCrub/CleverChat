package edu.uw.tcss450.angelans.finalProject.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * A class that holds user information so that it persists even when a UI element is
 * destroyed or recreated.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class UserInfoViewModel extends ViewModel {

    private final String mEmail;
    private final String mJwt;

    /**
     * Private constructor for UserInfoViewModel.
     *
     * @param theEmail The user's email.
     * @param theJwt The JSON Web Token supplied by the server.
     */
    private UserInfoViewModel(String theEmail, String theJwt) {
        mEmail = theEmail;
        mJwt = theJwt;
    }

    /**
     * Getter for user's email.
     *
     * @return String user's email.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Getter for The JSON Web Token supplied by the server.
     *
     * @return String The JSON Web Token supplied by the server.
     */
    public String getmJwt() {
        return mJwt;
    }


    /**
     * Inner class to help instantiate UserInfoViewModel (since the constructor defined
     * above is private).
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final String mEmail;
        private final String mJwt;

        /**
         * Constructor for UserInfoViewModelFactory.
         *
         * @param theEmail User's email.
         * @param theJwt The JSON Web Token supplied by the server.
         */
        public UserInfoViewModelFactory(String theEmail, String theJwt) {
            this.mEmail = theEmail;
            this.mJwt = theJwt;
        }

        /**
         * Special UserInfoViewModel constructor.
         *
         * @param modelClass The object that will be used for class checking.
         * @param <T> Any class that extends ViewModel that can be returned as an output.
         * @return a new UserInfoViewModel made from the user's JWT and Email.
         */
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(mEmail, mJwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }


}
