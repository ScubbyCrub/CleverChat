package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.uw.tcss450.angelans.finalProject.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherHourlyBinding;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder> {
    private final List<Weather> mWeather;
    public WeatherRecyclerViewAdapter(List<Weather> weather) {
        this.mWeather = weather;
    }

    @NonNull
    @Override
    public WeatherRecyclerViewAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_weather_hourly, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.setData_24h(mWeather.get(position));
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentWeatherHourlyBinding binding;

        public WeatherViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentWeatherHourlyBinding.bind(view);
        }

        void setData_24h(final Weather weather) {
           
            binding.hourlyTime.setText(weather.getTime());
            binding.hourlyTemp.setText(Long.toString(weather.getCurr_temp())+ "°C");
        }
    }
}
