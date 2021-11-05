package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPWLength;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPwdSpecialChar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentPasswordResetBinding;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

/**
 * Password Reset Fragment to allow for UI elements to function when the user is prompted
 * to reset their password.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class PasswordReset extends Fragment {

    private PasswordValidator mEmailCheck = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));
    private FragmentPasswordResetBinding mBinding;
    private PasswordResetViewModel mPasswordResetViewModel;

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState){
        super.onCreate(theSavedInstanceState);
        mPasswordResetViewModel = new ViewModelProvider(getActivity())
                .get(PasswordResetViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState){
        super.onViewCreated(theView,theSavedInstanceState);
        //add listener that will send the request to the backend
        mBinding.buttonResetPassword.setOnClickListener(this::attemptRegister);
    }
    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        //make binding so we can access fields
        mBinding = FragmentPasswordResetBinding.inflate(theInflater);

        return mBinding.getRoot();
        //inflater.inflate(R.layout.fragment_password_reset, container, false);
    }

    /**
     * Checks if the user's email is one that is stored as a registered account before
     * sending a password reset email.
     *
     * @param theButton The button that starts the email checking process.
     */
    private void attemptRegister(final View theButton) {checkEmail();}

    /**
     * Prepares the email to be sent to the web service to see if it exists in the
     * Members database.
     */
    private void checkEmail() {
        mEmailCheck.processResult(
                mEmailCheck.apply(mBinding.editEmail.getText().toString().trim()),
                this::sendToServer,
                result -> mBinding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * Sends the email address to be checked in the database of registered users, to see
     * if changing a password would be possible.
     */
    private void sendToServer() {
        System.out.println("Sending to the server");
        mPasswordResetViewModel.connect(mBinding.editEmail.getText().toString().trim());
        //TODO: ADD navigation to fragment that confirms the email was sent
        Navigation.findNavController(getView()).navigate(
                PasswordResetDirections.actionPasswordResetToResetEmailSentOutFragment()
        );
    }
}