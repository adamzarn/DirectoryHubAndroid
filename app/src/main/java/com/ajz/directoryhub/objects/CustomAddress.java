package com.ajz.directoryhub.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/22/17.
 */

public class CustomAddress implements Parcelable {

    private String street;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String zip;

    public CustomAddress(String street, String line2, String line3, String city, String state, String zip) {
        this.street = street;
        this.line2 = line2;
        this.line3 = line3;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return this.street;
    }

    public String getLine2() {
        return this.line2;
    }

    public String getLine3() {
        return this.line3;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCityStateZip() {
        if (!TextUtils.equals(this.city, "") && !TextUtils.equals(this.state, "")) {
            return this.city + ", " + this.state + " " + this.zip;
        } else if (!TextUtils.equals(this.city, "")) {
            return this.city;
        } else {
            return "";
        }
    }

    public CustomAddress(Parcel parcel) {
        this.street = parcel.readString();
        this.line2 = parcel.readString();
        this.line3 = parcel.readString();
        this.city = parcel.readString();
        this.state = parcel.readString();
        this.zip = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(street);
        parcel.writeString(line2);
        parcel.writeString(line3);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(zip);
    }

    public static final Parcelable.Creator<CustomAddress> CREATOR
            = new Parcelable.Creator<CustomAddress>() {

        public CustomAddress createFromParcel(Parcel in) {
            return new CustomAddress(in);
        }

        public CustomAddress[] newArray(int size) {
            return new CustomAddress[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> address = new HashMap<String, Object>();
        address.put("street", street);
        address.put("line2", line2);
        address.put("line3", line3);
        address.put("city", city);
        address.put("state", state);
        address.put("zip", zip);
        return address;
    }

}
