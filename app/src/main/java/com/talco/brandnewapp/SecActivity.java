package com.talco.brandnewapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SecActivity extends AppCompatActivity {


    private TextView latitudeF;
    private TextView longlatitudeF;
    private TextView locationField;
    private LocationManager locationManager;
    private String provider;
    Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        locationField = (TextView) findViewById(R.id.placeName);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        if(provider!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(SecActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }else{
            Location location = locationManager.getLastKnownLocation(provider);
            if(location !=null){
                Log.d("provider " , provider  + "has been selected");

                onLocalChanged(location);

            }else{
                latitudeF.setText("location not available!");
                longlatitudeF.setText("location not available!");
            }
        }
    }

    public void onLocalChanged(Location location) {
        intent = new Intent(this, this.getClass());

        //old one is under this line:
        //intent = new Intent(this, MapsActivity.class);


        String lat = location.getLatitude()+"";
        String lng = location.getLongitude()+"";
        com.talco.brandnewapp.location location_xy = new com.talco.brandnewapp.location(lat,lng);
        latitudeF.setText(String.valueOf(lat));
        longlatitudeF.setText(String.valueOf(lng));
        try{

            get_full(location.getLatitude(),location.getLongitude());
            intent.putExtra("lat" , location.getLatitude());
            intent.putExtra("lot" , location.getLongitude());
        }catch(IOException e){
        e.printStackTrace();
        }

        //start activity intent
    }



    private void get_full(double latitude, double longlatitude) throws IOException{
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude,longlatitude,1);
        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();
        String known_name = addresses.get(0).getFeatureName();

        locationField.setText(address+ " "+ city+" "+ country+ " "+ known_name);
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SecActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(provider,200,1, (LocationListener) this);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates((LocationListener) this);
    }


    public void logoutFunc(View view) {
        Button logoutButton = findViewById(R.id.cancel_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secFragment);
                setContentView(R.layout.activity_main);

            }
        });

    }



    }