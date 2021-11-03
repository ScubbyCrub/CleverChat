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

 */
public class ResetEmailSentFragment extends Fragment {
    private FragmentResetEmailSentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetEmailSentBinding.inflate(inflater);
        return binding.getRoot();
    }
    public ResetEmailSentFragment() {

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //add listener that will send the request to the backend
        binding.buttonBackToSignIn.setOnClickListener(button -> {
            Navigation.findNavController(getView()).navigate(
                    PasswordResetDirections.actionPasswordResetToSignInFragment()
            );
        });
    }
}