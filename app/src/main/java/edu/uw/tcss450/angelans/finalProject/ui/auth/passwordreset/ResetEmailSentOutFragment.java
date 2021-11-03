package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentResetEmailSentOutBinding;

/**

 */
public class ResetEmailSentOutFragment extends Fragment {
    private FragmentResetEmailSentOutBinding binding;
    public ResetEmailSentOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetEmailSentOutBinding.inflate(inflater);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_reset_email_sent_out, container, false);
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