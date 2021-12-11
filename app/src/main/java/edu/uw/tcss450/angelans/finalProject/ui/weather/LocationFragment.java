package edu.uw.tcss450.angelans.finalProject.ui.weather;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentLocationBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private LocationViewModel mLocation;
    private WeatherViewModel mWeatherViewModel;
    private GoogleMap mMap;
    private UserInfoViewModel mUser;
    private String zipCode;
    private String city;
    private String country;
    private String latitude;
    private String longitude;


    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mWeatherViewModel = provider
                .get(WeatherViewModel.class);
        mUser = provider.get(UserInfoViewModel.class);
        mLocation = provider.get(LocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentLocationBinding binding = FragmentLocationBinding.bind(getView());
//        mModel = new ViewModelProvider(getActivity())
//                .get(LocationViewModel.class);
//        mModel.addLocationObserver(getViewLifecycleOwner(), location ->
//                binding.searchLocation.se(location.toString()));
        binding.findWeather.setOnClickListener(text ->{
            Navigation.findNavController((getView())).navigate((
                    LocationFragmentDirections.actionLocationFragmentToNavigationWeather()
            ));
        });
        binding.searchLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = binding.searchLocation.getQuery().toString();
                List<Address> addressList = new ArrayList<>();
                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.size() == 0){
                        //TODO
                    }else {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        country = address.getCountryName();
                        latitude = Double.toString(address.getLatitude());
                        longitude = Double.toString(address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        mWeatherViewModel.connectGet("current", mUser.getmJwt(), latitude, longitude);
                        mWeatherViewModel.connectGet("hourly", mUser.getmJwt(), latitude, longitude);
                        mWeatherViewModel.connectGet("daily", mUser.getmJwt(), latitude, longitude);
                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(latLng.latitude);
                        temp.setLongitude(latLng.longitude);
                        mLocation.setLocation(temp,getActivity());
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);
                final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
                //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
            }
        });
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
        mWeatherViewModel.connectGet("current", mUser.getmJwt(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        mWeatherViewModel.connectGet("hourly", mUser.getmJwt(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        mWeatherViewModel.connectGet("daily", mUser.getmJwt(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(latLng.latitude);
        temp.setLongitude(latLng.longitude);
        mLocation.setLocation(temp,getActivity());
    }
}
