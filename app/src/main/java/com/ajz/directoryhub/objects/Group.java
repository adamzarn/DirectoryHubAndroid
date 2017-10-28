package com.ajz.directoryhub.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/21/17.
 */

public class Group {

    private String uid;
    private String name;
    private String lowercasedName;
    private String city;
    private String state;
    private String password;
    private Map<String, Object> admins;
    private Map<String, Object> users;
    private String createdBy;
    private String lowercasedCreatedBy;
    private String createdByUid;

    public Group(String uid, String name, String lowercasedName,
                      String city, String state, String password,
                        Map<String, Object> admins, Map<String, Object> users, String createdBy,
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

}
