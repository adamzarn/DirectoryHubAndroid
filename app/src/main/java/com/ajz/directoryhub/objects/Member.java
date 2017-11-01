package com.ajz.directoryhub.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/23/17.
 */

public class Member {

    private String uid;
    private String name;
    private Boolean isAdmin;

    public Member(String uid, String name, Boolean isAdmin) {
        this.uid = uid;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> member = new HashMap<String, Object>();
        member.put("uid", uid);
        member.put("name", name);
        return member;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
