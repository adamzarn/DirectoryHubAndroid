package com.ajz.directoryhub.objects;

/**
 * Created by adamzarn on 10/22/17.
 */

public class Person {

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
}
