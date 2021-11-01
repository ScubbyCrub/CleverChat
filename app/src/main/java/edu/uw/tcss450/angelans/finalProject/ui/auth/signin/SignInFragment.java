package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

import static edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator.*;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
    }
}
