package com.example.stormhacks2024;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    GoogleMap myMap;
    private EditText locationEntry, latEntry, lngEntry;
    private String locationString, latString, lngString;

    private FusedLocationProviderClient fusedLocationClient;

    private static final double
            VANCOUVER_LAT = 49.277549,
            VANCOUVER_LNG = -123.123921,
            SEATLLE_LAT = 47.60621,
            SEATTLE_LNG = -122.33207,

    CALGARY_LAT = 51.068045,
            CALGARY_LNG = -114.074182;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationEntry = (EditText) findViewById(R.id.locationEditText);
        latEntry = (EditText) findViewById(R.id.latEditText);
        lngEntry = (EditText) findViewById(R.id.lngEditText);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        myMap = map;

        checkLocationPermission();

        gotoLocation(VANCOUVER_LAT, VANCOUVER_LNG, 15);

        myMap.setMyLocationEnabled(true);
        myMap.setOnMyLocationButtonClickListener(this);
        myMap.setOnMyLocationClickListener(this);
    }


    public void geolocate(View v) {
        Geocoder myGeocoder = new Geocoder(this);

        hideSoftKeyboard(v);

        if (v.getId() == R.id.locationButton) {
            locationString = locationEntry.getText().toString();
            Toast.makeText(this, "Searching for " + locationString, Toast.LENGTH_SHORT).show();

            List<Address> list = null;
            try {
                list = myGeocoder.getFromLocationName(locationString, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (list.size() > 0) {
                Address add = list.get(0);
                String locality = add.getLocality();
                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();

                double lat = add.getLatitude();
                double lng = add.getLongitude();
                gotoLocation(lat, lng, 15);

                MarkerOptions options = new MarkerOptions()
                        .title(locality)
                        .position(new LatLng(lat, lng));
                myMap.addMarker(options);
            }
        }

        if (v.getId() == R.id.latLngButton) {
            latString = latEntry.getText().toString();
            lngString = lngEntry.getText().toString();
            Toast.makeText(this, "Searching for " + latString + " , " + lngString, Toast.LENGTH_SHORT).show();

            List<Address> list = null;
            try {
                list = myGeocoder.getFromLocation(Double.parseDouble(latString), Double.parseDouble(lngString), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (list.size() > 0) {
                Address add = list.get(0);
                String locality = add.getLocality();
                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();

                double lat = add.getLatitude();
                double lng = add.getLongitude();
                gotoLocation(lat, lng, 15);

//                MarkerOptions options = new MarkerOptions()
//                        .title(locality)
//                        .position(new LatLng(lat, lng));
//                myMap.addMarker(options);
            }
        }
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    //just lat lng
    private void gotoLocation(double lat, double lng) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(latlng);
        myMap.moveCamera(update);
    }

    //lan lng and zoom
    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        myMap.moveCamera(update);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Add menu handling code
        switch (id) {

            case R.id.mapTypeNone:
                myMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeHybrid:
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.mapTypeTerrain:
                myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeSatellite:
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeNormal:
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mapTypeNone) {
            myMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.mapTypeHybrid) {
            myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.mapTypeTerrain) {
            myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.mapTypeSatellite) {
            myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.mapTypeNormal) {
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        return super.onOptionsItemSelected(item);
    }



    public void showCurrentLocation(MenuItem item) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
                        myMap.animateCamera(update);


                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],  int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //  locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied - Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}