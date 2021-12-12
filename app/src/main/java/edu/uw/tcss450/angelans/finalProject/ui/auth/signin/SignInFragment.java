package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.*;


import android.content.Context;
import android.content.SharedPreferences;
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

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentSignInBinding;
import edu.uw.tcss450.angelans.finalProject.model.PushyTokenViewModel;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

/**
 * Sign In Fragment to allow for UI elements to function when the user is prompted
 * to sign in to their existing account.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class SignInFragment extends Fragment {

    private FragmentSignInBinding mBinding;
    private SignInViewModel mSignInViewModel;

    private PushyTokenViewModel mPushyTokenViewModel;
    private UserInfoViewModel mUserViewModel;

    private PasswordValidator mCheckEmail = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"))
            .and(checkPwdOnlyHasLettersNumbersHyphensUnderscoresPlusSignPeriodsAtSign());

    /*
    PW length > 7
    PW = re-type PW
    PW include one of @#$%&*!?
    PW no white space
    PW include 1 digit
    PW include lower and upper cases
     */
    private PasswordValidator mCheckPassword =
            checkClientPredicate(pwd -> pwd.equals(mBinding.editPasswordSignin
                    .getText().toString()))
                    .and(checkPWLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /**
     * Constructor for SignInFragment.
     */
    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mSignInViewModel = new ViewModelProvider(getActivity())
                .get(SignInViewModel.class);

        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentSignInBinding.inflate(theInflater);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        //when reset button click nav to reset screen
        mBinding.textAskForgotPassword.setOnClickListener(text ->{
            Navigation.findNavController((getView())).navigate((
                    SignInFragmentDirections.actionSignInFragmentToPasswordReset()
                    ));
        });
        //When hit Sign Up button navigate to Sign Up page
        mBinding.buttonToSignUp.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                    SignInFragmentDirections.actionSignInFragmentToRegisterFragment()
                ));
        mBinding.buttonSignIn.setOnClickListener(this::attemptSignIn);
//        mBinding.buttonSignIn.setOnClickListener(button ->
//                Navigation.findNavController(getView())
//                        .navigate(SignInFragmentDirections
//                                .actionSignInFragmentToMainActivity("theEmail","theJwt")
//        ));

        mSignInViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);

        // User unable to hit sign in button until pushy token retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                mBinding.buttonSignIn.setEnabled(!token.isEmpty()));

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        mBinding.editEmailSignin.setText(args.getEmail()
                .equals("default") ? "" : args.getEmail());
        mBinding.editPasswordSignin.setText(args.getPassword()
                .equals("default") ? "" : args.getPassword());
    }

    /**
     * Starts a chain to check if the fields on the client side are formatted correctly
     * before attempting to send to the web service.
     *
     * @param theButton The button that begins the sign in process.
     */
    private void attemptSignIn(final View theButton) {
        checkEmail();
    }

    /**
     * Checks if the email is formatted properly before signing in the user.
     */
    private void checkEmail() {
        mCheckEmail.processResult(
                mCheckEmail.apply(mBinding.editEmailSignin.getText().toString().trim()),
                this::checkPassword,
                result -> mBinding.editEmailSignin.setError("Emails must be:\n" +
                        "1) 3-255 characters long\n" +
                        "2) Have an @ sign\n" +
                        "3) Only contain letters, numbers, hyphens, underscores, plus signs, or periods"));
    }

    /**
     * Checks if the password is formatted properly before signing in the user.
     */
    private void checkPassword() {
        mCheckPassword.processResult(
                mCheckPassword.apply(mBinding.editPasswordSignin.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.editPasswordSignin.setError("Passwords must be:\n" +
                        "1) 7-255 characters long\n" +
                        "2) Contain at least one capital letter, one lowercase letter, " +
                        "one number, and one of the special characters @#$%&*!?"));
    }

    /**
     * Sends the email and the password to the web service to see if the credentials
     * are valid for signing in.
     */
    private void verifyAuthWithServer() {
        mSignInViewModel.connect(
                mBinding.editEmailSignin.getText().toString(),
                mBinding.editPasswordSignin.getText().toString());
    }

    /**
     * Helper to abstract the navigation to the Activity past Authentication.
     *
     * @param theEmail user's email.
     * @param theJwt the JSON Web Token supplied by the server.
     */
    private void navigateToSuccess(final String theEmail, final String theJwt) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Store the credentials in SharedPrefs
        //always set the jwt that is used for requests

        prefs.edit().putString(getString(R.string.keys_prefs_jwt), theJwt).apply();
        prefs.edit().putString("email",theEmail).apply();
        if (mBinding.switchStaySignedIn.isChecked()) {
//            SharedPreferences prefs =
//                    getActivity().getSharedPreferences(
//                            getString(R.string.keys_shared_prefs),
//                            Context.MODE_PRIVATE);
//            // Store the credentials in SharedPrefs
//            prefs.edit().putString(getString(R.string.keys_prefs_jwt), theJwt).apply();

            //if stay signed in selected then set the sign in jwt
            prefs.edit().putString(getString(R.string.keys_prefs_signed_in), theJwt).apply();
            System.out.println(prefs.getString(getString(R.string.keys_prefs_jwt), "")
                    + " is the jwt (196)");
        }

        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                        .actionSignInFragmentToMainActivity(theEmail, theJwt));

        // Remove THIS activity from the task list. Pops off the backstack
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        if (prefs.contains(getString(R.string.keys_prefs_signed_in))) {
            String token = prefs.getString(getString(R.string.keys_prefs_signed_in), "");
            JWT jwt = new JWT(token);
            /* Check to see if the web token is still valid or not. To make a JWT expire after a
             * longer or shorter time period, change the expiration time when the JWT is
             * created on the web service.
             */
            if (!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToSuccess(email, token);
                return;
            }
        }
    }


    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param theResponse the Response from the server.
     */
    private void observeResponse(final JSONObject theResponse)  {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                Log.e("observerresponse","Response with code triggered");
                try {
                    //navigate to resend if it was an unverified user error
                    if(theResponse.getString("code").equals("400")){
                        //navigate to the verify user fragment
                        SignInFragmentDirections
                                .ActionSignInFragmentToEmailVerificationFragment dir
                                = SignInFragmentDirections
                                .actionSignInFragmentToEmailVerificationFragment();
                        dir.setEmail(String.valueOf(mBinding.editEmailSignin.getText()));
                        mSignInViewModel.resetValue();
                        Navigation.findNavController(getView()).navigate(dir);
                        //remove this from the backstack or something like that

                    }
                    mBinding.editEmailSignin.setError(
                            "Error Authenticating: " +
                                    theResponse.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    mUserViewModel = new ViewModelProvider(getActivity(),
                            new UserInfoViewModel.UserInfoViewModelFactory(
                                    mBinding.editEmailSignin.getText().toString(),
                                    theResponse.getString("token")
                            )).get(UserInfoViewModel.class);
                    sendPushyToken();
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response (SignInFragment 269)", "No Response");
        }
    }

    /**
     * Helper to abstract the request to send the pushy token to the web service
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getmJwt());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be attached to
     * PushyTokenViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observePushyPutResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // This error cannot be fixed by the user changing credentials...
                mBinding.editEmailSignin.setError("Error Authenticating on Push Token. " +
                        "Please contact support");
            } else {
                navigateToSuccess(
                        mBinding.editEmailSignin.getText().toString(),
                        mUserViewModel.getmJwt()
                );
            }
        }
    }

}
