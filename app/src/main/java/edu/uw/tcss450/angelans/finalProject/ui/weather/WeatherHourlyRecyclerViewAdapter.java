package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.uw.tcss450.angelans.finalProject.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentHourlyWeatherBinding;

/**
 * The RecyclerView that displays hourly weather information
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class WeatherHourlyRecyclerViewAdapter extends RecyclerView.Adapter<WeatherHourlyRecyclerViewAdapter.WeatherHourlyViewHolder> {
    private final List<Weather> mWeather;

    /**
     * Constructor for WeatherHourlyRecyclerViewAdapter
     *
     * @param weather The list of Weather data to display
     */
    public WeatherHourlyRecyclerViewAdapter(List<Weather> weather) {
        this.mWeather = weather;
    }

    @NonNull
    @Override
    public WeatherHourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherHourlyViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_hourly_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHourlyViewHolder holder, int position) {
        holder.setHourlyData(mWeather.get(position));
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    class WeatherHourlyViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentHourlyWeatherBinding binding;

        public WeatherHourlyViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentHourlyWeatherBinding.bind(view);
        }

        void setHourlyData(final Weather weather) {
            binding.hourlyTime.setText(weather.getTime());
            Picasso.get().load("https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png").into(binding.imageViewHourly);
            binding.hourlyTemp.setText(Long.toString(weather.getCurr_temp())+ "°C");
        }
    }
}
