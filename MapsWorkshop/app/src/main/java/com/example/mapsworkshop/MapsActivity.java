package com.example.mapsworkshop;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapsworkshop.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RadioGroup.OnCheckedChangeListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText locationEditText;
    private Button findLocationButton;
    private ActivityMapsBinding binding;
    private RadioGroup mapTypeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapTypeRadioGroup = findViewById(R.id.mapTypeRadioGroup);
        mapTypeRadioGroup.setOnCheckedChangeListener(this);

        locationEditText = findViewById(R.id.locationEditText); // "get the references"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // call get map async - this is the code that will pass the key to get your map
        mapFragment.getMapAsync(this);

        // fuse location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // part of the main activity code from canvas -> you can copy paste it exactly pretty much,
    // any errors will be due to i differences
    public void geolocation(View v) {

        // create geocoder object
        Geocoder myGeocoder = new Geocoder(this);

        if (v.getId() == R.id.findLocationButton) {
           String locationString = locationEditText.getText().toString();
            Toast.makeText(this, "Searching for: " + locationString, Toast.LENGTH_SHORT).show();

            List<Address> list = null;
            try {
                list = myGeocoder.getFromLocationName(locationString, 1); // we just want  result, but if we change this we can get multiple "vancouver"s
            } catch(Exception e) {
                e.printStackTrace(); // will print what the error was
            }

            if(list.size()>0) { // if we have anything in our list...
                Address add = list.get(0);
                String locality = add.getLocality(); // if we get locality, it let's us get a similar option if we spelled smth kind of incorrectly
                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();

                double lat = add.getLatitude();
                double lng = add.getLongitude();

                LatLng latlng = new LatLng(lat, lng);
                //CameraUpdate update = CameraUpdateFactory.newLatLng(latlng);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
                mMap.moveCamera(update);

                // ut a pin exactly where we moved the camera to
                MarkerOptions options = new MarkerOptions().title(locality).position(latlng);
                mMap.addMarker(options);
            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    // the method that is called once you receive the map from google maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // this is the latitude and longitude for Sydney and that is why the pin goes there
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
            case R.id.defaultRadioButton:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satelliteRadioButton:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
        }
    }
}