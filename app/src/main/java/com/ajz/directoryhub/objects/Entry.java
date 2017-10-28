package com.ajz.directoryhub.objects;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by adamzarn on 10/22/17.
 */

public class Entry {

    private String uid;
    private String name;
    private String phone;
    private String email;
    private CustomAddress address;
    private ArrayList<Person> people;

    public Entry(String uid, String name, String phone, String email, CustomAddress address, ArrayList<Person> people) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.people = people;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public CustomAddress getAddress() {
        return address;
    }

    public ArrayList<Person> getPeople() {
        return this.people;
    }

    public String getHeader() {

        String husbandFirstName = "";
        String wifeFirstName = "";
        String singleFirstName = "";


        for (Person person : people) {
            if (TextUtils.equals(person.getType(), "Single")) {
                singleFirstName = person.getName();
            } else if (TextUtils.equals(person.getType(), "Husband")) {
                husbandFirstName = person.getName();
            } else if (TextUtils.equals(person.getType(), "Wife")) {
                wifeFirstName = person.getName();
            }
        }

        if (!TextUtils.equals(singleFirstName, "")) {
            return this.name + ", " + singleFirstName;
        } else {
            return this.name + ", " + husbandFirstName + " & " + wifeFirstName;
        }

    }

    public String getChildrenString() {

        ArrayList<Person> children = new ArrayList<Person>();
        for (Person person : this.people) {
            if (TextUtils.equals(person.getType(), "Child")) {
                children.add(person);
            }
        }

        Collections.sort(children, new Comparator<Person>() {
            @Override public int compare(Person p1, Person p2) {
                return p1.getBirthOrder() - p2.getBirthOrder();
            }
        });

        String childrenString = "";
        int i = 0;

        if (children.size() == 2) {
            return children.get(0).getName() + " & " + children.get(1).getName();
        }

        for (Person child : children) {
            if (TextUtils.equals(childrenString, "")) {
                childrenString = child.getName();
            } else if (i == children.size() - 1) {
                childrenString = childrenString + ", & " + child.getName();
            } else {
                childrenString = childrenString + ", " + child.getName();
            }
            i = i + 1;
        }

        return childrenString;

    }

    private String getEntryStatus() {
        for (Person person : people) {
            if (TextUtils.equals(person.getType(),"Single")) {
                return "Single";
            }
        }
        return "Married";
    }

    public String getTitle() {
        if (TextUtils.equals(getEntryStatus(),"Single")) {
            String firstName = "";
            for (Person person : people) {
                if (TextUtils.equals(person.getType(), "Single")) {
                    firstName = person.getName();
                }
            }
            return firstName + " " + this.getName();
        } else {
            return "The " + this.getName() + " Family";
        }
    }

}
