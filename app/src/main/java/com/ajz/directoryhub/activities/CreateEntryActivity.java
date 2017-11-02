package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ajz.directoryhub.DialogUtils;
import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.fragments.CreateEntryFragment;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

import java.util.ArrayList;
import java.util.Arrays;
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
        final Spinner birthOrderSpinner = (Spinner) personView.findViewById(R.id.birth_order_spinner);
        final EditText phoneEditText = (EditText) personView.findViewById(R.id.person_phone_edit_text);
        final EditText emailEditText = (EditText) personView.findViewById(R.id.person_email_edit_text);

        final ArrayList<String> typeArray = new ArrayList<String>(Arrays.asList("Type", "Husband", "Wife", "Single", "Child"));
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeArray) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.dropdown_row, null);
                }

                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setText(typeArray.get(position));

                switch (position) {
                    case 0:
                        tv.setEnabled(false);
                        break;
                    default:
                        tv.setEnabled(true);
                        break;
                }
                return v;
            }
        };
        typeSpinner.setAdapter(typeAdapter);

        final ArrayList<String> birthOrderArray = new ArrayList<String>(Arrays.asList("Birth Order", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
        ArrayAdapter<String> birthOrderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, birthOrderArray) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.dropdown_row, null);
                }

                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setText(birthOrderArray.get(position));

                switch (position) {
                    case 0:
                        tv.setEnabled(false);
                        break;
                    default:
                        tv.setEnabled(true);
                        break;
                }
                return v;
            }
        };
        birthOrderSpinner.setAdapter(birthOrderAdapter);

        if (selectedPerson != null) {
            firstNameEditText.setText(selectedPerson.getName());
            typeSpinner.setSelection(typeAdapter.getPosition(selectedPerson.getType()));
            birthOrderSpinner.setSelection(birthOrderAdapter.getPosition(String.valueOf(selectedPerson.getBirthOrder())));
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

                String type;
                if (TextUtils.equals(typeSpinner.getSelectedItem().toString(),"Type")) {
                    type = "";
                } else {
                    type = typeSpinner.getSelectedItem().toString();
                }

                int birthOrder;
                if (TextUtils.equals(birthOrderSpinner.getSelectedItem().toString(),"Birth Order")) {
                    birthOrder = 0;
                } else {
                    birthOrder = Integer.parseInt(birthOrderSpinner.getSelectedItem().toString());
                }

                Person newPerson = new Person(uid,
                        type,
                        firstNameEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        birthOrder);
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

    @Override
    public void displayInvalidPersonAlert(String title, String message) {
        DialogUtils.showPositiveAlert(CreateEntryActivity.this, title, message);
    }

}
