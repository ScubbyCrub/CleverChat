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

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private RegisterViewModel registerViewModel;

    //Name contains at 1 or more char
    private PasswordValidator nameCheck = checkPWLength(1);

    //Email has more than 2 char, no white space and include special char "@"
    private PasswordValidator emailCheck = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /*
    PW length > 7
    PW = re-type PW
    PW include one of @#$%&*!?
    PW no white space
    PW include 1 digit
    PW include lower and upper cases
     */
    private PasswordValidator passwordCheck =
            checkClientPredicate(pwd -> pwd.equals(binding.editRePasswordSignup.getText().toString()))
                    .and(checkPWLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerViewModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //When hit Sign In button navigate to Sign In page with user's email and password
        binding.buttonSignUp.setOnClickListener(this::attemptRegister);
        registerViewModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    private void attemptRegister(final View button) {
        checkFirstName();
    }

    private void checkFirstName() {
        nameCheck.processResult(
                nameCheck.apply(binding.editFirstnameSignup.getText().toString().trim()),
                this::checkLastName,
                result -> binding.editFirstnameSignup.setError("Please enter your first name."));
    }

    private void checkLastName() {
        nameCheck.processResult(
                nameCheck.apply(binding.editLastnameSignup.getText().toString().trim()),
                this::checkEmail,
                result -> binding.editLastnameSignup.setError("Please enter your last name."));
    }

    private void checkEmail() {
         emailCheck.processResult(
                emailCheck.apply(binding.editEmailSignup.getText().toString().trim()),
                this::checkPasswordsMatch,
                result -> binding.editEmailSignup.setError("Please enter a valid Email address."));
    }

    private void checkPasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.editRePasswordSignup.getText().toString().trim()));

        emailCheck.processResult(
                matchValidator.apply(binding.editPasswordSignup.getText().toString().trim()),
                this::checkPassword,
                result -> binding.editPasswordSignup.setError("Passwords must match."));
    }

    private void checkPassword() {
        passwordCheck.processResult(
                passwordCheck.apply(binding.editPasswordSignup.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPasswordSignup.setError("Please enter a valid Password."));
    }

    private void verifyAuthWithServer() {
        registerViewModel.connect(
                binding.editFirstnameSignup.getText().toString(),
                binding.editLastnameSignup.getText().toString(),
                binding.editEmailSignup.getText().toString(),
                binding.editPasswordSignup.getText().toString());
    }

    private void navigateToLogin() {
        binding.buttonSignUp.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToSignInFragment(
                                binding.editEmailSignup.getText().toString(),
                                binding.editPasswordSignup.getText().toString()
                        )
                ));

    }

    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editEmailSignup.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
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
