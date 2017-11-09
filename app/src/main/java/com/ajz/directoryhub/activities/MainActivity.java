package com.ajz.directoryhub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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

import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

public class MainActivity extends AppCompatActivity implements LoginFragment.ClickListener {

    private LoginFragment loginFragment;
    private static final String TAG_LOGIN_FRAGMENT = "loginFragment";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(get(R.string.main_activity_title));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(TAG_LOGIN_FRAGMENT);

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.main_activity_container, loginFragment, TAG_LOGIN_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onButtonClick(Button b, final String email, String password, final ProgressBar pb) {
        switch (b.getId()) {
            case R.id.login_button:
                if (StringUtils.isMissing(email)) {
                    pb.setVisibility(View.INVISIBLE);
                    DialogUtils.showPositiveAlert(MainActivity.this, get(R.string.missing_email_title), get(R.string.missing_email_message));
                    return;
                } else if (StringUtils.isMissing(password)) {
                    pb.setVisibility(View.INVISIBLE);
                    DialogUtils.showPositiveAlert(MainActivity.this, get(R.string.missing_password_title), get(R.string.missing_password_message));
                    return;
                } else {
                    if (isConnected()) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pb.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful()) {
                                    startMyGroups();
                                } else {
                                    DialogUtils.showPositiveAlert(MainActivity.this, get(R.string.error_title), task.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        pb.setVisibility(View.INVISIBLE);
                        DialogUtils.showPositiveAlert(MainActivity.this, get(R.string.no_internet_connection_title), get(R.string.no_internet_connection_message));
                    }
                }
                break;
            case R.id.create_account_button:
                Class createAccount = CreateAccountActivity.class;
                Intent createAccountIntent = new Intent(getApplicationContext(), createAccount);
                startActivity(createAccountIntent);
                break;
            case R.id.forgot_password_button:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(get(R.string.send_password_reset_email_title));
                builder.setMessage(get(R.string.send_password_reset_email_message_prefix) + email + "?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        get(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if (isConnected()) {
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialog.cancel();
                                            if (task.isSuccessful()) {
                                                DialogUtils.showPositiveAlert(getApplicationContext(), get(R.string.password_reset_email_sent), get(R.string.password_reset_email_sent_message));
                                            }
                                        }
                                    });
                                } else {
                                    DialogUtils.showPositiveAlert(MainActivity.this, get(R.string.no_internet_connection_title), get(R.string.no_internet_connection_message));
                                }
                            }
                        });

                builder.setNegativeButton(
                        get(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert = builder.create();

                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    }
                });

                alert.show();

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

    public String get(int i) {
        return getResources().getString(i);
    }

}
