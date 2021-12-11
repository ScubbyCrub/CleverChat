package edu.uw.tcss450.angelans.finalProject.ui.weather;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.angelans.finalProject.MainActivity;
import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * Weather ViewModel that protects weather information beyond the lifecycle of a fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
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
        mWeather.put("current",new MutableLiveData<>(new ArrayList<Weather>()));
        mWeather.put("hourly",new MutableLiveData<>(new ArrayList<Weather>()));
        mWeather.put("daily",new MutableLiveData<>(new ArrayList<Weather>()));
    }

    /**
     * Add an observer to the register data to notify once data changes.
     *
     * @param owner The owner of the class that has an android life cycle.
     * @param observer The observer to respond to when data is updated.
     */
    public void addResponseObserver(String time,@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Weather>> observer) {
        mWeather.get(time).observe(owner, observer);
    }

    /**
     * Returns weather information based on the time specified
     *
     * @param time The time to retrieve weather information from
     * @return A list of weather patterns that match the given time of prediction
     */
    public List<Weather> getWeatherListByTime(final String time) {
        return mWeather.get(time).getValue();
    }

    /**
     * Creates a map entry between the time of weather and the weather's data
     *
     * @param time The time to map to a weather pattern
     * @return A list of weather patterns that match the given time of prediction
     */
    private void getOrCreateMapEntry(final String time,ArrayList<Weather> list) {
        if(!mWeather.containsKey(time)) {
            mWeather.put(time, new MutableLiveData<>(list));
        }
        else {
            mWeather.get(time).setValue(list);
        }
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


    /**
     * How to handle if the network response comes back successful.
     *
     * @param response The web service's response to our web request.
     */
    private void handelSuccess(final JSONObject response) {
        ArrayList<Weather> list = new ArrayList<Weather>();
        if (!response.has("main") && !response.has("daily") && !response.has("hourly")) {
            throw new IllegalStateException("Unexpected response in WeatherViewModel: " + response);
        }
        if (response.has("current")){
            try {
                list = new ArrayList<Weather>();
                JSONObject current_data = response.getJSONObject("current");
                JSONObject current_weather_data = response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp");
                Weather weather = new Weather(
                        "",
                        "US",
                        current_data.getJSONArray("weather").getJSONObject(0).getString("main"),
                        "",
                        current_data.getLong("temp"),
                        current_weather_data.getLong("min"),
                        current_weather_data.getLong("max"),
                        current_data.getLong("sunrise"),
                        current_data.getLong("sunset"),
                        current_data.getDouble("wind_speed"),
                        current_data.getLong("pressure"),
                        current_data.getLong("humidity"),
                        ""
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
                getOrCreateMapEntry("current",list);
            }catch (JSONException e) {
                Log.e("JSON PARSE ERROR", "Found in handle Success WeatherViewModel in current");
                Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
            }
        }
        if (response.has("hourly")){
            try {
                list = new ArrayList<Weather>();
                JSONArray hourly_weather_data = response.getJSONArray("hourly");
                Calendar now = Calendar.getInstance();
                for (int i = 0; i < 24;i++) {
                    JSONObject hourly_weather = hourly_weather_data.getJSONObject(i);
                    int hour = now.get(Calendar.HOUR_OF_DAY);
                    Weather weather = new Weather(
                            Integer.toString(hour),
                            hourly_weather.getLong("temp"),
                            hourly_weather.getJSONArray("weather").getJSONObject(0).getString("icon")
                    );
                    if (!list.contains(weather)) {
                        // don't add a duplicate
                        list.add(weather);
                    } else {
                        // this shouldn't happen but could with the asynchronous
                        // nature of the application
                        Log.wtf("Weather temp already received",
                                "Or duplicate time:" + weather.getCurr_temp());
                    }
                    now.add(Calendar.HOUR,1);
                    //inform observers of the change (setValue)
                }
                getOrCreateMapEntry("hourly",list);
            }catch (JSONException e) {
                Log.e("JSON PARSE ERROR", "Found in handle Success WeatherViewModel in hourly");
                Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
            }
        }
        else if (response.has("daily")){
            try {
                list = new ArrayList<Weather>();
                JSONArray daily_weather_data = response.getJSONArray("daily");
                Calendar now = Calendar.getInstance();
                String[] dayOfWeek = {"","Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                for (int i = 1; i < daily_weather_data.length();i++) {
                    JSONObject daily_weather = daily_weather_data.getJSONObject(i);
                    String day = dayOfWeek[now.get(Calendar.DAY_OF_WEEK)];
                    Weather weather = new Weather(
                            day,
                            daily_weather.getJSONObject("temp").getLong("min"),
                            daily_weather.getJSONObject("temp").getLong("max"),
                            daily_weather.getLong("humidity"),
                            daily_weather.getJSONArray("weather").getJSONObject(0).getString("icon")
                    );
                    if (!list.contains(weather)) {
                        // don't add a duplicate
                        list.add(weather);
                    } else {
                        // this shouldn't happen but could with the asynchronous
                        // nature of the application
                        Log.wtf("Weather temp already received",
                                "Or duplicate time:" + weather.getTime());
                    }
                    now.add(Calendar.DAY_OF_WEEK,1);
                }
                //inform observers of the change (setValue)
                getOrCreateMapEntry("daily",list);
            }catch (JSONException e) {
                Log.e("JSON PARSE ERROR", "Found in handle Success WeatherViewModel in daily");
                Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves weather data from openweathermap.com based on the time requested
     *
     * @param time The time at which the weather pattern should happen
     */
    public void connectGet(final String time,final String jwt, final String lat, final String lon) {
        if (jwt == null) {
            throw new IllegalArgumentException("No UserInfoViewModel is assigned");
        }
        String url = getApplication().getResources().getString(R.string.base_url) ;
        if (time == "current"){
            url = url + "current_weather";
        }
        else if (time == "hourly"){
            url = url + "hourly_weather";
        }
        else if (time == "daily"){
            url = url + "daily_weather";
        }
        else {
            throw new IllegalStateException("Wrong time given");
        }
        Log.d("token", jwt);
        Request request = new JsonObjectRequest(Request.Method.GET, url, null,
                //no body for this get request
                this::handelSuccess, this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", "Bearer " + jwt);
                headers.put("latitude",lat);
                headers.put("longitude",lon);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10_000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }


}
