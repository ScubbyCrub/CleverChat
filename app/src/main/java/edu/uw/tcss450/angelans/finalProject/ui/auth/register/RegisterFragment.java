package edu.uw.tcss450.angelans.finalProject.ui.auth.register;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    public RegisterFragment() {
        // Required empty public constructor
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
        binding.buttonSignUp.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToSignInFragment(
                                binding.editEmailSignup.getText().toString(),
                                binding.editPasswordSignup.getText().toString()
                        )
                ));
    }

}
