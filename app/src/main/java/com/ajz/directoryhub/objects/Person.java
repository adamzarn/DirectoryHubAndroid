package com.ajz.directoryhub.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/22/17.
 */

public class Person implements Parcelable {

    private String uid;
    private String type;
    private String name;
    private String phone;
    private String email;
    private int birthOrder;

    public Person(String uid, String type, String name, String phone, String email, int birthOrder) {
        this.uid = uid;
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthOrder = birthOrder;
    }

    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getBirthOrder() {
        return birthOrder;
    }

    public Person(Parcel parcel) {
        this.uid = parcel.readString();
        this.type = parcel.readString();
        this.name = parcel.readString();
        this.phone = parcel.readString();
        this.email = parcel.readString();
        this.birthOrder = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeInt(birthOrder);
    }

    public static final Parcelable.Creator<Person> CREATOR
            = new Parcelable.Creator<Person>() {

        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> person = new HashMap<String, Object>();
        person.put("type", type);
        person.put("name", name);
        person.put("birthOrder", birthOrder);
        person.put("phone", phone);
        person.put("email", email);
        return person;
    }
    
}
