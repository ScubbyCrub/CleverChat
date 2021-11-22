package edu.uw.tcss450.angelans.finalProject.ui.weather;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Weather implements Serializable {
    private final long sunrise;
    private final long sunset;
    private final double wind;
    private final long humidity;
    private final long pressure;
    private final String description;
    private final String time;
    private final String city;
    private final String country;
    private final long curr_temp;
    private final long min_temp;
    private final long max_temp;
    private final String icon;

    public Weather(String city, String country, String description, String time, long curr_temp,
                   long min_temp, long max_temp, long sunrise, long sunset, double wind,
                   long pressure, long humidity, String icon) {
        this.city = city;
        this.country = country;
        this.description = description;
        this.time = time;
        this.curr_temp = curr_temp;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.wind = convertMsToMph(wind);
        this.pressure = pressure;
        this.humidity = humidity;
        this.icon = icon;
    }

    public Weather (String time, long min_temp, long max_temp, long humidity, String icon){
        this("","","",time,-1,min_temp,max_temp,-1,-1,-1.0,-1,humidity,icon);
    }

    public Weather (String hour, long temp, String icon){
        this("","","",hour,temp,-1,-1,-1,-1,-1.0,-1,-1,icon);
    }

//    /**
//     * Static factory method to turn a properly formatted JSON String into a
//     * ChatMessage object.
//     * @param cmAsJson the String to be parsed into a ChatMessage Object.
//     * @return a ChatMessage Object with the details contained in the JSON String.
//     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
//     */
//    public static Weather createFromJsonString(final String cmAsJson) throws JSONException {
//        final JSONObject data = new JSONObject(cmAsJson);
//        return new Weather(data.getLong("temp"));
//    }


    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public long getHumidity() {
        return humidity;
    }

    public long getMax_temp() {
        return max_temp;
    }

    public long getMin_temp() {
        return min_temp;
    }

    public long getPressure() {
        return pressure;
    }

    public double getWind() {
        return wind;
    }

    public String getDescription() {
        return description;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public long getCurr_temp() {
        return curr_temp;
    }

    public String getTime() {
        return time;
    }

    public String getIcon() {
        return icon;
    }

    public static double convertMsToMph(double metPerSec) {
        return (double) (metPerSec * 2.23694);
    }

    /**
     * Provides equality solely based on MessageId.
     * @param other the other object to check for equality
     * @return true if other message ID matches this message ID, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof Weather) {
            result = time == ((Weather) other).time;
        }
        return result;
    }
}
