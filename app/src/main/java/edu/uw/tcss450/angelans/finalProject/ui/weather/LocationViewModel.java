package edu.uw.tcss450.angelans.finalProject.ui.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;

    public class LocationViewModel extends ViewModel {

        private MutableLiveData<Location> mLocation;
        private String mCity;
        private Geocoder mGeocoder;

        public LocationViewModel() {
            mLocation = new MediatorLiveData<>();
            mCity = "Unknown";
        }

        public void addLocationObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Location> observer) {
            mLocation.observe(owner, observer);
        }

        public void setLocation(final Location location, Context context) {
            mGeocoder = new Geocoder(context);
            if (!location.equals(mLocation.getValue())) {
                mLocation.setValue(location);
            }
            List<Address> addressList = new ArrayList<>();
            try {
                addressList = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(),10);
            } catch (IOException e){
                e.printStackTrace();
            }
            if (addressList.size() == 0){
                //Send an error message
                Log.e("Geocoder", "There is no location");
                this.mCity ="Unknown";
            }
            else {
                Address address = addressList.get(0);
                if (addressList.get(0).getLocality() != null){
                    this.mCity = addressList.get(0).getLocality();
                }
                else{
                    this.mCity ="Unknown";
                }
            }
        }

        public Location getCurrentLocation() {
            return new Location(mLocation.getValue());
        }


        public String getCity(){
            return this.mCity;
        }
}
