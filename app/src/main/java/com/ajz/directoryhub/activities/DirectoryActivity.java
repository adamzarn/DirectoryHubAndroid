package com.ajz.directoryhub.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.ajz.directoryhub.DirectoryWidgetProvider;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.DirectoryFragment;
import com.ajz.directoryhub.objects.Entry;

import static com.ajz.directoryhub.DirectoryHubApplication.getContext;

/**
 * Created by adamzarn on 10/21/17.
 */

public class DirectoryActivity extends AppCompatActivity implements DirectoryFragment.OnEntryClickListener {

    private String groupUid;
    private DirectoryFragment directoryFragment;
    private static final String TAG_DIRECTORY_FRAGMENT = "directoryFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(get(R.string.directory_title));
        }

        if (getIntent().getExtras() != null) {
            groupUid = getIntent().getExtras().getString("groupUid");
        } else {
            groupUid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("groupUid","");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        directoryFragment = (DirectoryFragment) fragmentManager.findFragmentByTag(TAG_DIRECTORY_FRAGMENT);

        if (directoryFragment == null) {
            directoryFragment = new DirectoryFragment();
            directoryFragment.setArguments(getIntent().getExtras());
            fragmentManager.beginTransaction()
                    .add(R.id.directory_activity_container, directoryFragment, TAG_DIRECTORY_FRAGMENT)
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
    public void onDeleteEntryClicked(final Entry entryToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryActivity.this);
        builder.setTitle(get(R.string.delete_entry_title));
        builder.setMessage(get(R.string.delete_entry_message));
        builder.setCancelable(true);

        builder.setPositiveButton(
                get(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new FirebaseClient().deleteEntry(DirectoryActivity.this, groupUid, entryToDelete.getUid());
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
    }

    @Override
    public void onAddEntrySelected() {
        Class createEntry = CreateEntryActivity.class;
        Intent intent = new Intent(getApplicationContext(), createEntry);
        intent.putExtra("groupUid", getIntent().getExtras().getString("groupUid"));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupUid", groupUid);
    }

    public void updateWidget() {
        Intent intent = new Intent(this, DirectoryWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getContext(), DirectoryWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public String get(int i) {
        return getResources().getString(i);
    }

}
