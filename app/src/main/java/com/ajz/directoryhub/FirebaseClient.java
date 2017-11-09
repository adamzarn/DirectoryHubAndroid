package com.ajz.directoryhub;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.ajz.directoryhub.activities.CreateGroupActivity;
import com.ajz.directoryhub.activities.DirectoryActivity;
import com.ajz.directoryhub.activities.MyGroupsActivity;
import com.ajz.directoryhub.adapters.DirectoryAdapter;
import com.ajz.directoryhub.adapters.MyGroupsAdapter;
import com.ajz.directoryhub.adapters.SearchGroupsAdapter;
import com.ajz.directoryhub.fragments.EntryFragment;
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
import java.util.HashMap;
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

    public void getUserGroups(final MyGroupsActivity activity, final MyGroupsAdapter adapter, String userUid, final RecyclerView rv, final ProgressBar pb) {

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
                        getGroup(adapter, groupUid, rv, pb, finished);
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

    public void getGroup(final MyGroupsAdapter adapter, String groupUid, final RecyclerView rv, final ProgressBar pb, final Boolean finished) {

        mDatabase.child("Groups").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                if (ds.exists()) {
                    Group receivedGroup = makeGroup(ds);
                    adapter.appendGroup(receivedGroup);
                }

                if (finished) {
                    pb.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
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

    public void getDirectory(final DirectoryAdapter adapter, String groupUid, final RecyclerView rv, final ProgressBar pb) {

        mDatabase.child("Directories").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                pb.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

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

    public void getWidgetData(final String groupUid) {

        mDatabase.child("Directories").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {

                ArrayList<Entry> receivedEntries = new ArrayList<Entry>();

                for (DataSnapshot entry : ds.getChildren()) {
                    receivedEntries.add(makeEntry(entry));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void getEntry(final EntryFragment fragment, String groupUid, String entryUid, final ScrollView sv, final ProgressBar pb) {

        mDatabase.child("Directories").child(groupUid).child(entryUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                pb.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);
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

    public void joinGroup(final AppCompatActivity activity, final String groupUid, final ArrayList<String> userGroups, String userType) {

        final String userUid = mAuth.getCurrentUser().getUid();
        final String displayName = mAuth.getCurrentUser().getDisplayName();

        final DatabaseReference userGroupsRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("groups");
        final DatabaseReference groupUsersRef = mDatabase.child("Groups").child(groupUid).child(userType).child(userUid);

        userGroupsRef.setValue(userGroups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    groupUsersRef.setValue(displayName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            activity.finish();
                        }
                    });
                }
            }
        });
    }

    public void deleteFromMyGroups(final MyGroupsActivity activity, final String groupUid, String userType, final ArrayList<String> userGroups) {

        final String userUid = mAuth.getCurrentUser().getUid();
        final DatabaseReference userGroupsRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("groups");
        final DatabaseReference groupUsersRef = mDatabase.child("Groups").child(groupUid).child(userType).child(userUid);

        userGroupsRef.setValue(userGroups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                groupUsersRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        activity.finish();
                    }
                });
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
        HashMap<String, Object> admins = (HashMap<String, Object>) ds.child("admins").getValue();
        HashMap<String, Object> users = (HashMap<String, Object>) ds.child("users").getValue();

        return new Group(uid, name, lowercasedName, city, state, password, admins, users, createdBy, lowercasedCreatedBy, createdByUid);
    }

    public void createGroup(final AppCompatActivity activity, final Group newGroup, final byte[] groupLogo, ArrayList<String> currentUserGroups) {

        DatabaseReference newGroupRef;

        final ArrayList<String> updatedUserGroups = currentUserGroups;

        if (newGroup.getUid() == null) {
            newGroupRef = mDatabase.child("Groups").push();
            updatedUserGroups.add(newGroupRef.getKey());
        } else {
            newGroupRef = mDatabase.child("Groups").child(newGroup.getUid());
        }
        final String groupUid = newGroupRef.getKey();
        newGroupRef.setValue(newGroup.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageReference storageRef = mStorage.getReference();
                    if (groupLogo.length > 0) {
                        UploadTask uploadTask = storageRef.child(groupUid + ".jpg").putBytes(groupLogo);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                if (newGroup.getUid() == null) {
                                    joinGroup(activity, groupUid, updatedUserGroups, "admins");
                                } else {
                                    activity.finish();
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (newGroup.getUid() == null) {
                                    joinGroup(activity, groupUid, updatedUserGroups, "admins");
                                } else {
                                    activity.finish();
                                }
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });
                    } else {
                        if (newGroup.getUid() == null) {
                            joinGroup(activity, groupUid, updatedUserGroups, "admins");
                        } else {
                            activity.finish();
                        }
                    }
                }
            }
        });

    }

    public void editGroup(final AppCompatActivity activity, final Group newGroup, final byte[] groupLogo) {

        DatabaseReference groupRef = mDatabase.child("Groups").child(newGroup.getUid());
        final String groupUid = groupRef.getKey();
        groupRef.setValue(newGroup.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageReference storageRef = mStorage.getReference();
                    if (groupLogo.length > 0) {
                        UploadTask uploadTask = storageRef.child(groupUid + ".jpg").putBytes(groupLogo);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                activity.finish();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                activity.finish();
                            }
                        });
                    } else {
                        activity.finish();
                    }
                }
            }
        });
    }

    public void addEntry(final AppCompatActivity activity, String groupUid, Entry entry) {

        DatabaseReference directoryRef = mDatabase.child("Directories").child(groupUid);

        DatabaseReference entryRef;
        if (StringUtils.isMissing(entry.getUid())) {
            entryRef = directoryRef.push();
        } else {
            entryRef = directoryRef.child(entry.getUid());
        }

        entryRef.setValue(entry.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.finish();
            }
        });

    }

    public void deleteGroup(final CreateGroupActivity activity, String groupUid) {

        final DatabaseReference groupRef = mDatabase.child("Groups").child(groupUid);
        final DatabaseReference directoryRef = mDatabase.child("Directories").child(groupUid);
        final StorageReference imageRef = mStorage.getReference().child(groupUid + ".jpg");

        groupRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                directoryRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                activity.finish();
                            }
                        });
                    }
                });
            }
        });
    }

    public void deleteEntry(final DirectoryActivity activity, String groupUid, String entryUid) {
        DatabaseReference directoryRef = mDatabase.child("Directories").child(groupUid).child(entryUid);
        directoryRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.recreate();
            }
        });
    }

    public String getValue(DataSnapshot ds, String key) {
        return ds.child(key).getValue().toString();
    }

}
