package com.ajz.directoryhub.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adamzarn on 10/23/17.
 */

public class Member {

    private String uid;
    private String name;

    public Member(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> member = new HashMap<String, Object>();
        member.put("uid", uid);
        member.put("name", name);
        return member;
    }

}
