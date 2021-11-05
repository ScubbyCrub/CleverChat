package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import android.os.Bundle;
import android.os.strictmode.NetworkViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentResetEmailSentBinding;

/**
 * Reset Email Sent Fragment to allow for UI elements to function when the user wants
 * to know if an email sent to reset their password.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class ResetEmailSentFragment extends Fragment {
    private FragmentResetEmailSentBinding binding;

    /**
     * Constructor for ResetEmailSentFragment.
     */
    public ResetEmailSentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetEmailSentBinding.inflate(theInflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState){
        super.onViewCreated(theView,theSavedInstanceState);
        //add listener that will send the request to the backend
        binding.buttonBackToSignIn.setOnClickListener(button -> {
            Navigation.findNavController(getView()).navigate(
                    PasswordResetDirections.actionPasswordResetToSignInFragment()
            );
        });
    }
}