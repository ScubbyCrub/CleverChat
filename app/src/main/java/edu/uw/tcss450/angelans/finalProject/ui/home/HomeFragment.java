package edu.uw.tcss450.angelans.finalProject.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentHomeBinding;
import edu.uw.tcss450.angelans.finalProject.model.PushyTokenViewModel;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import me.pushy.sdk.Pushy;

/**
 * Home Fragment to allow for UI elements to function when the user is interacting with
 * the home screen.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;

    UserInfoViewModel mModel;

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
        mModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mBinding = FragmentHomeBinding.bind(getView());

        mBinding.textHomeUsername.setText(mModel.getEmail());

        mBinding.tempChatButton.setOnClickListener(button ->
                Navigation.findNavController(getView())
                        .navigate(HomeFragmentDirections
                                .actionNavigationHomeToSingleChatFragment()
                ));

        mBinding.buttonSignOut.setOnClickListener(button -> {
            signOut();
        });

        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        prefs.edit().putString("email", mModel.getEmail()).apply();
        System.out.println("email: " + mModel.getEmail());
//        FragmentHomeBinding.bind(getView()).textHello.setText("Hello " + model.getEmail());

        // Token Tester
        PushyTokenViewModel pushyTokenViewModel = new ViewModelProvider(
                getActivity()).get(PushyTokenViewModel.class);
//        Log.d("HomeFragment Tokens",  "Email: " + mModel.getEmail()
//                + "\n JWT Token: " + mModel.getmJwt());
    }

    /**
     * Removes the users JWT token from the saved preferences on sign out.
     */
    private void signOut() {

        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_signed_in)).apply();

        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);

        // When we heard back from the web service, quit
        model.addResponseObserver(this, result -> this.getActivity().finishAndRemoveTask());

        model.deleteTokenFromWebservice(
                new ViewModelProvider(getActivity())
                        .get(UserInfoViewModel.class).getmJwt()
        );
    }
}