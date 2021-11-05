package edu.uw.tcss450.angelans.finalProject.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherBinding;

/**
 * Weather Fragment to allow for UI elements to function when the user is interacting
 * with a chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class WeatherFragment extends Fragment {
    private FragmentWeatherBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentWeatherBinding.inflate(theInflater);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }
}
