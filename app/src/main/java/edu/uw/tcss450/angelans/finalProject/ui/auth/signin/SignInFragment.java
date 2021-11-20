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

    //Email has more than 2 char, no white space and include special char "@"
    private PasswordValidator mCheckEmail = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    //Password has more than 1 char, no space
    private PasswordValidator mCheckPassword = checkPWLength(1)
            .and(checkExcludeWhiteSpace());

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

        mSignInViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

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
                result -> mBinding.editEmailSignin.setError("Please enter a valid Email address."));
    }

    /**
     * Checks if the password is formatted properly before signing in the user.
     */
    private void checkPassword() {
        mCheckPassword.processResult(
                mCheckPassword.apply(mBinding.editPasswordSignin.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.editPasswordSignin.setError("Please enter a valid Password."));
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
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        "shared_prefs",
                        Context.MODE_PRIVATE);
        if (prefs.contains("jwt")) {
            String token = prefs.getString("jwt", "");
            JWT jwt = new JWT(token);
            //check to see expired
            if(!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToSuccess(email, token);
                return;
            }
        }
    }
    /**
     * Helper to abstract the navigation to the Activity past Authentication.
     *
     * @param theEmail user's email.
     * @param theJwt the JSON Web Token supplied by the server.
     */
    private void navigateToSuccess(final String theEmail, final String theJwt) {
        if(mBinding.switchStaySignedIn.isChecked()){
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            "shared_prefs",
                            Context.MODE_PRIVATE
                    );
            //store the credentials in the shared preferences
            prefs.edit().putString("jwt", theJwt).apply();
        }
        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                        .actionSignInFragmentToMainActivity(theEmail,theJwt));

        //remove the activity from the stack to not let the user back to the lock screen after
        getActivity().finish();
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
                Log.e("observerresponse","Respnose with code triggered");
                try {
                    //navigate to resend if it was an unverified user error
                    if(theResponse.getString("code").equals("400")){
                        //navigate to the verify user fragment
                        SignInFragmentDirections.ActionSignInFragmentToEmailVerificationFragment dir
                                = SignInFragmentDirections.actionSignInFragmentToEmailVerificationFragment();
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
                    navigateToSuccess(
                            mBinding.editEmailSignin.getText().toString(),
                            theResponse.getString("token")
                    );
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
