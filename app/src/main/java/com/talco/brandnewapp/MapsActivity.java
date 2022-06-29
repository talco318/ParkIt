package com.talco.brandnewapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talco.brandnewapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("result", "map activity");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent i = getIntent();
        Location location = (Location) i.getParcelableExtra("key");

        LatLng currentLoc = new LatLng(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
        LatLng sec = new LatLng(32.073698, 34.781924); // this is a test

        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your current location"));

        mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        Toast.makeText(this,location.get_Latitude()+" " +location.get_Longitude(), Toast.LENGTH_LONG).show();
    }
}