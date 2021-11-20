package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentEmailVerificationBinding;

/**
 * This fragment is responsible for resending the user the verification emial if needed
 */
public class EmailVerificationFragment extends Fragment {
    FragmentEmailVerificationBinding mBinding;
    EmailVerificationViewModel mViewModel;

    public EmailVerificationFragment() {
        // Required empty public constructor
    }

    /**
     *
     * Overrides the on create view method
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentEmailVerificationBinding.inflate(inflater);
//        return inflater.inflate(R.layout.fragment_email_verification, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState){
        super.onCreate(theSavedInstanceState);
        mViewModel = new ViewModelProvider(getActivity())
                .get(EmailVerificationViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mBinding.buttonResendEmail.setOnClickListener(button -> {
            Log.e("Button", "Clicked!");
            //resend email
            EmailVerificationFragmentArgs args =  EmailVerificationFragmentArgs.fromBundle(getArguments());
            mViewModel.connect(args.getEmail());
            //navigate back to the main page
            Log.e("WEEWEE","WOOWOO");

            EmailVerificationFragmentDirections.ActionEmailVerificationFragmentToSignInFragment dir =
                    EmailVerificationFragmentDirections.actionEmailVerificationFragmentToSignInFragment();

            dir.setEmail(args.getEmail());
            dir.setPassword("");
//            Navigation.findNavController(getView()).navigate(dir);
            Navigation.findNavController(getView()).popBackStack();
        });

    }



}