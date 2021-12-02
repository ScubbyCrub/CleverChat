package edu.uw.tcss450.angelans.finalProject.ui.auth.register;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.*;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

/**
 * Register Fragment to allow for UI elements to function when the user is prompted
 * to register a new account.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding mBinding;

    private RegisterViewModel mRegisterViewModel;

    // Name contains at least 2 or more char and only contains letters, hyphens, or spaces.
    private PasswordValidator mNameCheck = checkPWLength(1)
            .and(checkPwdOnlyHasLettersSpacesHyphens());

    // Username contains 2-24 chars and only contains letters, numbers, hyphens, or underscores.
    private PasswordValidator mUsernameCheck = checkPWLength(1,24)
            .and(checkPwdOnlyHasLettersNumbersHyphensUnderscores());

    // Email has more than 3 char, include special char "@", and only contains letters,
    // numbers, hyphens, underscores, or periods.
    private PasswordValidator mEmailCheck = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"))
            .and(checkPwdOnlyHasLettersNumbersHyphensUnderscoresPeriodsAtSign());

    /*
    PW length > 7
    PW = re-type PW
    PW include one of @#$%&*!?
    PW no white space
    PW include 1 digit
    PW include lower and upper cases
     */
    private PasswordValidator mPasswordCheck =
            checkClientPredicate(pwd -> pwd.equals(mBinding.editRePasswordSignup
                    .getText().toString()))
                    .and(checkPWLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /**
     * Register Fragment constructor.
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mRegisterViewModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        //When hit Sign Up button navigate to Sign In page with user's email and password
        mBinding.buttonSignUp.setOnClickListener(this::attemptRegister);
        mRegisterViewModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Starts chain that checks if registration attempt meets all field requirements.
     *
     * @param theButton The button that is used to submit the register field checks.
     */
    private void attemptRegister(final View theButton) {
        checkFirstName();
    }

    /**
     * Checks if the email meets all minimum requirements.
     */
    private void checkEmail() {
        mEmailCheck.processResult(
                mEmailCheck.apply(mBinding.editEmailSignup.getText().toString().trim()),
                this::checkPasswordsMatch,
                result -> mBinding.editEmailSignup.setError("Emails must be:\n" +
                        "1) Only associated with one account\n" +
                        "2) 3-255 characters long\n" +
                        "3) Have an @ sign\n" +
                        "4) Only contain letters, numbers, hyphens, underscores, or periods"));
    }

    /**
     * Checks if the first name meets all minimum requirements.
     */
    private void checkFirstName() {
        mNameCheck.processResult(
                mNameCheck.apply(mBinding.editFirstnameSignup.getText().toString().trim()),
                this::checkLastName,
                result -> mBinding.editFirstnameSignup.setError("First names must be:\n" +
                        "1) 2-255 characters long\n" +
                        "2) Only contain letters, spaces, or hyphens"));
    }

    /**
     * Checks if the last name meets all minimum requirements.
     */
    private void checkLastName() {
        mNameCheck.processResult(
                mNameCheck.apply(mBinding.editLastnameSignup.getText().toString().trim()),
                this::checkUsername,
                result -> mBinding.editLastnameSignup.setError("Last names must be:\n" +
                        "1) 2-255 characters long\n" +
                        "2) Only contain letters, spaces, or hyphens"));
    }

    /**
     * Checks if the username meets all minimum requirements.
     */
    private void checkUsername() {
        mUsernameCheck.processResult(
                mUsernameCheck.apply(mBinding.editUsernameSignup.getText().toString().trim()),
                this::checkEmail,
                result -> mBinding.editUsernameSignup.setError("Usernames must be:\n" +
                        "1) 2-24 characters long\n" +
                        "2) Only contain letters, numbers, hyphens, or underscores"));
    }

    /**
     * Checks if the passwords match when the user types in their password twice.
     */
    private void checkPasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.editRePasswordSignup.getText()
                                .toString().trim()));

        mEmailCheck.processResult(
                matchValidator.apply(mBinding.editPasswordSignup.getText().toString().trim()),
                this::checkPassword,
                result -> mBinding.editPasswordSignup.setError("Passwords must match."));
    }

    /**
     * Checks if the password meets all minimum requirements.
     */
    private void checkPassword() {
        mPasswordCheck.processResult(
                mPasswordCheck.apply(mBinding.editPasswordSignup.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.editPasswordSignup.setError("Passwords must be:\n" +
                        "1) 7-255 characters long\n" +
                        "2) Contain at least one capital letter, one lowercase letter, " +
                        "one number, and one of the special characters @#$%&*!?"));
    }

    /**
     * Connects to the web service to register the user through the database.
     */
    private void verifyAuthWithServer() {
        mRegisterViewModel.connect(
                mBinding.editFirstnameSignup.getText().toString(),
                mBinding.editLastnameSignup.getText().toString(),
                mBinding.editUsernameSignup.getText().toString(),
                mBinding.editEmailSignup.getText().toString(),
                mBinding.editPasswordSignup.getText().toString());
    }

    /**
     * After the user registers successfully, navigate back to the sign in screen with their
     * email and password copied to the respective sign in fields.
     */
    private void navigateToLogin() {
        RegisterFragmentDirections.ActionRegisterFragmentToSignInFragment directions =
                RegisterFragmentDirections.actionRegisterFragmentToSignInFragment();

        directions.setEmail(mBinding.editEmailSignup.getText().toString());
        directions.setPassword(mBinding.editPasswordSignup.getText().toString());

        Navigation.findNavController(getView()).navigate(directions);

    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param theResponse the Response from the server.
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                try {
                    String errorDescription = "Error Authenticating: "
                            + theResponse.getJSONObject("data").getString("message");
                    if (errorDescription.contains("Username")) {
                        mBinding.editUsernameSignup.setError(errorDescription);
                    } else {
                        mBinding.editEmailSignup.setError(errorDescription);
                    }
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

}
