package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.fragments.CreateGroupFragment;
import com.ajz.directoryhub.objects.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 10/27/17.
 */

public class CreateGroupActivity extends AppCompatActivity implements CreateGroupFragment.ClickListener {

    private FirebaseAuth mAuth;
    private CreateGroupFragment createGroupFragment;
    private HashMap<String, Object> editedAdmins;
    private HashMap<String, Object> editedUsers;

    private static final String TAG_CREATE_GROUP_FRAGMENT = "createGroupFragment";

    private Group groupToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras().getParcelable("groupToEdit") == null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(get(R.string.create_group_title));
            }
            groupToEdit = new Group();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(get(R.string.edit_group_title));
            }
            groupToEdit = getIntent().getExtras().getParcelable("groupToEdit");
            editedAdmins = groupToEdit.getAdmins();
            editedUsers = groupToEdit.getUsers();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        createGroupFragment = (CreateGroupFragment) fragmentManager.findFragmentByTag(TAG_CREATE_GROUP_FRAGMENT);

        if (createGroupFragment == null) {
            createGroupFragment = new CreateGroupFragment();
            createGroupFragment.setArguments(getIntent().getExtras());
            fragmentManager.beginTransaction()
                    .add(R.id.create_group_activity_container, createGroupFragment, TAG_CREATE_GROUP_FRAGMENT)
                    .commit();
        }

    }

    @Override
    public void onButtonClick(String groupUid, ArrayList<String> groupUids, String groupName, String city, String state, String password, byte[] groupLogo, ProgressBar pb) {

        if (StringUtils.isMissing(groupName)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.missing_group_name_title), get(R.string.missing_group_name_message));
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(city)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.missing_city_title), get(R.string.missing_city_message));
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(state)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.missing_state_title), get(R.string.missing_state_message));
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        if (StringUtils.isMissing(password)) {
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.missing_password_title), get(R.string.missing_password_message));
            pb.setVisibility(View.INVISIBLE);
            return;
        }

        if (isConnected()) {
            if (getIntent().getExtras().getParcelable("groupToEdit") == null) {
                FirebaseUser user = mAuth.getCurrentUser();
                HashMap<String, Object> admins = new HashMap<String, Object>();
                admins.put(user.getUid(), user.getDisplayName());

                Group newGroup = new Group(groupUid, groupName, groupName.toLowerCase(), city, state, password, admins, null, user.getDisplayName(),
                        user.getDisplayName().toLowerCase(), user.getUid());
                new FirebaseClient().createGroup(CreateGroupActivity.this, newGroup, groupLogo, groupUids);
            } else {
                Group editedGroup = new Group(groupUid, groupName, groupName.toLowerCase(), city, state, password, editedAdmins, editedUsers, groupToEdit.getCreatedBy(),
                        groupToEdit.getLowercasedCreatedBy(), groupToEdit.getCreatedByUid());
                new FirebaseClient().editGroup(CreateGroupActivity.this, editedGroup, groupLogo);
            }
        } else {
            pb.setVisibility(View.INVISIBLE);
            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.no_internet_connection_title), get(R.string.no_internet_connection_message));
        }

    }

    @Override
    public void onFocusChange(View view) {
        hideKeyboard(view);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void manageAdministratorsButtonClicked(Group groupBeingEdited) {
        Class manageAdministrators = ManageAdministratorsActivity.class;
        Intent intent = new Intent(getApplicationContext(), manageAdministrators);
        intent.putExtra("groupBeingEdited", groupBeingEdited);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                if (inputStream != null) {
                    try {
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        createGroupFragment.setImage(bytes);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        if (requestCode == 2) {
            Group editedGroup = data.getExtras().getParcelable("editedGroup");
            editedAdmins = editedGroup.getAdmins();
            editedUsers = editedGroup.getUsers();
            groupToEdit.setAdmins(editedAdmins);
            groupToEdit.setUsers(editedUsers);
            createGroupFragment.setGroupToEdit(groupToEdit);
        }
    }

    @Override
    public void deleteGroup(final Group groupToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setTitle(get(R.string.delete_group_title));
        builder.setMessage(get(R.string.delete_group_message));
        builder.setCancelable(true);

        builder.setPositiveButton(
                get(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isConnected()) {
                            new FirebaseClient().deleteGroup(CreateGroupActivity.this, groupToDelete.getUid());
                        } else {
                            DialogUtils.showPositiveAlert(CreateGroupActivity.this, get(R.string.no_internet_connection_title), get(R.string.no_internet_connection_message));
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

    }

    @Override
    public void shareGroup(Group groupToShare) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, get(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, get(R.string.share_email_text) + "\n\n"
                + get(R.string.group_name_email_label) + groupToShare.getName() + "\n"
                + get(R.string.group_created_by_label) + groupToShare.getCreatedBy() + "\n"
                + get(R.string.group_uid_email_label) + groupToShare.getUid() + "\n\n"
                + get(R.string.group_password_label) + groupToShare.getPassword());
        startActivity(intent);
    }

    public String get(int i) {
        return getResources().getString(i);
    }

}
