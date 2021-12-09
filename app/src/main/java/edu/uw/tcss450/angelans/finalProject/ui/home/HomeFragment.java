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

import com.google.android.material.navigation.NavigationBarItemView;

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
 * @version Sprint 3
 */
public class HomeFragment extends Fragment {
    String TAG = "PLEASE";
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
        //password reset navigation
        mBinding.buttonResetPasswordInApp.setOnClickListener(click -> {
            Navigation.findNavController(getView()).navigate(
                    HomeFragmentDirections.actionNavigationHomeToAppPasswordResetFragment()
            );
        });
        // Instantiate ViewModels and Binding
        mUserInfoViewModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mBinding = FragmentHomeBinding.bind(getView());

        Log.d("HomeFragment","User's email: " + mUserInfoViewModel.getEmail());

        mHomeViewModel.connectForUsername(mUserInfoViewModel.getEmail());
        mHomeViewModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            try {
                // Put most recently retrieved username in shared preferences (asynchronous update)
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        getString(R.string.keys_username),
                        Context.MODE_PRIVATE);

                prefs.edit().putString("currentUsername",
                        response.getString("username")).apply();

                // Display username and email
                mBinding.textHomeUsername.setText(response.getString("username"));
                mBinding.textHomeEmail.setText(mUserInfoViewModel.getEmail());
                Log.d("HomeFragment",
                        "Home screen displaying username, username retrieval success!");

            } catch (JSONException e) {
                mBinding.textHomeUsername.setText(mUserInfoViewModel.getEmail());
                mBinding.textHomeEmail.setText("");
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
        //handle dark mode
        Log.e(TAG, "onViewCreated: " + prefs.contains(getString( R.string.keys_prefs_dark_mode)) );
        if(prefs.contains(getString( R.string.keys_prefs_dark_mode))) {
            System.out.println("Found preference");
            Log.e("Found Prefs ", "OutserMethod:found pref" );
            if(prefs.getBoolean(getString(R.string.keys_prefs_dark_mode),false)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.e("prefs", "onCheckedChanged: set dark" );
                //update the switch to flip the correct direction
                mBinding.switchDarkmode.setChecked(true);
            } else {
                System.out.println();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.e("prefs", "onCheckedChanged: set true light" );
            }
        }
        prefs.edit().putString("email", mUserInfoViewModel.getEmail()).apply();

        // Token Tester
        PushyTokenViewModel pushyTokenViewModel = new ViewModelProvider(
                getActivity()).get(PushyTokenViewModel.class);
//        Log.d("HomeFragment Tokens",  "Email: " + mModel.getEmail()
//                + "\n JWT Token: " + mModel.getmJwt());


        //DarkMode
        mBinding.switchDarkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

                //Check condition
                //add it to the shared preferences
                if(mBinding.switchDarkmode.isChecked()) {
                    //Switch to dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    if(prefs.contains((getString( R.string.keys_prefs_dark_mode)))){
                        //update it
                    prefs.edit().putBoolean(getString(R.string.keys_prefs_dark_mode), true).apply();
                    Log.e("prefs", "onCheckedChanged: set true" );
//                    }
                }
                else {
                    //Switch back to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs.edit().putBoolean(getString(R.string.keys_prefs_dark_mode), false).apply();
                    Log.e("prefs", "onCheckedChanged: set false" );
                }
                Log.e(TAG, "Found Prefs Method: " + prefs.contains(getString( R.string.keys_prefs_dark_mode)) );
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