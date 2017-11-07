package com.ajz.directoryhub.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.SearchGroupsFragment;
import com.ajz.directoryhub.objects.Group;

import java.util.ArrayList;

/**
 * Created by adamzarn on 10/26/17.
 */

public class SearchGroupsActivity extends AppCompatActivity implements SearchGroupsFragment.OnGroupClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_groups);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(get(R.string.search_groups_title));
        }

        SearchGroupsFragment searchGroupsFragment = new SearchGroupsFragment();
        searchGroupsFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.search_groups_activity_container, searchGroupsFragment)
                    .commit();
        }

    }

    @Override
    public void onGroupSelected(final Group selectedGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        final EditText groupPasswordEditText = (EditText) dialogView.findViewById(R.id.group_password_edit_text);

        builder.setTitle(get(R.string.password_required));
        builder.setMessage(get(R.string.enter_password_prefix) + selectedGroup.getName() + "\"");

        builder.setPositiveButton(get(R.string.submit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (TextUtils.equals(groupPasswordEditText.getText().toString(),selectedGroup.getPassword())) {
                    ArrayList<String> currentUserGroups = getIntent().getStringArrayListExtra("groupUids");
                    currentUserGroups.add(selectedGroup.getUid());
                    new FirebaseClient().joinGroup(SearchGroupsActivity.this, selectedGroup.getUid(), currentUserGroups, "users");
                } else {
                    Toast.makeText(getApplicationContext(), get(R.string.incorrect_password), Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(get(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });

        alert.show();
    }

    public String get(int i) {
        return getResources().getString(i);
    }

}
