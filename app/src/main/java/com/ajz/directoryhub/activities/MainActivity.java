package com.ajz.directoryhub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.fragments.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.ClickListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle(getResources().getString(R.string.main_activity_title));

        LoginFragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.main_activity_container, loginFragment)
                    .commit();
        }
    }

    @Override
    public void onButtonClick(Button b, final String email, String password, final ProgressBar pb) {
        switch (b.getId()) {
            case R.id.login_button:
                if (!StringUtils.isMissing(email) && !StringUtils.isMissing(password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pb.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                startMyGroups();
                            } else {
                                DialogUtils.showPositiveAlert(MainActivity.this, "Error", task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    pb.setVisibility(View.INVISIBLE);
                    DialogUtils.showPositiveAlert(MainActivity.this, "Missing Email or Password", "You must enter an email and password to login.");
                }
                break;
            case R.id.create_account_button:
                Class createAccount = CreateAccountActivity.class;
                Intent createAccountIntent = new Intent(getApplicationContext(), createAccount);
                startActivity(createAccountIntent);
                break;
            case R.id.forgot_password_button:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Send Password Reset Email");
                builder1.setMessage("Send email to " + email + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialog.cancel();
                                                if (task.isSuccessful()) {
                                                    System.out.println("Success");
                                                }
                                            }
                                        });
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startMyGroups();
        }
    }

    public void startMyGroups() {
        Class myGroups = MyGroupsActivity.class;
        Intent myGroupsIntent = new Intent(getApplicationContext(), myGroups);
        startActivity(myGroupsIntent);
    }

}
