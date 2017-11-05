package com.ajz.directoryhub.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by adamzarn on 10/22/17.
 */

public class Entry implements Parcelable {

    private String uid;
    private String name;
    private String phone;
    private String email;
    private CustomAddress address;
    private ArrayList<Person> people;

    public Entry() {
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(CustomAddress address) {
        this.address = address;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
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

    private Entry(Parcel parcel) {
        this.uid = parcel.readString();
        this.name = parcel.readString();
        this.phone = parcel.readString();
        this.email = parcel.readString();
        this.address = parcel.readParcelable(Entry.class.getClassLoader());
        this.people = new ArrayList<Person>();
        parcel.readTypedList(people, Person.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeParcelable(address, i);
        parcel.writeTypedList(people);
    }

    public static final Parcelable.Creator<Entry> CREATOR
            = new Parcelable.Creator<Entry>() {

        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> entry = new HashMap<String, Object>();
        entry.put("name", name);
        entry.put("phone", phone);
        entry.put("email", email);
        entry.put("Address", address.toMap());
        entry.put("People", peopleToMap(people));
        return entry;
    }

    public Map<String, Object> peopleToMap(ArrayList<Person> peopleArrayList) {
        Map<String, Object> people = new HashMap<String, Object>();
        for (Person person : peopleArrayList) {
            people.put(UUID.randomUUID().toString(), person.toMap());
        }
        return people;
    }

}
