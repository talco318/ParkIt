package com.talco.brandnewapp;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class location implements Parcelable {

    private String Latitude;
    private String Longitude;


    public location(String x, String y){
        Latitude = x;
        Longitude = y;

    }


    protected location(Parcel in){
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(String.valueOf(in)); // needs to be in only
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Latitude);
        parcel.writeString(Longitude);
    }
}
