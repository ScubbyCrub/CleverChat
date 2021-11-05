package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

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

    private FragmentSignInBinding binding;

    private SignInViewModel signInViewModel;

    //Email has more than 2 char, no white space and include special char "@"
    private PasswordValidator checkEmail = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    //Password has more than 1 char, no space
    private PasswordValidator checkPassword = checkPWLength(1)
            .and(checkExcludeWhiteSpace());

    /**
     * Constructor for SignInFragment.
     */
    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInViewModel = new ViewModelProvider(getActivity())
                .get(SignInViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //when reset button click nav to reset screen
        binding.textAskForgotPassword.setOnClickListener(text ->{
            Navigation.findNavController((getView())).navigate((
                    SignInFragmentDirections.actionSignInFragmentToPasswordReset()
                    ));
        });
        //When hit Sign Up button navigate to Sign Up page
        binding.buttonToSignUp.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                    SignInFragmentDirections.actionSignInFragmentToRegisterFragment()
                ));
        binding.buttonSignIn.setOnClickListener(this::attemptSignIn);

        signInViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        binding.editEmailSignin.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        binding.editPasswordSignin.setText(args.getPassword().equals("default") ? "" : args.getPassword());
    }

    /**
     * Starts a chain to check if the fields on the client side are formatted correctly
     * before attempting to send to the web service.
     *
     * @param button The button that begins the sign in process.
     */
    private void attemptSignIn(final View button) {
        checkEmail();
    }

    /**
     * Checks if the email is formatted properly before signing in the user.
     */
    private void checkEmail() {
        checkEmail.processResult(
                checkEmail.apply(binding.editEmailSignin.getText().toString().trim()),
                this::checkPassword,
                result -> binding.editEmailSignin.setError("Please enter a valid Email address."));
    }

    /**
     * Checks if the password is formatted properly before signing in the user.
     */
    private void checkPassword() {
        checkPassword.processResult(
                checkPassword.apply(binding.editPasswordSignin.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPasswordSignin.setError("Please enter a valid Password."));
    }

    /**
     * Sends the email and the password to the web service to see if the credentials
     * are valid for signing in.
     */
    private void verifyAuthWithServer() {
        signInViewModel.connect(
                binding.editEmailSignin.getText().toString(),
                binding.editPasswordSignin.getText().toString());
    }

    /**
     * Helper to abstract the navigation to the Activity past Authentication.
     *
     * @param email user's email.
     * @param jwt the JSON Web Token supplied by the server.
     */
    private void navigateToSuccess(final String email, final String jwt) {
        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                        .actionSignInFragmentToMainActivity(email,jwt));
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server.
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editEmailSignin.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    navigateToSuccess(
                            binding.editEmailSignin.getText().toString(),
                            response.getString("token")
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
