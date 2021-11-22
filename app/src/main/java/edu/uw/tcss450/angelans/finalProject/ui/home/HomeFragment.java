package edu.uw.tcss450.angelans.finalProject.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
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
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentHomeBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * Home Fragment to allow for UI elements to function when the user is interacting with
 * the home screen.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;
    /**
     * Constructor for HomeFragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(theInflater);
//        return theInflater.inflate(R.layout.fragment_home, theContainer, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());

        binding.textHomeUsername.setText(model.getEmail());

        binding.tempChatButton.setOnClickListener(button ->
                Navigation.findNavController(getView())
                        .navigate(HomeFragmentDirections.actionNavigationHomeToSingleChatFragment()
                ));
        mBinding.buttonSignOut.setOnClickListener(button -> {
            signOut();
        });
        SharedPreferences prefs = getActivity().getSharedPreferences(
                "shared_prefs",
                Context.MODE_PRIVATE
        );
        prefs.edit().putString("email", model.getEmail()).apply();
        System.out.println("email: " + model.getEmail());
//        FragmentHomeBinding.bind(getView()).textHello.setText("Hello " + model.getEmail());
    }

    /**
     *
     */
    private void signOut() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
         "shared_prefs",
                Context.MODE_PRIVATE
        );

        prefs.edit().remove("jwt").apply();
        //End the app completely
        getActivity().finishAndRemoveTask();
    }
}