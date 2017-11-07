package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ajz.directoryhub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adamzarn on 10/20/17.
 */

public class CreateAccountFragment extends Fragment {

    @BindView(R.id.first_name_edit_text)
    EditText firstNameEditText;

    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;

    @BindView(R.id.create_account_email_edit_text)
    EditText createAccountEmailEditText;

    @BindView(R.id.create_account_password_edit_text)
    EditText createAccountPasswordEditText;

    @BindView(R.id.verify_password_edit_text)
    EditText verifyPasswordEditText;

    @BindView(R.id.create_account_progress_bar)
    ProgressBar createAccountProgressBar;

    @BindView(R.id.submit_create_account_button)
    Button submitCreateAccountButton;

    @OnClick(R.id.submit_create_account_button)
    public void createAccount() {
        createAccountProgressBar.setVisibility(View.VISIBLE);
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = createAccountEmailEditText.getText().toString();
        String password = createAccountPasswordEditText.getText().toString();
        String passwordVerification = verifyPasswordEditText.getText().toString();
        mCallback.onButtonClick(firstName, lastName, email, password, passwordVerification, createAccountProgressBar);
    }

    ClickListener mCallback;

    public interface ClickListener {
        void onButtonClick(String firstName, String lastName, String email, String password, String passwordVerification, ProgressBar pb);
    }

    public CreateAccountFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(get(R.string.must_implement_interface));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_account_fragment, container, false);
        ButterKnife.bind(this, rootView);
        createAccountProgressBar.setVisibility(View.GONE);
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

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}
