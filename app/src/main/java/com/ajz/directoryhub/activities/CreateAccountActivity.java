package com.ajz.directoryhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.fragments.CreateAccountFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by adamzarn on 10/20/17.
 */
public class CreateAccountActivity extends AppCompatActivity implements CreateAccountFragment.ClickListener {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        CreateAccountFragment createAccountFragment = new CreateAccountFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.create_account_activity_container, createAccountFragment)
                    .commit();
        }

    }

    @Override
    public void onButtonClick(String firstName, String lastName, String email, String password, String passwordVerification, final ProgressBar pb) {

        if (StringUtils.isMissing(firstName)) {
            DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Missing First Name", "Please provide a first name.");
            pb.setVisibility(View.GONE);
            return;
        }
        if (StringUtils.isMissing(lastName)) {
            DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Missing Last Name", "Please provide a last name.");
            pb.setVisibility(View.GONE);
            return;
        }
        if (StringUtils.isMissing(email)) {
            DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Missing Email", "Please provide an email.");
            pb.setVisibility(View.GONE);
            return;
        }
        if (StringUtils.isMissing(password)) {
            DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Missing Password", "Please provide a password.");
            pb.setVisibility(View.GONE);
            return;
        }

        if (!TextUtils.equals(password, passwordVerification)) {
            DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Password Mismatch", "Your passwords do not match. Please try again.");
            pb.setVisibility(View.GONE);
            return;
        }

        final String displayName = firstName.trim() + " " + lastName.trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    setDisplayName(user, displayName);
                    new FirebaseClient().addNewUser(displayName);
                } else {
                    DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Error", task.getException().getMessage());
                }
            }
        });

    }

    public void setDisplayName(FirebaseUser user, String displayName) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startMyGroups();
                } else {
                    DialogUtils.showPositiveAlert(CreateAccountActivity.this, "Error", task.getException().getMessage());
                }
            }
        });

    }

    public void startMyGroups() {
        Class myGroups = MyGroupsActivity.class;
        Intent myGroupsIntent = new Intent(getApplicationContext(), myGroups);
        startActivity(myGroupsIntent);
    }

}
