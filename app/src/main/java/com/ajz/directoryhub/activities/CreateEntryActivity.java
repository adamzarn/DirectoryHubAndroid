package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.CreateEntryFragment;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

import java.util.UUID;

/**
 * Created by adamzarn on 11/1/17.
 */

public class CreateEntryActivity extends AppCompatActivity implements CreateEntryFragment.ClickListener {

    private CreateEntryFragment createEntryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        if (getIntent().getExtras().getParcelable("entryToEdit") != null) {
            getSupportActionBar().setTitle("Edit Entry");
        } else {
            getSupportActionBar().setTitle("Add Entry");
        }

        createEntryFragment = new CreateEntryFragment();
        createEntryFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.create_entry_activity_container, createEntryFragment)
                    .commit();
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
    public void onPersonSelected(final Person selectedPerson, String title) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View personView = inflater.inflate(R.layout.person_dialog, null);
        dialogBuilder.setView(personView);

        final EditText firstNameEditText = (EditText) personView.findViewById(R.id.person_first_name_edit_text);
        final Spinner typeSpinner = (Spinner) personView.findViewById(R.id.person_type_spinner);
        final NumberPicker birthOrderNumberPicker = (NumberPicker) personView.findViewById(R.id.birth_order_number_picker);
        final EditText phoneEditText = (EditText) personView.findViewById(R.id.person_phone_edit_text);
        final EditText emailEditText = (EditText) personView.findViewById(R.id.person_email_edit_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.person_type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        birthOrderNumberPicker.setMinValue(0);
        birthOrderNumberPicker.setMaxValue(20);

        if (selectedPerson != null) {
            firstNameEditText.setText(selectedPerson.getName());
            typeSpinner.setSelection(adapter.getPosition(selectedPerson.getType()));
            birthOrderNumberPicker.setValue(selectedPerson.getBirthOrder());
            phoneEditText.setText(selectedPerson.getPhone());
            emailEditText.setText(selectedPerson.getEmail());
        }

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String uid;
                if (selectedPerson != null) {
                    if (selectedPerson.getUid() != null) {
                        uid = selectedPerson.getUid();
                    } else {
                        uid = UUID.randomUUID().toString();
                    }
                } else {
                    uid = UUID.randomUUID().toString();
                }
                Person newPerson = new Person(uid,
                        typeSpinner.getSelectedItem().toString(),
                        firstNameEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        birthOrderNumberPicker.getValue());
                createEntryFragment.updatePeople(newPerson);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void submitButtonClicked(Entry entry) {
        new FirebaseClient().addEntry(CreateEntryActivity.this, getIntent().getExtras().getString("groupUid"), entry);
    }


}
