package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.offset;
import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 10/24/17.
 */

public class EntryFragment extends Fragment {

    private Entry currentEntry;
    private Bundle args;
    private ViewGroup parent;

    @BindView(R.id.entry_scroll_view)
    ScrollView entryScrollView;

    ///PHONE///

    @BindView(R.id.entry_phone_linear_layout)
    LinearLayout phoneLinearLayout;

    @BindView(R.id.clickable_phone_linear_layout)
    LinearLayout clickablePhoneLinearLayout;

    @BindView(R.id.entry_phone_header_text_view)
    TextView phoneHeaderTextView;

    @BindView(R.id.entry_phone_text_view)
    TextView phoneTextView;

    ///EMAIL///

    @BindView(R.id.entry_email_linear_layout)
    LinearLayout emailLinearLayout;

    @BindView(R.id.clickable_email_linear_layout)
    LinearLayout clickableEmailLinearLayout;

    @BindView(R.id.entry_email_header_text_view)
    TextView emailHeaderTextView;

    @BindView(R.id.entry_email_text_view)
    TextView emailTextView;

    ///ADDRESS///

    @BindView(R.id.entry_address_linear_layout)
    LinearLayout addressLinearLayout;

    @BindView(R.id.clickable_address_linear_layout)
    LinearLayout clickableAddressLinearLayout;

    @BindView(R.id.entry_address_header_text_view)
    TextView addressHeaderTextView;

    @BindView(R.id.entry_line2_text_view)
    TextView line2TextView;

    @BindView(R.id.entry_line3_text_view)
    TextView line3TextView;

    @BindView(R.id.entry_street_text_view)
    TextView streetTextView;

    @BindView(R.id.entry_city_state_zip_text_view)
    TextView cityStateZipTextView;

    ///CONTACT INFO///

    @BindView(R.id.entry_contact_info_linear_layout)
    LinearLayout contactInfoLinearLayout;

    @BindView(R.id.entry_contact_info_header_text_view)
    TextView contactInfoHeaderTextView;

    ///CHILDREN///

    @BindView(R.id.entry_children_linear_layout)
    LinearLayout childrenLinearLayout;

    @BindView(R.id.entry_children_header_text_view)
    TextView childrenHeaderTextView;

    ///OTHER ELEMENTS///

    @BindView(R.id.entry_progress_bar)
    ProgressBar entryProgressBar;

    @BindView(R.id.edit_entry_fab)
    FloatingActionButton editEntryFab;

    @OnClick(R.id.edit_entry_fab)
    public void editEntry() {
        mCallback.onEditEntry(currentEntry);
    }

    public EntryFragment() {
    }

    OnEditEntryClickListener mCallback;

    public interface OnEditEntryClickListener {
        void onEditEntry(Entry entryToEdit);
        void presentContactOptions(Person person, String lastName);
        void noInternet();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEditEntryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(get(R.string.must_implement_interface));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.entry_fragment, container, false);
        parent = container;
        ButterKnife.bind(this, rootView);

        args = getArguments();

        if (!getArguments().getBoolean("isAdmin")) {
            editEntryFab.setVisibility(View.GONE);
        }

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        entryScrollView.setVisibility(View.INVISIBLE);
        loadData();
    }

    public void loadData() {
        entryProgressBar.setVisibility(View.VISIBLE);
        if (isConnected()) {
            new FirebaseClient().getEntry(this, args.getString("groupUid"), args.getString("entryUid"), entryScrollView, entryProgressBar);
        } else {
            mCallback.noInternet();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void populateFragment(final Entry selectedEntry) {

        currentEntry = selectedEntry;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(selectedEntry.getTitle());
        }

        if (!StringUtils.isMissing(selectedEntry.getPhone())) {
            phoneTextView.setText(selectedEntry.getPhone());
            phoneLinearLayout.setVisibility(View.VISIBLE);
            clickablePhoneLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + selectedEntry.getPhone()));
                    startActivity(intent);
                }
            });
        } else {
            phoneLinearLayout.setVisibility(View.GONE);
        }
        if (!StringUtils.isMissing(selectedEntry.getEmail())) {
            emailTextView.setText(selectedEntry.getEmail());
            emailLinearLayout.setVisibility(View.VISIBLE);
            clickableEmailLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + selectedEntry.getEmail()));
                    startActivity(intent);
                }
            });
        } else {
            emailLinearLayout.setVisibility(View.GONE);
        }
        if (!StringUtils.isMissing(selectedEntry.getAddress().getLine2()) ||
                !StringUtils.isMissing(selectedEntry.getAddress().getLine3()) ||
                !StringUtils.isMissing(selectedEntry.getAddress().getStreet()) ||
                !StringUtils.isMissing(selectedEntry.getAddress().getCityStateZip())) {

            addressLinearLayout.setVisibility(View.VISIBLE);
            addressLinearLayout.setVisibility(View.VISIBLE);

            clickableAddressLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String addressString = selectedEntry.getAddress().getStreet() + ", " + selectedEntry.getAddress().getCityStateZip();
                    addressString.replace(" ", "+");
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=&daddr="+addressString;
                    Uri gmmIntentUri = Uri.parse(uri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

            if (!StringUtils.isMissing(selectedEntry.getAddress().getLine2())) {
                line2TextView.setVisibility(View.VISIBLE);
                line2TextView.setText(selectedEntry.getAddress().getLine2());
            } else {
                line2TextView.setVisibility(View.GONE);
            }
            if (!StringUtils.isMissing(selectedEntry.getAddress().getLine3())) {
                line3TextView.setVisibility(View.VISIBLE);
                line3TextView.setText(selectedEntry.getAddress().getLine3());
            } else {
                line3TextView.setVisibility(View.GONE);
            }
            if (!StringUtils.isMissing(selectedEntry.getAddress().getStreet())) {
                streetTextView.setVisibility(View.VISIBLE);
                streetTextView.setText(selectedEntry.getAddress().getStreet());
            } else {
                streetTextView.setVisibility(View.GONE);
            }
            if (!StringUtils.isMissing(selectedEntry.getAddress().getCityStateZip())) {
                cityStateZipTextView.setVisibility(View.VISIBLE);
                cityStateZipTextView.setText(selectedEntry.getAddress().getCityStateZip());
            } else {
                cityStateZipTextView.setVisibility(View.GONE);
            }

        } else {
            addressLinearLayout.setVisibility(View.GONE);
        }

        ArrayList<Person> adults = new ArrayList<Person>();
        ArrayList<Person> children = new ArrayList<Person>();

        for (Person person : selectedEntry.getPeople()) {
            if (TextUtils.equals(person.getType(),"Child")) {
                children.add(person);
            } else {
                adults.add(person);
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

        int adultCount = contactInfoLinearLayout.getChildCount() - 1;
        for (int i = adultCount; i > 0; i--) {
            contactInfoLinearLayout.removeViewAt(i);
        }
        for (Person adult : adults) {
            addPersonTextViews(contactInfoLinearLayout, adult);
            addSeparator(contactInfoLinearLayout);
        }

        int childCount = childrenLinearLayout.getChildCount() - 1;
        for (int i = childCount; i > 0; i--) {
            childrenLinearLayout.removeViewAt(i);
        }
        if (children.size() == 0) {
            childrenHeaderTextView.setVisibility(View.GONE);
        } else {
            childrenHeaderTextView.setVisibility(View.VISIBLE);
            for (Person child : children) {
                addPersonTextViews(childrenLinearLayout, child);
                addSeparator(childrenLinearLayout);
            }
        }

    }

    public void addPersonTextViews(LinearLayout ll,final Person person) {

        View personView = LayoutInflater.from(getContext()).inflate(R.layout.person_view, parent, false);

        TextView nameTextView = (TextView) personView.findViewById(R.id.name_text_view);
        TextView phoneTextView = (TextView) personView.findViewById(R.id.phone_text_view);
        TextView emailTextView = (TextView) personView.findViewById(R.id.email_text_view);
        LinearLayout deleteButtonLinearLayout = (LinearLayout) personView.findViewById(R.id.delete_button_linear_layout);
        deleteButtonLinearLayout.setVisibility(View.GONE);

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

        personView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.presentContactOptions(person, currentEntry.getName());
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

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}
