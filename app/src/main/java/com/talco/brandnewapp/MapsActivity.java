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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talco.brandnewapp.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public int locNumber;
    public int counterLocsFromDB ;

    ArrayList<Location> locsToMap; // Create an ArrayList object
    ArrayList<Marker> markerArrayList;
    //database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locNumberRef = database.getReference("Location number");
    DatabaseReference locationsRef = database.getReference("Locations");
    DatabaseReference publicRef = database.getReference();

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView LocationField;
    private LocationManager locationManager;
    private String provider;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locsToMap = new ArrayList<>(); // Create an ArrayList object
        markerArrayList = new ArrayList<>();

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
                    onLocationChanged(location);
                } else {
                    Log.d("result", "Location not available");
                }
            }
        }
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


    private String get_full_for_table(double latitude, double longitude) throws IOException {
        StringBuilder fullstring = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        assert fullstring != null;
        fullstring.append(address).append(" ").append(city).append(" ");
        return fullstring.toString();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Marker marker;
        mMap = googleMap;
        mMap.clear();
        Intent i = getIntent();
        readLocsFromDB();

        Location location = (Location) i.getParcelableExtra("key");
        LatLng currentLoc = new LatLng(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
        //LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        //mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your current location"));
        //mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));

        MarkerOptions userIndicator = new MarkerOptions()
                .position(new LatLng(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude())))
                .title("You are here")
                .snippet("lat:" + Double.parseDouble(location.get_Latitude()) + ", lng:" +  Double.parseDouble(location.get_Longitude()));
        marker = googleMap.addMarker(userIndicator);
        markerArrayList.add(marker);



        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            get_full(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
            //writeLoc(location.get_Latitude(), location.get_Longitude());
        } catch (IOException e) {
            e.printStackTrace();
        }


        addLocationsToMap(mMap); //read from db and marker them on map
        mMap.setMyLocationEnabled(true);
    }


    public void addLocationsToMap(GoogleMap googleMap) {
        int locNum = 0;
        //hard codded:
        //LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        //mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));

        //LatLng third = new LatLng(32.095280, 34.871420); // this is a test
        //mMap.addMarker(new MarkerOptions().position(third).title("Your third location"));


        locNumberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int locNum = (int) snapshot.getValue(int.class);
                locNumber = locNum;
                Log.d("result:", "locNum is: " + locNumber);
                readLocsFromDB(); // read from db and add them to arrayList
                //TODO: Add the locations from locsToMap to the map!
                locateLocs();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result:", "Database error!");
            }
        });

    }

    public void locateLocs() {

        //place the locations from the db on the map:
        Log.d("result:", "locateLocs func ");

        for (int i = 0; i < locNumber; i++) {
            LatLng thisLoc = new LatLng(Double.parseDouble(locsToMap.get(i).get_Latitude()), Double.parseDouble(locsToMap.get(i).get_Longitude()));
            Marker marker = mMap.addMarker(
                    new MarkerOptions()
                            .position(thisLoc)
                            .draggable(true));
            this.mMap.addMarker(new MarkerOptions().position(thisLoc).title("loc from db"));
            Log.d("result:", "i is: " + i + "- location added.");

        }
    }


    public void readLocsFromDB() {
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    counter++;
                    Log.d("result:", "readLocsFromDB func ");
                    Location loc = snap.getValue(Location.class);
                    assert loc != null;
                    Log.d("result:", "loc info is: " + loc.toString() + " lat is: " + loc.get_Latitude() + " lnglat: " + loc.get_Longitude());
                    locsToMap.add(loc);

                }
                counterLocsFromDB=counter;
                Log.d("result:", "numLocsFromDB is: " +counterLocsFromDB);
                locNumberRef.setValue(counterLocsFromDB);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result:", "Database error!");

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
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //get all data from the layout text
        Location l = new Location(lat, longlat);
        DatabaseReference myRef = database.getReference("User").child("User id: " + id).child("Location " + (locNumber+1));

        DatabaseReference publicRef = database.getReference("Locations").child("" + (locNumber+1));
        DatabaseReference locNumberRef = database.getReference("Location number");


        publicRef.setValue(l);
        myRef.setValue(l);
        Toast.makeText(this, "location " + (locNumber+1) + " added.",
                Toast.LENGTH_SHORT).show();
        locNumberRef.setValue(l.get_id());
        //addToView();
        Toast.makeText(this, "Location added!", Toast.LENGTH_LONG).show();

    }


    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    public void addToView() {
        TableLayout stk = (TableLayout) findViewById(R.id.tableLayout);
        for (int i = 0; i < locsToMap.size(); i++) {

        }

    }

}