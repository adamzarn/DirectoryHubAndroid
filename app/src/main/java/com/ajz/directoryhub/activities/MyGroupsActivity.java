package com.ajz.directoryhub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.MyGroupsFragment;
import com.ajz.directoryhub.objects.Group;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by adamzarn on 10/21/17.
 */

public class MyGroupsActivity extends AppCompatActivity implements MyGroupsFragment.OnGroupClickListener, MyGroupsFragment.OnAddGroupFabClickListener {

    private FirebaseAuth mAuth;
    private ArrayList<String> groupUids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("My Groups");

        MyGroupsFragment myGroupsFragment = new MyGroupsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.my_groups_activity_container, myGroupsFragment)
                    .commit();
        }

    }

    @Override
    public void onGroupSelected(Group selectedGroup) {
        Class directory = DirectoryActivity.class;
        Intent directoryIntent = new Intent(getApplicationContext(), directory);
        directoryIntent.putExtra("groupUid", selectedGroup.getUid());
        if (!selectedGroup.getAdminKeys().contains(mAuth.getCurrentUser().getUid())) {
            directoryIntent.putExtra("isAdmin", false);
        } else {
            directoryIntent.putExtra("isAdmin", true);
        }
        startActivity(directoryIntent);
    }

    @Override
    public void onGroupToEditSelected(Group groupToEdit) {
        Class editGroup = CreateGroupActivity.class;
        Intent editGroupIntent = new Intent(getApplicationContext(), editGroup);
        editGroupIntent.putExtra("groupToEdit", groupToEdit);
        startActivity(editGroupIntent);
    }

    @Override
    public void onGroupToDeleteSelected(final Group groupToDelete) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyGroupsActivity.this);
        builder1.setTitle("Delete Group");
        builder1.setMessage("This will only remove \"" + groupToDelete.getName() + "\" from \"My Groups\". You will be able to add it back again later. Continue?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String groupUid = groupToDelete.getUid();
                        groupUids.remove(groupUid);
                        new FirebaseClient().deleteFromMyGroups(MyGroupsActivity.this, groupUid, groupUids);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item:
                mAuth.signOut();
                Class main = MainActivity.class;
                Intent mainIntent = new Intent(getApplicationContext(), main);
                startActivity(mainIntent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_groups_menu, menu);
        MenuItem displayNameItem = menu.findItem(R.id.display_name);
        displayNameItem.setTitle(mAuth.getCurrentUser().getDisplayName());
        return true;
    }

    @Override
    public void onAddGroupFabClicked() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyGroupsActivity.this);
        builder1.setTitle("Add Group");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Create",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Class createGroup = CreateGroupActivity.class;
                        Intent intent = new Intent(getApplicationContext(), createGroup);
                        intent.putStringArrayListExtra("groupUids", groupUids);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "Search",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Class searchGroups = SearchGroupsActivity.class;
                        Intent intent = new Intent(getApplicationContext(), searchGroups);
                        intent.putStringArrayListExtra("groupUids", groupUids);
                        startActivity(intent);
                    }
                });

        builder1.setNeutralButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setGroupUids(ArrayList<String> receivedGroupUids) {
        groupUids = receivedGroupUids;
    }

    public ArrayList<String> getGroupUids() {
        return groupUids;
    }

    public void displayOnlyAdminAlert() {
        DialogUtils.showPositiveAlert(MyGroupsActivity.this, "Hold On", "You are the only administrator for this group, so you cannot remove it from \"My Groups\".");
    }

}
