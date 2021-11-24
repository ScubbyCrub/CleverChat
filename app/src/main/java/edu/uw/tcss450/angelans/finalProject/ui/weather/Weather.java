package edu.uw.tcss450.angelans.finalProject.ui.weather;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Serialized data of the weather information to display
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
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

    /**
     * Constructor for serializable Weather information
     *
     * @param city The city where the weather info comes from
     * @param country The country where the weather info comes from
     * @param description The description of the weather info
     * @param time The time the weather info was generated
     * @param curr_temp The current temperature of the weather
     * @param min_temp The minimum temperature the weather will be
     * @param max_temp The maximum temperature the weather will be
     * @param sunrise Time of sunrise
     * @param sunset Time of sunset
     * @param wind Wind speed
     * @param pressure Air pressure
     * @param humidity Humidity measurement
     * @param icon The icon to display for the information
     */
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

    /**
     * Constructor for serializable Weather information
     *
     * @param time The time the weather info was generated
     * @param min_temp The minimum temperature the weather will be
     * @param max_temp The maximum temperature the weather will be
     * @param humidity Humidity measurement
     * @param icon The icon to display for the information
     */
    public Weather (String time, long min_temp, long max_temp, long humidity, String icon){
        this("","","",time,-1,min_temp,max_temp,-1,-1,-1.0,-1,humidity,icon);
    }

    /**
     * Constructor for serializable Weather information
     *
     * @param hour The hour at which the weather will match the data
     * @param temp The weather's temperature
     * @param icon The icon to display for the information
     */
    public Weather (String hour, long temp, String icon){
        this("","","",hour,temp,-1,-1,-1,-1,-1.0,-1,-1,icon);
    }

    /**
     * Getter for the city
     *
     * @return the City
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter for the Country
     *
     * @return the Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter for the humidity
     *
     * @return the Humidity
     */
    public long getHumidity() {
        return humidity;
    }

    /**
     * Getter for the maximum temperature
     *
     * @return the maximum temperature
     */
    public long getMax_temp() {
        return max_temp;
    }

    /**
     * Getter for the minimum temperature
     *
     * @return the minimum temperature
     */
    public long getMin_temp() {
        return min_temp;
    }

    /**
     * Getter for the air pressure
     *
     * @return the air pressure
     */
    public long getPressure() {
        return pressure;
    }

    /**
     * Getter for the wind speed
     *
     * @return wind speed
     */
    public double getWind() {
        return wind;
    }

    /**
     * Getter for the weather's description
     *
     * @return the weather's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the time of sunrise
     *
     * @return the time of sunrise
     */
    public long getSunrise() {
        return sunrise;
    }

    /**
     * Getter for the time of sunset
     *
     * @return the time of sunset
     */
    public long getSunset() {
        return sunset;
    }

    /**
     * Getter for the current temperature
     *
     * @return the current temperature
     */
    public long getCurr_temp() {
        return curr_temp;
    }

    /**
     * Getter for the weather's time of happening
     *
     * @return the weather's time of happening
     */
    public String getTime() {
        return time;
    }

    /**
     * Getter for the icon associated with the weather data
     *
     * @return the icon associated with the weather data
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Converts meters-per-second to miles-per-hour
     *
     * @param metPerSec Meters-per-second measurement
     * @return Miles-per-hour measurement
     */
    public static double convertMsToMph(double metPerSec) {
        return (double) (metPerSec * 2.23694);
    }

    /**
     * Provides equality solely based on MessageId.
     *
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
