package com.talco.brandnewapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talco.brandnewapp.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView LocationField;
    private LocationManager locationManager;
    private String provider;
    private FirebaseAuth mAuth;
    public int locNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("result", "map activity");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);


        LocationField = (TextView) findViewById(R.id.placeName);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (provider != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                android.location.Location location = locationManager.getLastKnownLocation(provider);

                // Initialize the location fields
                if (location != null) {
                    Log.d("result", "Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    Log.d("result", "Location not available");
                }
            }
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locNumberRef = database.getReference("Location number");
        locNumberRef.setValue(0);


    }


    public void onClickBtn(View v) {
        Intent i = getIntent();
        Location addLoc = (Location) i.getParcelableExtra("key");
        writeLoc(addLoc.get_Latitude(), addLoc.get_Longitude());
    }


    private void get_full(double latitude, double longitude) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        LocationField.setText(address + " " + city + " ");
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Intent i = getIntent();
        Location location = (Location) i.getParcelableExtra("key");
        LatLng currentLoc = new LatLng(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
        //LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your current location"));
        //mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //LocationField.setText("on map ready");
        try {

            get_full(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
            //writeLoc(location.get_Latitude(), location.get_Longitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        addLocationsToMap(mMap);
        mMap.setMyLocationEnabled(true);

    }

    public void addLocationsToMap(GoogleMap googleMap) {
        //hard codded:
        LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        LatLng third = new LatLng(32.095280, 34.871420); // this is a test
        mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));
        mMap.addMarker(new MarkerOptions().position(third).title("Your third location"));

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference locNumberRef = database.getReference("Location number");

        DatabaseReference locationsRef = database.getReference("Locations");

        locNumberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locNumber = (int)snapshot.getValue(int.class);
                for (int i=0; i<locNumber; i++){
                    //Log.d("result" , "i is "+ i);
                    Log.d("result" , "the number of locs is "+ locNumber + " i is " + i );

                    //Location loc = (Location) snapshot.getValue(Location.class);
                    //LatLng locLat = new LatLng(Double.parseDouble(loc.get_Latitude()), Double.parseDouble(loc.get_Longitude()));
                    //Log.d("result" , " " + i + "location is "+ loc.toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        Intent intent = new Intent(this, MapsActivity.class);
        String lat = location.getLatitude() + "";
        String lng = location.getLongitude() + "";
        com.talco.brandnewapp.Location location_xy = new com.talco.brandnewapp.Location(lat, lng);

        try {

            get_full(location.getLatitude(), location.getLongitude());
            intent.putExtra("key", (Parcelable) location_xy);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Turn on location services! ",
                Toast.LENGTH_SHORT).show();
    }

    public void writeLoc(String lat, String longlat) {
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //get all data from the layout text
        Location l = new Location(lat, longlat);
        DatabaseReference myRef = database.getReference("User").child("User id: " + id).child("Location id: " + l.get_id());
        DatabaseReference publicRef = database.getReference("Locations").child("Location id: " + l.get_id());
        DatabaseReference locNumberRef = database.getReference("Location number");

        Log.d("result:", myRef.toString());
        Log.d("result:", publicRef.toString());

        publicRef.setValue(l);
        myRef.setValue(l);
        locNumberRef.setValue(l.get_id());
        //addToView();
        Toast.makeText(this, "Location added!", Toast.LENGTH_LONG).show();

    }


    public void addToView() {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

    }

}