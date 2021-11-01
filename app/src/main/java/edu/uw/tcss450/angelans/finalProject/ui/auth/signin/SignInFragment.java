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

    private void attemptSignIn(final View button) {
        checkEmail();
    }

    private void checkEmail() {
        checkEmail.processResult(
                checkEmail.apply(binding.editEmailSignin.getText().toString().trim()),
                this::checkPassword,
                result -> binding.editEmailSignin.setError("Please enter a valid Email address."));
    }

    private void checkPassword() {
        checkPassword.processResult(
                checkPassword.apply(binding.editPasswordSignin.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPasswordSignin.setError("Please enter a valid Password."));
    }

    private void verifyAuthWithServer() {
        signInViewModel.connect(
                binding.editEmailSignin.getText().toString(),
                binding.editPasswordSignin.getText().toString());
    }

    private void navigateToSuccess(final String email, final String jwt) {
        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                        .actionSignInFragmentToMainActivity(email,jwt));
    }

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
