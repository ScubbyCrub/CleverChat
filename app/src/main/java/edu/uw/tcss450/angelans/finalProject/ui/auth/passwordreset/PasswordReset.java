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

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentPasswordResetBinding;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

/**
 */
public class PasswordReset extends Fragment {

    private PasswordValidator emailCheck = checkPWLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));
    private FragmentPasswordResetBinding binding;
    private PasswordResetViewModel passwordResetViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        passwordResetViewModel = new ViewModelProvider(getActivity()).get(PasswordResetViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //add listener that will send the request to the backend
        binding.buttonBackToSignIn.setOnClickListener(this::attemptRegister);




    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //make binding so we can access fields
        binding = FragmentPasswordResetBinding.inflate(inflater);

        return binding.getRoot();//inflater.inflate(R.layout.fragment_password_reset, container, false);
    }

    private void attemptRegister(final View button) {checkEmail();}

    private void checkEmail() {
        emailCheck.processResult(
                emailCheck.apply(binding.editEmail.getText().toString().trim()),
                this::sendToServer,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }

    private void sendToServer() {
        System.out.println("Sending to the server");
        passwordResetViewModel.connect(binding.editEmail.getText().toString().trim());
        //TODO: ADD navigation to fragment that confirms the email was sent
        Navigation.findNavController(getView()).navigate(
                PasswordResetDirections.actionPasswordResetToResetEmailSentFragment()
        );
    }
}