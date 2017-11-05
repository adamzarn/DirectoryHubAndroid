package com.ajz.directoryhub.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/21/17.
 */

public class Group implements Parcelable {

    private String uid;
    private String name;
    private String lowercasedName;
    private String city;
    private String state;
    private String password;
    private HashMap<String, Object> admins;
    private HashMap<String, Object> users;
    private String createdBy;
    private String lowercasedCreatedBy;
    private String createdByUid;

    public Group() {
    }

    public Group(String uid, String name, String lowercasedName,
                      String city, String state, String password,
                        HashMap<String, Object> admins, HashMap<String, Object> users, String createdBy,
                      String lowercasedCreatedBy, String createdByUid) {
        this.uid = uid;
        this.name = name;
        this.lowercasedName = lowercasedName;
        this.city = city;
        this.state = state;
        this.password = password;
        this.admins = admins;
        this.users = users;
        this.createdBy = createdBy;
        this.lowercasedCreatedBy = lowercasedCreatedBy;
        this.createdByUid = createdByUid;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return this.name;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public String getPassword() {
        return password;
    }

    public HashMap<String, Object> getAdmins() {
        return admins;
    }

    public HashMap<String, Object> getUsers() {
        return users;
    }

    public String getLowercasedCreatedBy() {
        return lowercasedCreatedBy;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public ArrayList<String> getAdminKeys() {
        return new ArrayList<String>(admins.keySet());
    }

    public void setAdmins(HashMap<String, Object> admins) {
        this.admins = admins;
    }

    public void setUsers(HashMap<String, Object> users) {
        this.users = users;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        group.put("lowercasedName", lowercasedName);
        group.put("city", city);
        group.put("state", state);
        group.put("password", password);
        group.put("admins", admins);
        group.put("users", users);
        group.put("createdBy", createdBy);
        group.put("lowercasedCreatedBy", lowercasedCreatedBy);
        group.put("createdByUid", createdByUid);
        return group;
    }

    public Group(Parcel parcel) {
        this.uid = parcel.readString();
        this.name = parcel.readString();
        this.lowercasedName = parcel.readString();
        this.city = parcel.readString();
        this.state = parcel.readString();
        this.password = parcel.readString();
        this.admins = (HashMap<String, Object>) parcel.readSerializable();
        this.users = (HashMap<String, Object>) parcel.readSerializable();
        this.createdBy = parcel.readString();
        this.lowercasedCreatedBy = parcel.readString();
        this.createdByUid = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(lowercasedName);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(password);
        parcel.writeSerializable(admins);
        parcel.writeSerializable(users);
        parcel.writeString(createdBy);
        parcel.writeString(lowercasedCreatedBy);
        parcel.writeString(createdByUid);
    }

    public static final Parcelable.Creator<Group> CREATOR
            = new Parcelable.Creator<Group>() {

        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

}
