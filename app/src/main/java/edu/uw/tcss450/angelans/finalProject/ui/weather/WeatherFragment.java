package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.MainActivity;
import edu.uw.tcss450.angelans.finalProject.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.ui.auth.signin.SignInFragmentDirections;

/**
 * Weather Fragment to allow for UI elements to function when the user is interacting
 * with a chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class WeatherFragment extends Fragment {
    private WeatherViewModel mWeatherViewModel;
    private LocationViewModel mLocation;
    private String tempSymbol = "°F";
    private Boolean flag = true;
    private UserInfoViewModel mUser;

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mWeatherViewModel = provider
                .get(WeatherViewModel.class);
        mUser = provider.get(UserInfoViewModel.class);
        mLocation = provider.get(LocationViewModel.class);
//        mWeatherViewModel.connectGet(mUser.getmJwt(),"98504");
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        if (flag) {
//            mWeatherViewModel.getDataByTime("current");
//            mWeatherViewModel.getDataByTime("hourly");
//            mWeatherViewModel.getDataByTime("daily");
//            mWeatherViewModel.connectGet("current",mUser.getmJwt(),"98101","37.41","-122.21");
//            mWeatherViewModel.connectGet("hourly",mUser.getmJwt(),"98404","37.41","-122.21");
//            mWeatherViewModel.connectGet("daily",mUser.getmJwt(),"98404","37.41","-122.21");
            flag = false;
        }
        return theInflater.inflate(R.layout.fragment_weather, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentWeatherBinding mBinding = FragmentWeatherBinding.bind(getView());
        mBinding.searchWeather.setOnClickListener(text ->{
            Navigation.findNavController((getView())).navigate((
                    WeatherFragmentDirections.actionNavigationWeatherToLocationFragment()
            ));
        });
        mWeatherViewModel.addResponseObserver("current",getViewLifecycleOwner(), weatherList ->{
            if (!weatherList.isEmpty()){
                Weather currentWeather = weatherList.get(0);
                mBinding.cityName.setText(mLocation.getCity());
                mBinding.status.setText(currentWeather.getDescription());
                mBinding.temp.setText(Long.toString(currentWeather.getCurr_temp())+ tempSymbol);
                mBinding.tempMin.setText("Min Temp: "+Long.toString(currentWeather.getMin_temp())+ tempSymbol);
                mBinding.tempMax.setText("Max Temp: "+Long.toString(currentWeather.getMax_temp())+ tempSymbol);
                mBinding.sunrise.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(currentWeather.getSunrise()*1000)));
                mBinding.sunset.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(currentWeather.getSunset()*1000)));
                mBinding.wind.setText(Long.toString(Math.round(currentWeather.getWind())) + "Mph");
                mBinding.pressure.setText(Long.toString(currentWeather.getPressure()));
                mBinding.humidity.setText(Long.toString(currentWeather.getHumidity())+"%");
            }
        });
        final RecyclerView hourly_recyclerView = mBinding.hourlyRecyclerView;
        final RecyclerView daily_recycleView = mBinding.dailyRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        hourly_recyclerView.setLayoutManager(linearLayoutManager);
        daily_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        mWeatherViewModel.addResponseObserver("hourly",getViewLifecycleOwner(),weatherList ->{
            if (!weatherList.isEmpty()){
                hourly_recyclerView.setAdapter(new WeatherHourlyRecyclerViewAdapter(
                        weatherList));
            }
        });


        mWeatherViewModel.addResponseObserver("daily",getViewLifecycleOwner(),weatherList ->{
            if (!weatherList.isEmpty()){
                daily_recycleView.setAdapter(new WeatherDailyRecyclerViewAdapter(
                        weatherList));
            }
        });
    }
}
