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

public class LoginFragment extends Fragment {

    @BindView(R.id.email_edit_text)
    EditText emailEditText;

    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @BindView(R.id.login_button)
    Button loginButton;

    @OnClick(R.id.login_button)
    public void loginButtonClicked() {
        loginProgressBar.setVisibility(View.VISIBLE);
        mCallback.onButtonClick(loginButton, emailEditText.getText().toString(), passwordEditText.getText().toString(), loginProgressBar);
    }

    @BindView(R.id.forgot_password_button)
    Button forgotPasswordButton;

    @OnClick(R.id.forgot_password_button)
    public void forgotPasswordButtonClicked() {
        mCallback.onButtonClick(forgotPasswordButton, emailEditText.getText().toString(), null, null);
    }

    @BindView(R.id.login_progress_bar)
    ProgressBar loginProgressBar;

    @BindView(R.id.create_account_button)
    Button createAccountButton;

    @OnClick(R.id.create_account_button)
    public void createAccountButtonClicked() {
        mCallback.onButtonClick(createAccountButton, null, null, null);
    }

    public LoginFragment() {
    }

    ClickListener mCallback;

    public interface ClickListener {
        void onButtonClick(Button b, String email, String password, ProgressBar pb);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, rootView);
        loginProgressBar.setVisibility(View.INVISIBLE);
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
