package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.fragments.CreateGroupFragment;
import com.ajz.directoryhub.objects.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adamzarn on 10/27/17.
 */

public class CreateGroupActivity extends AppCompatActivity implements CreateGroupFragment.ClickListener {

    private FirebaseAuth mAuth;
    private CreateGroupFragment createGroupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras().getParcelable("groupToEdit") == null) {
            getSupportActionBar().setTitle("Create Group");
        } else {
            getSupportActionBar().setTitle("Edit Group");
        }

        createGroupFragment = new CreateGroupFragment();
        createGroupFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.create_group_activity_container, createGroupFragment)
                    .commit();
        }

    }

    @Override
    public void onButtonClick(String groupUid, ArrayList<String> groupUids, String groupName, String city, String state, String password, byte[] groupLogo, ProgressBar pb) {

        if (StringUtils.isMissing(groupName)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, "Missing Group Name", "Please provide a group name.");
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(city)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, "Missing City", "Please provide a city.");
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(state)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, "Missing State", "Please provide a state.");
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(password)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, "Missing Password", "Please provide a password.");
            pb.setVisibility(View.INVISIBLE);
        }

        FirebaseUser user = mAuth.getCurrentUser();

        HashMap<String, Object> admins = new HashMap<String, Object>();
        admins.put(user.getUid(), user.getDisplayName());

        Group newGroup = new Group(groupUid, groupName, groupName.toLowerCase(), city, state, password, admins, null, user.getDisplayName(),
                user.getDisplayName().toLowerCase(), user.getUid());
        new FirebaseClient().createGroup(CreateGroupActivity.this, newGroup, groupLogo, groupUids);

    }

    @Override
    public void onFocusChange(View view) {
        hideKeyboard(view);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void manageAdministratorsButtonClicked(Group groupBeingEdited) {
        Class manageAdministrators = ManageAdministratorsActivity.class;
        Intent intent = new Intent(getApplicationContext(), manageAdministrators);
        intent.putExtra("groupBeingEdited", groupBeingEdited);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                if (inputStream != null) {
                    try {
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        createGroupFragment.setImage(bytes);
                    } catch (IOException e) {
                        System.out.println("Error");
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
            }
        }
    }

}
