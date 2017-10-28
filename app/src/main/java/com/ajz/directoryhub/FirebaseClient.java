package com.ajz.directoryhub;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ajz.directoryhub.activities.MyGroupsActivity;
import com.ajz.directoryhub.adapters.DirectoryAdapter;
import com.ajz.directoryhub.adapters.MyGroupsAdapter;
import com.ajz.directoryhub.adapters.SearchGroupsAdapter;
import com.ajz.directoryhub.fragments.EntryFragment;
import com.ajz.directoryhub.fragments.MyGroupsFragment;
import com.ajz.directoryhub.objects.CustomAddress;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Group;
import com.ajz.directoryhub.objects.Person;
import com.ajz.directoryhub.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by adamzarn on 10/23/17.
 */

public class FirebaseClient {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;

    public FirebaseClient() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public void getUserGroups(final MyGroupsActivity activity, final MyGroupsAdapter adapter, String userUid, final ProgressBar pb) {

        mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                int i = 1;
                Boolean finished = false;
                ArrayList<String> groupUids = (ArrayList<String>) ds.child("groups").getValue();
                if (groupUids != null) {
                    activity.setGroupUids(groupUids);
                    for (String groupUid : groupUids) {
                        if (i == groupUids.size()) {
                            finished = true;
                        }
                        getGroup(adapter, groupUid, pb, finished);
                        i++;
                    }
                } else {
                    pb.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void getGroup(final MyGroupsAdapter adapter, String groupUid, final ProgressBar pb, final Boolean finished) {

        mDatabase.child("Groups").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                Group receivedGroup = makeGroup(ds);
                adapter.appendGroup(receivedGroup);

                if (finished) {
                    pb.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void queryGroupsByUniqueID(final ArrayList<String> groupUids, final SearchGroupsAdapter adapter, final RecyclerView recyclerView, String groupUid, final ProgressBar pb) {

        mDatabase.child("Groups").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                if (ds.exists()) {
                    Group receivedGroup = makeGroup(ds);
                    if (!groupUids.contains(receivedGroup.getUid())) {
                        adapter.deliverGroup(receivedGroup);
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void getDirectory(final DirectoryAdapter adapter, String groupUid) {

        mDatabase.child("Directories").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                ArrayList<Entry> receivedEntries = new ArrayList<Entry>();

                for (DataSnapshot entry : ds.getChildren()) {
                    receivedEntries.add(makeEntry(entry));
                }

                adapter.setData(receivedEntries);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


    }

    public void getEntry(final EntryFragment fragment, String groupUid, String entryUid) {

        mDatabase.child("Directories").child(groupUid).child(entryUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                Entry selectedEntry = makeEntry(ds);
                fragment.populateFragment(selectedEntry);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void queryGroups(final ArrayList<String> groupUids, final SearchGroupsAdapter adapter, final RecyclerView recyclerView, String query, String searchKey, final ProgressBar pb) {

        mDatabase.child("Groups").orderByChild(searchKey).startAt(query).limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                ArrayList<Group> receivedGroups = new ArrayList<>();
                for (DataSnapshot child: ds.getChildren()) {
                    Group receivedGroup = makeGroup(child);
                    if (!groupUids.contains(receivedGroup.getUid())) {
                        receivedGroups.add(receivedGroup);
                    }
                }
                adapter.deliverGroups(receivedGroups);
                pb.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void updateUserGroups(final AppCompatActivity activity, final String newGroupUid) {

        final DatabaseReference userGroupsRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("groups");

        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                ArrayList<String> userGroups = (ArrayList<String>) ds.getValue();
                if (userGroups == null) {
                    ArrayList<String> newUserGroups = new ArrayList<String>();
                    userGroups = newUserGroups;
                }
                userGroups.add(newGroupUid);

                userGroupsRef.setValue(userGroups).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        activity.finish();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void deleteFromMyGroups(final MyGroupsFragment fragment, ArrayList<String> currentUserGroups, String groupUid) {

        final DatabaseReference userGroupsRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("groups");

        currentUserGroups.remove(groupUid);

        userGroupsRef.setValue(currentUserGroups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fragment.reloadData();
            }
        });

    }

    public void addNewUser(String displayName) {

        DatabaseReference userRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
        User newUser = new User(mAuth.getCurrentUser().getUid(), displayName, new ArrayList<String>());
        userRef.setValue(newUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("Success");
            }
        });
    }

    public Entry makeEntry(DataSnapshot entry) {

        String uid = entry.getKey();
        String name = getValue(entry, "name");
        String phone = getValue(entry, "phone");
        String email = getValue(entry, "email");

        String street = entry.child("Address").child("street").getValue().toString();
        String line2 = entry.child("Address").child("line2").getValue().toString();
        String line3 = entry.child("Address").child("line3").getValue().toString();
        String city = entry.child("Address").child("city").getValue().toString();
        String state = entry.child("Address").child("state").getValue().toString();
        String zip = entry.child("Address").child("zip").getValue().toString();

        CustomAddress address = new CustomAddress(street, line2, line3, city, state, zip);

        Map<String, Object> p = (Map<String, Object>) entry.child("People").getValue();
        ArrayList<Person> people = new ArrayList<Person>();
        for (Map.Entry<String, Object> person : p.entrySet()) {
            Map<String, Object> info = (Map<String, Object>) person.getValue();
            String personUid = person.getKey();
            String type = info.get("type").toString();
            String personName = info.get("name").toString();
            String personPhone = info.get("phone").toString();
            String personEmail = info.get("email").toString();
            int birthOrder = Integer.parseInt(info.get("birthOrder").toString());
            Person newPerson = new Person(personUid, type, personName, personPhone, personEmail, birthOrder);
            people.add(newPerson);
        }

        return new Entry(uid, name, phone, email, address, people);

    }

    public Group makeGroup(DataSnapshot ds) {

        String uid = ds.getKey();
        String name = getValue(ds, "name");
        String lowercasedName = getValue(ds, "lowercasedName");
        String city = getValue(ds, "city");
        String state = getValue(ds, "state");
        String password = getValue(ds, "password");
        String createdBy = getValue(ds, "createdBy");
        String lowercasedCreatedBy = getValue(ds, "lowercasedCreatedBy");
        String createdByUid = getValue(ds, "createdByUid");
        Map<String, Object> admins = (Map<String, Object>) ds.child("admins").getValue();
        Map<String, Object> users = (Map<String, Object>) ds.child("users").getValue();

        return new Group(uid, name, lowercasedName, city, state, password, admins, users, createdBy, lowercasedCreatedBy, createdByUid);
    }

    public void createGroup(final AppCompatActivity activity, Group newGroup, final byte[] groupLogo, ArrayList<String> currentUserGroups) {

        DatabaseReference newGroupRef = mDatabase.child("Groups").push();
        final String groupUid = newGroupRef.getKey();
        newGroupRef.setValue(newGroup.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageReference storageRef = mStorage.getReference();
                    UploadTask uploadTask = storageRef.child(groupUid + ".jpg").putBytes(groupLogo);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            updateUserGroups(activity, groupUid);
                            System.out.println(exception.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("photoUploaded");
                            updateUserGroups(activity, groupUid);
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            System.out.println(downloadUrl);
                        }
                    });
                }
            }
        });

    }

    public String getValue(DataSnapshot ds, String key) {
        return ds.child(key).getValue().toString();
    }

}
