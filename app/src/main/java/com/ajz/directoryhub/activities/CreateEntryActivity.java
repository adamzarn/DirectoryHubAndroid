package com.ajz.directoryhub.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 11/1/17.
 */

public class CreateEntryActivity extends AppCompatActivity implements CreateEntryFragment.ClickListener {

    private CreateEntryFragment createEntryFragment;
    private static final String TAG_CREATE_ENTRY_FRAGMENT = "createEntryFragment";
    private Person pendingPerson;
    private String currentPersonDialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        if (getSupportActionBar() != null) {
            if (getIntent().getExtras().getParcelable("entryToEdit") != null) {
                getSupportActionBar().setTitle(get(R.string.edit_entry_title));
            } else {
                getSupportActionBar().setTitle(get(R.string.add_entry_title));
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        createEntryFragment = (CreateEntryFragment) fragmentManager.findFragmentByTag(TAG_CREATE_ENTRY_FRAGMENT);

        if (createEntryFragment == null) {
            createEntryFragment = new CreateEntryFragment();
            createEntryFragment.setArguments(getIntent().getExtras());
            fragmentManager.beginTransaction()
                    .add(R.id.create_entry_activity_container, createEntryFragment, TAG_CREATE_ENTRY_FRAGMENT)
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

        currentPersonDialogTitle = title;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View personView = inflater.inflate(R.layout.person_dialog, null);
        dialogBuilder.setView(personView);

        final EditText firstNameEditText = (EditText) personView.findViewById(R.id.person_first_name_edit_text);
        final Spinner typeSpinner = (Spinner) personView.findViewById(R.id.person_type_spinner);
        final Spinner birthOrderSpinner = (Spinner) personView.findViewById(R.id.birth_order_spinner);
        final EditText phoneEditText = (EditText) personView.findViewById(R.id.person_phone_edit_text);
        final EditText emailEditText = (EditText) personView.findViewById(R.id.person_email_edit_text);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!TextUtils.equals(typeSpinner.getItemAtPosition(i).toString(), get(R.string.child))) {
                    birthOrderSpinner.setEnabled(false);
                    birthOrderSpinner.setSelection(0);
                } else {
                    birthOrderSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayList<String> typeArray = new ArrayList<String>(Arrays.asList(get(R.string.type), get(R.string.husband), get(R.string.wife), get(R.string.single), get(R.string.child)));
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

        final ArrayList<String> birthOrderArray = new ArrayList<String>(Arrays.asList(get(R.string.birth_order), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
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

        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length()-phoneEditText.getSelectionStart();
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                String phone = string.replaceAll("[^\\d]", "");

                if (!editedFlag) {

                    if (phone.length() >= 6 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = phone.substring(0, 3) + "-" + phone.substring(3,6) + "-" + phone.substring(6);
                        phoneEditText.setText(ans);
                        phoneEditText.setSelection(phoneEditText.getText().length()-cursorComplement);
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = phone.substring(0, 3) + "-" + phone.substring(3);
                        phoneEditText.setText(ans);
                        phoneEditText.setSelection(phoneEditText.getText().length()-cursorComplement);
                    }

                } else {
                    editedFlag = false;
                }
            }
        });

        if (selectedPerson != null) {
            firstNameEditText.setText(selectedPerson.getName());
            typeSpinner.setSelection(typeAdapter.getPosition(selectedPerson.getType()));
            birthOrderSpinner.setSelection(birthOrderAdapter.getPosition(String.valueOf(selectedPerson.getBirthOrder())));
            phoneEditText.setText(selectedPerson.getPhone());
            emailEditText.setText(selectedPerson.getEmail());
            if (!TextUtils.equals(selectedPerson.getType(), get(R.string.child))) {
                birthOrderSpinner.setEnabled(false);
            }
        }

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(get(R.string.submit), new DialogInterface.OnClickListener() {
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
                if (TextUtils.equals(typeSpinner.getSelectedItem().toString(), get(R.string.type))) {
                    type = "";
                } else {
                    type = typeSpinner.getSelectedItem().toString();
                }

                int birthOrder;
                if (TextUtils.equals(birthOrderSpinner.getSelectedItem().toString(), get(R.string.birth_order))) {
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
                pendingPerson = newPerson;
                createEntryFragment.updatePeople(newPerson);
            }
        });
        dialogBuilder.setNegativeButton(
                get(R.string.cancel),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void submitButtonClicked(Entry entry) {
        if (isConnected()) {
            new FirebaseClient().addEntry(CreateEntryActivity.this, getIntent().getExtras().getString("groupUid"), entry);
        } else {
            DialogUtils.showPositiveAlert(CreateEntryActivity.this, get(R.string.no_internet_connection_title), get(R.string.no_internet_connection_message));
        }
    }

    @Override
    public void displayInvalidSubmissionAlert(String title, String message, final int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEntryActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                (get(R.string.ok)),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (error == 0) {
                            onPersonSelected(pendingPerson, currentPersonDialogTitle);
                        }
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
    public void deletePersonClicked(final Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEntryActivity.this);

        builder.setTitle(get(R.string.delete_person_title));
        builder.setMessage(get(R.string.delete_person_message_prefix) + person.getName() + "?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                get(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createEntryFragment.deletePerson(person);
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

    public String get(int i) {
        return getResources().getString(i);
    }

}
