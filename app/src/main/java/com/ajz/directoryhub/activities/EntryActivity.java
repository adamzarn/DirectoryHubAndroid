package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.fragments.EntryFragment;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

/**
 * Created by adamzarn on 10/24/17.
 */

public class EntryActivity extends AppCompatActivity implements EntryFragment.OnEditEntryClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

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

    @Override
    public void presentContactOptions(final Person person, String lastName) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(EntryActivity.this);
        final String fullName = person.getName() + " " + lastName;

        builder1.setTitle(fullName);
        builder1.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();

        final View contactPersonView = inflater.inflate(R.layout.contact_person_view, null);
        builder1.setView(contactPersonView);

        Button callButton = (Button) contactPersonView.findViewById(R.id.call_button);
        Button textButton = (Button) contactPersonView.findViewById(R.id.text_button);
        Button emailButton = (Button) contactPersonView.findViewById(R.id.email_button);
        Button addToContactsButton = (Button) contactPersonView.findViewById(R.id.add_to_contacts_button);

        builder1.setNegativeButton(
                get(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert11 = builder1.create();

        if (StringUtils.isMissing(person.getPhone())) {
            callButton.setVisibility(View.GONE);
            textButton.setVisibility(View.GONE);
        } else {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("tel:" + person.getPhone()));
                    startActivity(intent);
                    alert11.dismiss();
                }
            });
            textButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + person.getPhone()));
                    startActivity(intent);
                    alert11.dismiss();
                }
            });
        }

        if (StringUtils.isMissing(person.getEmail())) {
            emailButton.setVisibility(View.GONE);
        } else {
            emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + person.getEmail()));
                    startActivity(intent);
                    alert11.dismiss();
                }
            });
        }

        addToContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, fullName)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, person.getPhone())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, person.getEmail());
                startActivityForResult(contactIntent, 1);
                alert11.dismiss();
            }
        });

        alert11.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.added_contact_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String get(int i) {
        return getResources().getString(i);
    }

}
