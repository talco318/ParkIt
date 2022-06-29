package com.talco.brandnewapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable{


    private String Latitude;
    private String Longitude;

    public Location(String x , String y)
    {

        Latitude = x;
        Longitude = y ;

    }

    protected Location(Parcel in) {
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String get_Latitude ()
    {
        return Latitude;
    }

    public String get_Longitude ()
    {
        return Longitude;
    }
    public void set_Latitude(String x)
    {
        Latitude = x;
    }
    public void set_Longitude(String y)
    {
        Longitude = y;
    }

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
