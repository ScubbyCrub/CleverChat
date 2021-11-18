package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

public class WeatherViewModel extends AndroidViewModel {
    private Map<String, MutableLiveData<List<Weather>>> mWeather;

    /**
     * Constructor for WeatherViewModel.
     *
     * @param theApplication The application that WeatherViewModel should exist in.
     */
    public WeatherViewModel(@NonNull Application theApplication) {
        super(theApplication);
        mWeather = new HashMap<>();
    }

    /**
     * Add an observer to the register data to notify once data changes.
     *
     * @param owner The owner of the class that has an android life cycle.
     * @param observer The observer to respond to when data is updated.
     */
    public void addResponseObserver(String time,@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Weather>> observer) {
        getOrCreateMapEntry(time).observe(owner, observer);
    }

    public List<Weather> getWeatherListByTime(final String time) {
        return getOrCreateMapEntry(time).getValue();
    }

    private MutableLiveData<List<Weather>> getOrCreateMapEntry(final String time) {
        if(!mWeather.containsKey(time)) {
            mWeather.put(time, new MutableLiveData<>(new ArrayList<Weather>()));
        }
        return mWeather.get(time);
    }

    public void getCurrentData() {
        String url = "https://api.openweathermap.org/data/2.5/weather?units=metric&zip=98404,us&appid=d2dbfefbb461190d58e8c89d87bc0636";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelSuccess,
                this::handleError) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void getDailyData() {
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=47.258728&lon=-122.4443&units=metric&exclude=minutely,alert&appid=d2dbfefbb461190d58e8c89d87bc0636";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelSuccess,
                this::handleError) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void getHourlyData() {
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=47.258728&lon=-122.4443&units=metric&exclude=minutely,alert&appid=d2dbfefbb461190d58e8c89d87bc0636";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handelSuccess,
                this::handleError) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }


    /**
     * How to handle if the network response comes back with errors.
     *
     * @param theError The error sent back from the web service.
     */
    private void handleError(final VolleyError theError) {
        Log.e("CONNECTION ERROR", theError.getLocalizedMessage());
        throw new IllegalStateException(theError.getMessage());
    }


    private void handelSuccess(final JSONObject response) {
        List<Weather> list;
        if (!response.has("main")) {
            throw new IllegalStateException("Unexpected response in WeatherViewModel: " + response);
        }
        try {
            JSONObject current_weather_data = response.getJSONArray("weather").getJSONObject(0);
            JSONObject current_main_data = response.getJSONObject("main");
            JSONObject current_wind_data = response.getJSONObject("wind");
            JSONObject current_sys_data = response.getJSONObject("sys");
            list = getWeatherListByTime("current");
                Weather weather = new Weather(
                        response.getString("name"),
                        current_sys_data.getString("country"),
                        current_weather_data.getString("main"),
                        current_main_data.getLong("temp"),
                        current_main_data.getLong("temp_min"),
                        current_main_data.getLong("temp_max"),
                        current_sys_data.getString("sunrise"),
                        current_sys_data.getString("sunset"),
                        current_wind_data.getDouble("speed"),
                        current_main_data.getLong("pressure"),
                        current_main_data.getLong("humidity")
                );
                if (!list.contains(weather)) {
                    // don't add a duplicate
                    list.add( weather);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Weather temp already received",
                            "Or duplicate time:" + weather.getCurr_temp());
                }
            //inform observers of the change (setValue)
            getOrCreateMapEntry("current").setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success WeatherViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }
}
