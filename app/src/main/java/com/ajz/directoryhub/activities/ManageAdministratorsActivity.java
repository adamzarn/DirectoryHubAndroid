package com.ajz.directoryhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.ManageAdministratorsFragment;
import com.ajz.directoryhub.objects.Group;

/**
 * Created by adamzarn on 10/30/17.
 */

public class ManageAdministratorsActivity extends AppCompatActivity implements ManageAdministratorsFragment.OnAdminsEditedListener {

    private ManageAdministratorsFragment manageAdministratorsFragment;
    private static final String TAG_MANAGE_ADMINISTRATORS_FRAGMENT = "manageAdministratorsFragment";
    private Group editedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_administrators);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(get(R.string.manage_administrators_title));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        manageAdministratorsFragment = (ManageAdministratorsFragment) fragmentManager.findFragmentByTag(TAG_MANAGE_ADMINISTRATORS_FRAGMENT);

        if (manageAdministratorsFragment == null) {
            manageAdministratorsFragment = new ManageAdministratorsFragment();
            manageAdministratorsFragment.setArguments(getIntent().getExtras());
            fragmentManager.beginTransaction()
                    .add(R.id.manage_administrators_activity_container, manageAdministratorsFragment, TAG_MANAGE_ADMINISTRATORS_FRAGMENT)
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("editedGroup", editedGroup);
        setResult(2, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("editedGroup", editedGroup);
                setResult(2, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void adminsEdited(Group group) {
        this.editedGroup = group;
    }

    public String get(int i) {
        return getResources().getString(i);
    }

}
