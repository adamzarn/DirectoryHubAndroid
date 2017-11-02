package com.ajz.directoryhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.EntryFragment;
import com.ajz.directoryhub.objects.Entry;

/**
 * Created by adamzarn on 10/24/17.
 */

public class EntryActivity extends AppCompatActivity implements EntryFragment.OnEditEntryClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        getSupportActionBar().setTitle("");

        EntryFragment entryFragment = new EntryFragment();
        entryFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.entry_activity_container, entryFragment)
                    .commit();
        }

    }

    @Override
    public void onEditEntry(Entry entryToEdit) {
        Class createEntry = CreateEntryActivity.class;
        Intent intent = new Intent(getApplicationContext(), createEntry);
        intent.putExtra("entryToEdit", entryToEdit);
        intent.putExtra("groupUid", getIntent().getExtras().getString("groupUid"));
        startActivity(intent);
    }
}
