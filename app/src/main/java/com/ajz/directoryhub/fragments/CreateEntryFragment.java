package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.objects.CustomAddress;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adamzarn on 11/1/17.
 */

public class CreateEntryFragment extends Fragment {

    ClickListener mCallback;
    private Entry entryToEdit;
    private ArrayAdapter<String> stateAdapter;

    private ViewGroup parent;

    @BindView(R.id.people_linear_layout)
    LinearLayout peopleLinearLayout;

    @BindView(R.id.adults_linear_layout)
    LinearLayout adultsLinearLayout;

    @BindView(R.id.children_linear_layout)
    LinearLayout childrenLinearLayout;

    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;

    @BindView(R.id.entry_phone_edit_text)
    EditText entryPhoneEditText;

    @BindView(R.id.entry_email_edit_text)
    EditText entryEmailEditText;

    @BindView(R.id.entry_street_edit_text)
    EditText entryStreetEditText;

    @BindView(R.id.entry_line2_edit_text)
    EditText entryLine2EditText;

    @BindView(R.id.entry_line3_edit_text)
    EditText entryLine3EditText;

    @BindView(R.id.city_edit_text)
    EditText cityEditText;

    @BindView(R.id.state_spinner)
    Spinner stateSpinner;

    @BindView(R.id.zip_edit_text)
    EditText zipEditText;

    @BindView(R.id.add_person_button)
    Button addPersonButton;

    @OnClick(R.id.add_person_button)
    public void addPersonButtonClicked() {
        mCallback.onPersonSelected(null, get(R.string.add_person_title));
    }

    @BindView(R.id.submit_create_entry_button)
    Button createEntryButton;

    @BindView(R.id.submit_create_entry_progress_bar)
    ProgressBar submitCreateEntryProgressBar;

    @OnClick(R.id.submit_create_entry_button)
    public void submitButtonClicked() {

        String lastName = lastNameEditText.getText().toString();
        String phone = entryPhoneEditText.getText().toString();
        String email = entryEmailEditText.getText().toString();
        String street = entryStreetEditText.getText().toString();
        String line2 = entryLine2EditText.getText().toString();
        String line3 = entryLine3EditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateSpinner.getSelectedItem().toString();
        String zip = zipEditText.getText().toString();
        CustomAddress address = new CustomAddress(street, line2, line3, city, state, zip);

        if (StringUtils.isMissing(lastName)) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.missing_entry_last_name_title), get(R.string.missing_entry_last_name_message), 1);
            return;
        }

        if (getAdults(entryToEdit.getPeople()).size() == 0) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.no_adults_title), get(R.string.no_adults_message), 1);
            return;
        }

        if (phone.length() < 12 && phone.length() > 0) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.bad_phone_number_title), get(R.string.bad_phone_number_message), 1);
            return;
        }

        if (getAdults(entryToEdit.getPeople()).size() == 1) {
            if (TextUtils.equals(getAdults(entryToEdit.getPeople()).get(0).getType(), get(R.string.husband))) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.missing_spouse_title), get(R.string.missing_wife_message), 1);
                return;
            } else if (TextUtils.equals(getAdults(entryToEdit.getPeople()).get(0).getType(), get(R.string.wife))) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.missing_spouse_title), get(R.string.missing_husband_message), 1);
                return;
            }
        }

        submitCreateEntryProgressBar.bringToFront();
        submitCreateEntryProgressBar.setVisibility(View.VISIBLE);

        entryToEdit.setName(lastName);
        entryToEdit.setPhone(phone);
        entryToEdit.setEmail(email);
        entryToEdit.setAddress(address);
        mCallback.submitButtonClicked(entryToEdit);

    }

    public interface ClickListener {
        void onFocusChange(View view);
        void onPersonSelected(Person selectedPerson, String title);
        void submitButtonClicked(Entry entry);
        void displayInvalidSubmissionAlert(String title, String message, int error);
        void deletePersonClicked(Person person);
    }

    public CreateEntryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (CreateEntryFragment.ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(get(R.string.must_implement_interface));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_entry_fragment, container, false);
        parent = container;
        ButterKnife.bind(this, rootView);

        final ArrayList<String> stateArray = new ArrayList<String>(Arrays.asList("", "IL", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
                "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"));
        stateAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stateArray) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.dropdown_row, null);
                }
                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setText(stateArray.get(position));
                return v;
            }
        };
        stateSpinner.setAdapter(stateAdapter);

        if (getArguments().getParcelable("entryToEdit") != null) {
            entryToEdit = getArguments().getParcelable("entryToEdit");
            populateInfo();
            populatePeople();
            createEntryButton.setText(get(R.string.submit_changes));
        } else {
            entryToEdit = new Entry();
            createEntryButton.setText(get(R.string.submit));
        }

        ArrayList<EditText> editTexts = new ArrayList<EditText>(
                Arrays.asList(lastNameEditText, entryPhoneEditText, entryEmailEditText,
                        entryStreetEditText, entryLine2EditText, entryLine3EditText,
                        cityEditText, zipEditText));

        entryPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length()-entryPhoneEditText.getSelectionStart();
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
                        entryPhoneEditText.setText(ans);
                        entryPhoneEditText.setSelection(entryPhoneEditText.getText().length()-cursorComplement);
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = phone.substring(0, 3) + "-" + phone.substring(3);
                        entryPhoneEditText.setText(ans);
                        entryPhoneEditText.setSelection(entryPhoneEditText.getText().length()-cursorComplement);
                    }

                } else {
                    editedFlag = false;
                }
            }
        });

        for (EditText editText : editTexts) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        mCallback.onFocusChange(view);
                    }
                }
            });
        }

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void populateInfo() {
        CustomAddress address = entryToEdit.getAddress();
        lastNameEditText.setText(entryToEdit.getName());
        entryPhoneEditText.setText(entryToEdit.getPhone());
        entryEmailEditText.setText(entryToEdit.getEmail());
        entryStreetEditText.setText(address.getStreet());
        entryLine2EditText.setText(address.getLine2());
        entryLine3EditText.setText(address.getLine3());
        cityEditText.setText(address.getCity());
        stateSpinner.setSelection(stateAdapter.getPosition(entryToEdit.getAddress().getState()));
        zipEditText.setText(address.getZip());
    }

    public void populatePeople() {

        ArrayList<Person> adults = new ArrayList<>();
        ArrayList<Person> children = new ArrayList<>();

        if (entryToEdit.getPeople() != null) {
            for (Person person : entryToEdit.getPeople()) {
                if (!TextUtils.equals(person.getType(), get(R.string.child))) {
                    adults.add(person);
                } else {
                    children.add(person);
                }
            }
        }

        Collections.sort(adults, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return p1.getType().compareTo(p2.getType());
            }
        });

        Collections.sort(children, new Comparator<Person>() {
            @Override public int compare(Person p1, Person p2) {
                return p1.getBirthOrder() - p2.getBirthOrder();
            }
        });

        int adultCount = adultsLinearLayout.getChildCount() - 1;
        for (int i = adultCount; i > 0; i--) {
            adultsLinearLayout.removeViewAt(i);
        }
        for (Person adult : adults) {
            addPersonTextViews(adultsLinearLayout, adult);
            addSeparator(adultsLinearLayout);
        }

        int childCount = childrenLinearLayout.getChildCount() - 1;
        for (int i = childCount; i > 0; i--) {
            childrenLinearLayout.removeViewAt(i);
        }
        for (Person child : children) {
            addPersonTextViews(childrenLinearLayout, child);
            addSeparator(childrenLinearLayout);
        }
    }

    public void addPersonTextViews(LinearLayout ll,final Person person) {

        View personView = LayoutInflater.from(getContext()).inflate(R.layout.person_view, parent, false);

        TextView nameTextView = (TextView) personView.findViewById(R.id.name_text_view);
        TextView phoneTextView = (TextView) personView.findViewById(R.id.phone_text_view);
        TextView emailTextView = (TextView) personView.findViewById(R.id.email_text_view);
        Button deletePersonButton = (Button) personView.findViewById(R.id.delete_person_button);

        if (!TextUtils.equals(person.getType(), get(R.string.child))) {
            nameTextView.setText(person.getName() + ", " + person.getType());
        } else {
            String birthOrderString = get(R.string.st_child);
            if (person.getBirthOrder() == 2) {
                birthOrderString = get(R.string.nd_child);
            } else if (person.getBirthOrder() == 3) {
                birthOrderString = get(R.string.rd_child);
            } else if (person.getBirthOrder() > 3) {
                birthOrderString = get(R.string.th_child);
            }
            nameTextView.setText(person.getName() + ", " + person.getBirthOrder() + birthOrderString);
        }

        if (!StringUtils.isMissing(person.getPhone())) {
            String phoneText = get(R.string.phone_prefix) + person.getPhone();
            phoneTextView.setText(phoneText);
        } else {
            phoneTextView.setVisibility(View.GONE);
        }

        if (!StringUtils.isMissing(person.getEmail())) {
            String emailText = get(R.string.email_prefix) + person.getEmail();
            emailTextView.setText(emailText);
        } else {
            emailTextView.setVisibility(View.GONE);
        }

        deletePersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.deletePersonClicked(person);
            }
        });

        personView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onPersonSelected(person, get(R.string.edit_person_title));
            }
        });

        ll.addView(personView);

    }

    public void addSeparator(LinearLayout ll) {
        View separatorView = new View(getActivity());
        separatorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
        separatorView.setBackgroundColor(getResources().getColor(R.color.lightGray, getActivity().getTheme()));
        ll.addView(separatorView);
    }

    public void updatePeople(Person newPerson) {

        if (!validateNewPerson(newPerson)) {
            return;
        }

        ArrayList<Person> people = new ArrayList<Person>();
        Boolean notFound = true;
        if (entryToEdit.getPeople() != null) {
            people = entryToEdit.getPeople();
            for (Person person : people) {
                if (TextUtils.equals(person.getUid(), newPerson.getUid())) {
                    people.set(people.indexOf(person), newPerson);
                    notFound = false;
                }
            }
        }

        if (notFound) {
            people.add(newPerson);
        }

        entryToEdit.setPeople(people);
        populatePeople();

    }

    private Boolean validateNewPerson(Person newPerson) {

        if (StringUtils.isMissing(newPerson.getName())) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.missing_entry_first_name_title), get(R.string.missing_entry_first_name_message), 0);
            return false;
        }

        if (StringUtils.isMissing(newPerson.getType())) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.missing_type_title), get(R.string.missing_type_messaage), 0);
            return false;
        }

        if (TextUtils.equals(newPerson.getType(), get(R.string.child)) && newPerson.getBirthOrder() == 0) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.missing_birth_order_title), get(R.string.missing_birth_order_message), 0);
            return false;
        }

        if (newPerson.getPhone().length() < 12 && newPerson.getPhone().length() > 0) {
            mCallback.displayInvalidSubmissionAlert(get(R.string.bad_phone_number_title), get(R.string.bad_phone_number_message), 0);
            return false;
        }

        if (!TextUtils.equals(newPerson.getType(), get(R.string.child))) {
            if (getOtherPersonTypes(newPerson).contains(newPerson.getType())) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.duplicate_person_type_title), get(R.string.duplicate_person_type_message) + newPerson.getType() + ".", 0);
                return false;
            }
        }

        if (TextUtils.equals(newPerson.getType(), get(R.string.husband)) || TextUtils.equals(newPerson.getType(), get(R.string.wife))) {
            if (getOtherPersonTypes(newPerson).contains(get(R.string.single))) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.error_title), get(R.string.person_type_conflict_message), 0);
                return false;
            }
        }

        if (TextUtils.equals(newPerson.getType(), get(R.string.single))) {
            if (getOtherPersonTypes(newPerson).contains(get(R.string.husband)) || getOtherPersonTypes(newPerson).contains(get(R.string.wife))) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.error_title), get(R.string.person_type_conflict_message), 0);
                return false;
            }
        }

        if (newPerson.getBirthOrder() != 0) {
            if (getOtherBirthOrders(newPerson).contains(newPerson.getBirthOrder())) {
                mCallback.displayInvalidSubmissionAlert(get(R.string.duplicate_birth_order_title), get(R.string.duplicate_birth_order_message), 0);
                return false;
            }
        }

        return true;

    }

    private ArrayList<String> getOtherPersonTypes(Person newPerson) {
        ArrayList<String> personTypes = new ArrayList<>();
        if (entryToEdit.getPeople() == null) {
            return personTypes;
        } else {
            for (Person person : entryToEdit.getPeople()) {
                if (!TextUtils.equals(person.getUid(), newPerson.getUid())) {
                    personTypes.add(person.getType());
                }
            }
            return personTypes;
        }
    }

    private ArrayList<Integer> getOtherBirthOrders(Person newPerson) {
        ArrayList<Integer> birthOrders = new ArrayList<>();
        if (entryToEdit.getPeople() == null) {
            return birthOrders;
        } else {
            for (Person person : entryToEdit.getPeople()) {
                if (!TextUtils.equals(person.getUid(), newPerson.getUid())) {
                    birthOrders.add(person.getBirthOrder());
                }
            }
            return birthOrders;
        }
    }

    private ArrayList<Person> getAdults(ArrayList<Person> people) {
        ArrayList<Person> adults = new ArrayList<>();
        if (people != null) {
            for (Person person : people) {
                if (!TextUtils.equals(person.getType(), get(R.string.child))) {
                    adults.add(person);
                }
            }
        }
        return adults;
    }

    private ArrayList<Person> getChildren(ArrayList<Person> people) {
        ArrayList<Person> children = new ArrayList<>();
        if (people != null) {
            for (Person person : people) {
                if (TextUtils.equals(person.getType(), get(R.string.child))) {
                    children.add(person);
                }
            }
        }
        return children;
    }

    public void deletePerson(Person personToDelete) {

        ArrayList<Person> people = entryToEdit.getPeople();
        int indexToRemove = -1;
        for (Person person : people) {
            if (TextUtils.equals(person.getUid(),personToDelete.getUid())) {
                indexToRemove = people.indexOf(person);
            }
        }
        if (indexToRemove > -1) {
            people.remove(indexToRemove);
        }

        entryToEdit.setPeople(people);
        populatePeople();
    }

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}
