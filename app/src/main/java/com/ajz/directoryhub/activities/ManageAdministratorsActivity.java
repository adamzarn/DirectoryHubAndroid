package com.ajz.directoryhub.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.ManageAdministratorsFragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by adamzarn on 10/30/17.
 */

public class ManageAdministratorsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ManageAdministratorsFragment manageAdministratorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_administrators);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Manage Administrators");

        manageAdministratorsFragment = new ManageAdministratorsFragment();
        manageAdministratorsFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.manage_administrators_activity_container, manageAdministratorsFragment)
                    .commit();
        }

    }

}
