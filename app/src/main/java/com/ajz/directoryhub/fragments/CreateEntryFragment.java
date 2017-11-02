package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import static android.R.id.message;

/**
 * Created by adamzarn on 11/1/17.
 */

public class CreateEntryFragment extends Fragment {

    ClickListener mCallback;
    private Entry entryToEdit;
    private ArrayList<Person> newPeople;

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

    @BindView(R.id.state_edit_text)
    EditText stateEditText;

    @BindView(R.id.zip_edit_text)
    EditText zipEditText;

    @BindView(R.id.add_person_button)
    Button addPersonButton;

    @OnClick(R.id.add_person_button)
    public void addPersonButtonClicked() {
        mCallback.onPersonSelected(null, "Add Person");
    }

    @BindView(R.id.submit_create_entry_button)
    Button createEntryButton;

    @BindView(R.id.submit_create_entry_progress_bar)
    ProgressBar submitCreateEntryProgressBar;

    @OnClick(R.id.submit_create_entry_button)
    public void submitButtonClicked() {

        submitCreateEntryProgressBar.bringToFront();
        submitCreateEntryProgressBar.setVisibility(View.VISIBLE);

        String lastName = lastNameEditText.getText().toString();
        String phone = entryPhoneEditText.getText().toString();
        String email = entryEmailEditText.getText().toString();
        String street = entryStreetEditText.getText().toString();
        String line2 = entryLine2EditText.getText().toString();
        String line3 = entryLine3EditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String zip = zipEditText.getText().toString();
        CustomAddress address = new CustomAddress(street, line2, line3, city, state, zip);

        if (entryToEdit != null) {
            entryToEdit.setName(lastName);
            entryToEdit.setPhone(phone);
            entryToEdit.setEmail(email);
            entryToEdit.setAddress(address);
            entryToEdit.setPeople(newPeople);
            mCallback.submitButtonClicked(entryToEdit);
        } else {
            Entry newEntry = new Entry(null, lastName, phone, email, address, newPeople);
            mCallback.submitButtonClicked(newEntry);
        }
    }

    public interface ClickListener {
        void onFocusChange(View view);
        void onPersonSelected(Person selectedPerson, String title);
        void submitButtonClicked(Entry entry);
        void displayInvalidPersonAlert(String title, String message);
    }

    public CreateEntryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (CreateEntryFragment.ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_entry_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (getArguments().getParcelable("entryToEdit") != null) {
            entryToEdit = getArguments().getParcelable("entryToEdit");
            newPeople = entryToEdit.getPeople();
            populateInfo();
            populatePeople();
            createEntryButton.setText("SUBMIT CHANGES");
        } else {
            createEntryButton.setText("SUBMIT");
        }

        ArrayList<EditText> editTexts = new ArrayList<EditText>(
                Arrays.asList(lastNameEditText, entryPhoneEditText, entryEmailEditText,
                        entryStreetEditText, entryLine2EditText, entryLine3EditText,
                        cityEditText, stateEditText, zipEditText));

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
        stateEditText.setText(address.getState());
        zipEditText.setText(address.getZip());
    }

    public void populatePeople() {

        ArrayList<Person> adults = new ArrayList<>();
        ArrayList<Person> children = new ArrayList<>();

        for (Person person : entryToEdit.getPeople()) {
            if (!TextUtils.equals(person.getType(), "Child")) {
                adults.add(person);
            } else {
                children.add(person);
            }
        }

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
            if (adults.indexOf(adult) < adults.size() - 1) {
                addSeparator(adultsLinearLayout);
            }
        }

        int childCount = childrenLinearLayout.getChildCount() - 1;
        for (int i = childCount; i > 0; i--) {
            childrenLinearLayout.removeViewAt(i);
        }
        for (Person child : children) {
            addPersonTextViews(childrenLinearLayout, child);
            if (children.indexOf(child) < children.size() - 1) {
                addSeparator(childrenLinearLayout);
            }
        }
    }

    public void addPersonTextViews(LinearLayout ll,final Person person) {

        LinearLayout clickablePersonLinearLayout = new LinearLayout(getActivity());
        clickablePersonLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView nameTextView = new TextView(getActivity());
        formatTextView(nameTextView);
        nameTextView.setText(person.getName() + ", " + person.getType());
        clickablePersonLinearLayout.addView(nameTextView);

        if (!TextUtils.equals(person.getPhone(),"")) {
            TextView phoneTextView = new TextView(getActivity());
            formatTextView(phoneTextView);
            phoneTextView.setText("Phone: " + person.getPhone());
            clickablePersonLinearLayout.addView(phoneTextView);
        }

        if (!TextUtils.equals(person.getEmail(),"")) {
            TextView emailTextView = new TextView(getActivity());
            formatTextView(emailTextView);
            emailTextView.setText("Email: " + person.getEmail());
            clickablePersonLinearLayout.addView(emailTextView);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int lr = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
        int tb = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        lp.setMargins(lr, tb, lr, tb);
        clickablePersonLinearLayout.setLayoutParams(lp);

        clickablePersonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onPersonSelected(person, "Edit Person");
            }
        });

        ll.addView(clickablePersonLinearLayout);

    }

    public void formatTextView(TextView tv) {

        final float scale = getResources().getDisplayMetrics().scaledDensity;
        int textSize = (int) (getResources().getDimensionPixelSize(R.dimen.font_sm) / scale);
        tv.setTextSize(textSize);

    }

    public void addSeparator(LinearLayout ll) {
        View separatorView = new View(getActivity());
        separatorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
        separatorView.setBackgroundColor(getResources().getColor(R.color.lightGray, getActivity().getTheme()));
        ll.addView(separatorView);
    }

    public void updatePeople(Person newPerson) {

        System.out.println("Here");

        if (!validateNewPerson(newPerson)) {
            System.out.println("Not Validated");
            return;
        } else {
            System.out.println("Validated");
        }

        ArrayList<Person> people = entryToEdit.getPeople();
        Boolean notFound = true;
        for (Person person : people) {
            if (TextUtils.equals(person.getUid(),newPerson.getUid())) {
                people.set(people.indexOf(person), newPerson);
                notFound = false;
            }
        }
        if (notFound) {
            people.add(newPerson);
        }
        newPeople = people;
        populatePeople();
    }

    private Boolean validateNewPerson(Person newPerson) {

        if (StringUtils.isMissing(newPerson.getName())) {
            mCallback.displayInvalidPersonAlert("No Name", "You must enter a first name.");
            return false;
        }

        if (StringUtils.isMissing(newPerson.getType())) {
            mCallback.displayInvalidPersonAlert("No Type", "Each person must have a type.");
            return false;
        }

        if (newPerson.getPhone().length() < 12 && newPerson.getPhone().length() > 0) {
            mCallback.displayInvalidPersonAlert("Bad Phone Number", "Phone Number must be 12 characters long.");
            return false;
        }

        if (TextUtils.equals(newPerson.getType(),"Child") && newPerson.getBirthOrder() == 0) {
            mCallback.displayInvalidPersonAlert("Missing Birth Order", "Children must have a birth order.");
            return false;
        }

        if (!TextUtils.equals(newPerson.getType(),"Child")) {
            if (getOtherPersonTypes(newPerson).contains(newPerson.getType())) {
                mCallback.displayInvalidPersonAlert("Duplicate Person Type", "Entries can only contain one " + newPerson.getType() + ".");
                return false;
            }
        }

        if (TextUtils.equals(newPerson.getType(), "Husband") || TextUtils.equals(newPerson.getType(), "Wife")) {
            if (getOtherPersonTypes(newPerson).contains("Single")) {
                mCallback.displayInvalidPersonAlert("Error", "Married couples and adult Singles cannot be in the same entry.");
                return false;
            }
        }

        if (TextUtils.equals(newPerson.getType(),"Single")) {
            if (getOtherPersonTypes(newPerson).contains("Husband") || getOtherPersonTypes(newPerson).contains("Wife")) {
                mCallback.displayInvalidPersonAlert("Error", "Married couples and adult Singles cannot be in the same entry.");
                return false;
            }
        }

        if (newPerson.getBirthOrder() != 0) {
            if (getOtherBirthOrders(newPerson).contains(newPerson.getBirthOrder())) {
                mCallback.displayInvalidPersonAlert("Bad Birth Order", "Birth Order must be unique.");
                return false;
            }
        }

        return true;

    }

    private ArrayList<String> getOtherPersonTypes(Person newPerson) {
        ArrayList<String> personTypes = new ArrayList<>();
        if (newPeople == null) {
            return personTypes;
        } else {
            for (Person person : newPeople) {
                if (!TextUtils.equals(person.getUid(), newPerson.getUid())) {
                    personTypes.add(person.getType());
                }
            }
            return personTypes;
        }
    }

    private ArrayList<Integer> getOtherBirthOrders(Person newPerson) {
        ArrayList<Integer> birthOrders = new ArrayList<>();
        if (newPeople == null) {
            return birthOrders;
        } else {
            for (Person person : newPeople) {
                if (!TextUtils.equals(person.getUid(), newPerson.getUid())) {
                    birthOrders.add(person.getBirthOrder());
                }
            }
            return birthOrders;
        }
    }


}
