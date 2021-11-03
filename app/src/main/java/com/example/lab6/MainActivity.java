package com.example.lab6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    private static final int PERMISSSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    // Somewhere in Australia
    private final LatLng mDestinationLatLng= new LatLng(43.0753138, -89.4036833);
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            // code for the marker
                googleMap.addMarker(new MarkerOptions()
                    .position(mDestinationLatLng).title("Start"));
                displayMyLocation();

        });

       // Obtain a Fused Location Provider Client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void displayMyLocation() {
        // Now we must check our permission
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Now we check and if it is not granted we will then request access
        if(permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }else{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, task ->{
                        Location mLastKnownLocation = task.getResult();
                            if(task.isSuccessful() && mLastKnownLocation != null){
                                mMap.addPolyline(new PolylineOptions().add(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()),
                                                mDestinationLatLng));

                                mMap.addMarker(new MarkerOptions()
                                        .position( new LatLng(mLastKnownLocation.getLatitude(),
                                                        mLastKnownLocation.getLongitude())).title("Destination"));
                                displayMyLocation();
                            }
                    });
        }
    }

    /**
     * Handle the result of our requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if(grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                displayMyLocation();
            }
        }
    }




}