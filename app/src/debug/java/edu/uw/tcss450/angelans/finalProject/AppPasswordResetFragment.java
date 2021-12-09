package edu.uw.tcss450.angelans.finalProject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentAppPasswordResetBinding;
import edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset.AppPasswordResetViewModel;

public class AppPasswordResetFragment extends Fragment {
    FragmentAppPasswordResetBinding mBinding;
    AppPasswordResetViewModel mViewModel;
    public AppPasswordResetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity())
                .get(AppPasswordResetViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAppPasswordResetBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        //reset password
        mBinding.buttonReset.setOnClickListener( click -> {
                   if(mBinding.textfieldNewPassword.getText().toString().equals(
                           mBinding.textfieldRetypePassword.getText().toString())){
                       mViewModel.resetPassword(
                               prefs.getString("email",""),
                               mBinding.textfieldCurrentPassword.getText().toString().trim(),
                               mBinding.textfieldNewPassword.getText().toString().trim()
                       );
                       //reset the fields to empty
//                       mBinding.textfieldCurrentPassword.setText("");
//                       mBinding.textfieldNewPassword.setText("");
//                       mBinding.textfieldRetypePassword.setText("");
                   }
        });
        mViewModel.addObserver(getViewLifecycleOwner(), data -> {
            Navigation.findNavController(getView()).navigate(
                    AppPasswordResetFragmentDirections.actionAppPasswordResetFragmentToNavigationHome()
            );
        });

    }
}