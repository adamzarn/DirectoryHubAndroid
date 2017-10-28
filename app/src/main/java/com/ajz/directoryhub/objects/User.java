package com.ajz.directoryhub.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/23/17.
 */

public class User {

    private String uid;
    private String name;
    private ArrayList<String> groups;

    public User(String uid, String name, ArrayList<String> groups) {
        this.uid = uid;
        this.name = name;
        this.groups = groups;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("name", name);
        user.put("groups", groups);
        return user;
    }

}
