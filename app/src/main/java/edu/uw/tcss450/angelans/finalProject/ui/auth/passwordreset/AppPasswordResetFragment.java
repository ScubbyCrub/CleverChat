package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPWLength;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.checkPwdUpperCase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentAppPasswordResetBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

/**
 * Password Reset Fragment to allow for UI elements to function when the user is prompted
 * to reset their password in-app.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 3
 */
public class AppPasswordResetFragment extends Fragment {
    FragmentAppPasswordResetBinding mBinding;
    AppPasswordResetViewModel mAppPasswordResetViewModel;
    UserInfoViewModel mUserInfoViewModel;

    private final String PASSWORD_FORMAT_ERROR_MESSAGE = "Passwords must be:\n" +
            "1) 7-255 characters long\n" +
            "2) Contain at least one capital letter, one lowercase letter, " +
            "one number, and one of the special characters @#$%&*!?";
    /*
PW length > 7
PW = re-type PW
PW include one of @#$%&*!?
PW no white space
PW include 1 digit
PW include lower and upper cases
 */
    private PasswordValidator mCurrentPasswordCheck = checkPWLength(7)
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    private PasswordValidator mNewPasswordCheck =
            checkClientPredicate(pwd -> pwd.equals(mBinding.textfieldRetypePassword
                    .getText().toString()))
                    .and(checkPWLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /**
     * Required empty constructor for AppPasswordResetFragment
     */
    public AppPasswordResetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPasswordResetViewModel = new ViewModelProvider(getActivity())
                .get(AppPasswordResetViewModel.class);
        mUserInfoViewModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAppPasswordResetBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //reset password
        mBinding.buttonReset.setOnClickListener(this::checkCurrentPassword);
        mAppPasswordResetViewModel.addObserver(getViewLifecycleOwner(), responseBoolean -> {
            if (responseBoolean == true) {
                navigateToHome();
            } else {
                Log.d("AppPasswordResetFragment", "Password Reset Failed");
                mBinding.textfieldCurrentPassword.setError(
                        "Current password entered does not match current password in database.");
            }
        });
    }

/*    private void checkCurrentAndNewPasswordsDifferent(final View theButton) {
        PasswordValidator mismatchValidator =
                checkClientPredicate(
                        pwd -> (!pwd.equals(mBinding.textfieldRetypePassword.getText()
                                .toString().trim())));
        mCurrentPasswordCheck.processResult(
                mismatchValidator.apply(mBinding.textfieldNewPassword.getText().toString().trim()),
                this::checkCurrentPassword,
                result -> mBinding.textfieldNewPassword
                        .setError("Current password and new password cannot be the same.")
        );
    }*/

    /**
     * Checks if the claimed current password meets the base password char requirements.
     *
     * @param theButton The button that started this chain of password checks
     */
    private void checkCurrentPassword(final View theButton) {
        mCurrentPasswordCheck.processResult(
                mCurrentPasswordCheck.apply(mBinding.textfieldCurrentPassword.getText().toString().trim()),
                        this::checkPasswordsMatch,
                        result -> mBinding.textfieldCurrentPassword.setError(PASSWORD_FORMAT_ERROR_MESSAGE));
    }

    /**
     * Checks if both of the new passwords match.
     */
    private void checkPasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.textfieldRetypePassword.getText()
                                .toString().trim()));
        mCurrentPasswordCheck.processResult(
                matchValidator.apply(mBinding.textfieldNewPassword.getText().toString().trim()),
                this::checkNewPassword,
                result -> mBinding.textfieldNewPassword.setError("New passwords must match.")
        );
    }

    /**
     * Checks if the new password meets the base password char requirements.
     */
    private void checkNewPassword() {
        mNewPasswordCheck.processResult(
                mNewPasswordCheck.apply(mBinding.textfieldNewPassword.getText().toString()),
                this::verifyPasswordResetWithServer,
                result -> mBinding.textfieldNewPassword.setError(PASSWORD_FORMAT_ERROR_MESSAGE));
    }

    /**
     * Sends the password reset request to the web service.
     */
    private void verifyPasswordResetWithServer() {
        mAppPasswordResetViewModel.resetPassword(
                mUserInfoViewModel.getEmail(),
                mBinding.textfieldCurrentPassword.getText().toString().trim(),
                mBinding.textfieldNewPassword.getText().toString().trim(),
                mUserInfoViewModel.getmJwt());
    }

    /**
     * Navigate to the home fragment (on success)
     */
    private void navigateToHome() {
        mAppPasswordResetViewModel.resetViewModelState();
        Navigation.findNavController(getView()).navigate(
                AppPasswordResetFragmentDirections
                        .actionAppPasswordResetFragmentToNavigationHome());
    }
}