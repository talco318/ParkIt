package com.talco.brandnewapp;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class location implements Parcelable {


    private String Latitude;
    private String Longitude;

    public location(String x , String y)
    {

        Latitude = x;
        Longitude = y ;

    }

    protected location(Parcel in) {
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public static final Creator<location> CREATOR = new Creator<location>() {
        @Override
        public location createFromParcel(Parcel in) {
            return new location(in);
        }

        @Override
        public location[] newArray(int size) {
            return new location[size];
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
