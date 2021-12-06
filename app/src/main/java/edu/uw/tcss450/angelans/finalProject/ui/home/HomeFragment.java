package edu.uw.tcss450.angelans.finalProject.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentHomeBinding;
import edu.uw.tcss450.angelans.finalProject.model.PushyTokenViewModel;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.ui.auth.signin.SignInFragmentDirections;

/**
 * Home Fragment to allow for UI elements to function when the user is interacting with
 * the home screen.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;

    UserInfoViewModel mUserInfoViewModel;
    HomeViewModel mHomeViewModel;
    SwitchCompat switchCompat;

    /**
     * Constructor for HomeFragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mHomeViewModel = new ViewModelProvider(getActivity())
                .get(HomeViewModel.class);
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

        // Instantiate ViewModels and Binding
        mUserInfoViewModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mBinding = FragmentHomeBinding.bind(getView());

        Log.d("HomeFragment","User's email: " + mUserInfoViewModel.getEmail());

        mHomeViewModel.connectForUsername(mUserInfoViewModel.getEmail());
        mHomeViewModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            try {
                mBinding.textHomeUsername.setText(response.getString("username"));
                Log.d("HomeFragment",
                        "Home screen displaying username, username retrieval success!");
            } catch (JSONException e) {
                mBinding.textHomeUsername.setText(mUserInfoViewModel.getEmail());
                Log.d("HomeFragment",
                        "Home screen displaying email, username retrieval failed.");
            }
        });

        mBinding.buttonSignOut.setOnClickListener(button -> {
            signOut();
        });

        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        prefs.edit().putString("email", mUserInfoViewModel.getEmail()).apply();
        System.out.println("email: " + mUserInfoViewModel.getEmail());

        // Token Tester
        PushyTokenViewModel pushyTokenViewModel = new ViewModelProvider(
                getActivity()).get(PushyTokenViewModel.class);
//        Log.d("HomeFragment Tokens",  "Email: " + mModel.getEmail()
//                + "\n JWT Token: " + mModel.getmJwt());


        //DarkMode
        mBinding.switchDarkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //Check condition
                if(mBinding.switchDarkmode.isChecked()) {
                    //Switch to dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    //Switch back to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
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