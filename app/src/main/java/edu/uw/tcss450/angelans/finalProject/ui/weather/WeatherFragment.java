package edu.uw.tcss450.angelans.finalProject.ui.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.uw.tcss450.angelans.finalProject.R;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.angelans.finalProject.ui.auth.signin.SignInViewModel;

/**
 * Weather Fragment to allow for UI elements to function when the user is interacting
 * with a chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class WeatherFragment extends Fragment {
    private WeatherViewModel mWeatherViewModel;
    private String tempSymbol = "°C";
    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mWeatherViewModel = provider
                .get(WeatherViewModel.class);
        mWeatherViewModel.getCurrentData();
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        return theInflater.inflate(R.layout.fragment_weather, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentWeatherBinding mBinding = FragmentWeatherBinding.bind(getView());
        mWeatherViewModel.addResponseObserver("current",getViewLifecycleOwner(), weatherList ->{
            if (!weatherList.isEmpty()){
                Weather currentWeather = weatherList.get(0);
                mBinding.cityName.setText(currentWeather.getCity() + ", " + currentWeather.getCountry());
                mBinding.status.setText(currentWeather.getDescription());
                mBinding.temp.setText(Long.toString(currentWeather.getCurr_temp())+ tempSymbol);
                mBinding.tempMin.setText("Min Temp: "+Long.toString(currentWeather.getMin_temp())+ tempSymbol);
                mBinding.tempMax.setText("Max Temp: "+Long.toString(currentWeather.getMax_temp())+ tempSymbol);
                mBinding.sunrise.setText(currentWeather.getSunrise());
                mBinding.sunset.setText(currentWeather.getSunset());
                mBinding.wind.setText(Long.toString(Math.round(currentWeather.getWind())) + "Mph");
                mBinding.pressure.setText(Long.toString(currentWeather.getPressure()));
                mBinding.humidity.setText(Long.toString(currentWeather.getHumidity()));

            }
        });
    }
}
