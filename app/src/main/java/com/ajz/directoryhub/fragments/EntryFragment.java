package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adamzarn on 10/24/17.
 */

public class EntryFragment extends Fragment {

    private Entry currentEntry;
    private Bundle args;

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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEditEntryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.entry_fragment, container, false);
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
        reloadData();
    }

    public void reloadData() {
        entryProgressBar.setVisibility(View.VISIBLE);
        new FirebaseClient().getEntry(this, args.getString("groupUid"), args.getString("entryUid"), entryScrollView, entryProgressBar);
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(selectedEntry.getTitle());

        if (!TextUtils.equals(selectedEntry.getPhone(),"")) {
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
        if (!TextUtils.equals(selectedEntry.getEmail(),"")) {
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
        if (!TextUtils.equals(selectedEntry.getAddress().getLine2(),"") ||
            !TextUtils.equals(selectedEntry.getAddress().getLine3(),"") ||
            !TextUtils.equals(selectedEntry.getAddress().getStreet(),"") ||
            !TextUtils.equals(selectedEntry.getAddress().getCityStateZip(),"")) {

            addressLinearLayout.setVisibility(View.VISIBLE);
            addressLinearLayout.setVisibility(View.VISIBLE);

            clickableAddressLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),selectedEntry.getAddress().getStreet() + "\n" + selectedEntry.getAddress().getCityStateZip(),Toast.LENGTH_LONG).show();
                }
            });

            if (!TextUtils.equals(selectedEntry.getAddress().getLine2(),"")) {
                line2TextView.setVisibility(View.VISIBLE);
                line2TextView.setText(selectedEntry.getAddress().getLine2());
            } else {
                line2TextView.setVisibility(View.GONE);
            }
            if (!TextUtils.equals(selectedEntry.getAddress().getLine3(),"")) {
                line3TextView.setVisibility(View.VISIBLE);
                line3TextView.setText(selectedEntry.getAddress().getLine3());
            } else {
                line3TextView.setVisibility(View.GONE);
            }
            if (!TextUtils.equals(selectedEntry.getAddress().getStreet(),"")) {
                streetTextView.setVisibility(View.VISIBLE);
                streetTextView.setText(selectedEntry.getAddress().getStreet());
            } else {
                streetTextView.setVisibility(View.GONE);
            }
            if (!TextUtils.equals(selectedEntry.getAddress().getCityStateZip(),"")) {
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

        int adultCount = contactInfoLinearLayout.getChildCount() - 1;
        for (int i = adultCount; i > 0; i--) {
            contactInfoLinearLayout.removeViewAt(i);
        }
        for (Person adult : adults) {
            addPersonTextViews(contactInfoLinearLayout, adult);

            View separatorView = new View(getActivity());
            separatorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
            separatorView.setBackgroundColor(getResources().getColor(R.color.lightGray, getActivity().getTheme()));
            contactInfoLinearLayout.addView(separatorView);

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

                View childrenSeparatorView = new View(getActivity());
                childrenSeparatorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
                childrenSeparatorView.setBackgroundColor(getResources().getColor(R.color.lightGray, getActivity().getTheme()));
                childrenLinearLayout.addView(childrenSeparatorView);

            }
        }

    }

    public void addPersonTextViews(LinearLayout ll,final Person person) {

        LinearLayout clickablePersonLinearLayout = new LinearLayout(getActivity());
        clickablePersonLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView nameTextView = new TextView(getActivity());
        formatTextView(nameTextView);
        nameTextView.setText(person.getName());
        clickablePersonLinearLayout.addView(nameTextView);

        if (!TextUtils.equals(person.getPhone(),"")) {
            TextView phoneTextView = new TextView(getActivity());
            formatTextView(phoneTextView);
            phoneTextView.setText(person.getPhone());
            clickablePersonLinearLayout.addView(phoneTextView);
        }

        if (!TextUtils.equals(person.getEmail(),"")) {
            TextView emailTextView = new TextView(getActivity());
            formatTextView(emailTextView);
            emailTextView.setText(person.getEmail());
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
                Toast.makeText(getActivity(),person.getName() + "\n" + person.getPhone() + "\n" + person.getEmail(),Toast.LENGTH_LONG).show();
            }
        });

        ll.addView(clickablePersonLinearLayout);

    }

    public void formatTextView(TextView tv) {

        final float scale = getResources().getDisplayMetrics().scaledDensity;
        int textSize = (int) (getResources().getDimensionPixelSize(R.dimen.font_sm) / scale);
        tv.setTextSize(textSize);

    }

}
