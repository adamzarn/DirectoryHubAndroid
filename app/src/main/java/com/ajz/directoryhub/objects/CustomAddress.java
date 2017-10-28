package com.ajz.directoryhub.objects;

import android.text.TextUtils;

/**
 * Created by adamzarn on 10/22/17.
 */

public class CustomAddress {

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

    public String getCityStateZip() {
        if (!TextUtils.equals(this.city, "") && !TextUtils.equals(this.state, "")) {
            return this.city + ", " + this.state + " " + this.zip;
        } else if (!TextUtils.equals(this.city, "")) {
            return this.city;
        } else {
            return "";
        }
    }

}
