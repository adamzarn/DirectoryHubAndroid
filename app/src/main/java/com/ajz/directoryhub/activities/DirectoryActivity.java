package com.ajz.directoryhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.DirectoryFragment;
import com.ajz.directoryhub.objects.Entry;

/**
 * Created by adamzarn on 10/21/17.
 */

public class DirectoryActivity extends AppCompatActivity implements DirectoryFragment.OnEntryClickListener {

    private String groupUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        getSupportActionBar().setTitle("Directory");

        if (getIntent().getExtras() != null) {
            groupUid = getIntent().getExtras().getString("groupUid");
        } else {
            groupUid = savedInstanceState.getString("groupUid");
        }

        DirectoryFragment directoryFragment = new DirectoryFragment();
        directoryFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.directory_activity_container, directoryFragment)
                    .commit();
        }

    }

    @Override
    public void onEntrySelected(Entry selectedEntry) {
        Class entry = EntryActivity.class;
        Intent entryIntent = new Intent(getApplicationContext(), entry);
        entryIntent.putExtra("groupUid", groupUid);
        entryIntent.putExtra("entryUid", selectedEntry.getUid());
        entryIntent.putExtra("isAdmin", getIntent().getExtras().getBoolean("isAdmin"));
        startActivity(entryIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupUid", groupUid);
    }
}
