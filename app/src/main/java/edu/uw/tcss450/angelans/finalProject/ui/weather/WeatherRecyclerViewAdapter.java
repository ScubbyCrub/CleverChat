package edu.uw.tcss450.angelans.finalProject.ui.weather;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentWeatherBinding;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder> {
    private final List<Weather> mWeather;
    public WeatherRecyclerViewAdapter(List<Weather> weather) {
        this.mWeather = weather;
    }

    @NonNull
    @Override
    public WeatherRecyclerViewAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.setData(mWeather.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentWeatherBinding binding;

        public WeatherViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentWeatherBinding.bind(view);
        }

        void setData(final Weather weather) {
            final Resources res = mView.getContext().getResources();
            binding.temp.setText((int) weather.getCurr_temp());
        }
    }
}
