package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.uw.tcss450.angelans.finalProject.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentDailyWeatherBinding;

/**
 * The RecyclerView that displays daily weather information
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class WeatherDailyRecyclerViewAdapter extends RecyclerView.Adapter<WeatherDailyRecyclerViewAdapter.WeatherDailyViewHolder> {
    private final List<Weather> mWeather;

    /**
     * Constructor for WeatherDailyRecyclerViewAdapter
     *
     * @param weather The Weather the list of Weather data to display
     */
    public WeatherDailyRecyclerViewAdapter(List<Weather> weather) {
        this.mWeather = weather;
    }

    @NonNull
    @Override
    public WeatherDailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherDailyViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_daily_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherDailyViewHolder holder, int position) {
        holder.setDailyData(mWeather.get(position));
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    class WeatherDailyViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentDailyWeatherBinding binding;

        public WeatherDailyViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentDailyWeatherBinding.bind(view);
        }

        void setDailyData(final Weather weather) {
            binding.weekDay.setText(weather.getTime());
            Picasso.get().load("https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png").into(binding.dailyCondition);
            binding.dailyHumidity.setText(Long.toString(weather.getHumidity())+ "%");
            binding.dailyLow.setText("L: "+Long.toString(weather.getMin_temp())+ "°F");
            binding.dailyHigh.setText("H: "+Long.toString(weather.getMax_temp())+ "°F");
        }
    }
}
