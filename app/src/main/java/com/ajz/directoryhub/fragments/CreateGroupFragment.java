package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ajz.directoryhub.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adamzarn on 10/27/17.
 */

public class CreateGroupFragment extends Fragment {

    private ArrayList<String> groupUids;
    private byte[] currentImageData;

    @BindView(R.id.group_uid_text_view)
    TextView groupUidTextView;

    @BindView(R.id.group_name_edit_text)
    EditText groupNameEditText;

    @BindView(R.id.city_edit_text)
    EditText cityEditText;

    @BindView(R.id.state_edit_text)
    EditText stateEditText;

    @BindView(R.id.create_group_password_edit_text)
    EditText passwordEditText;

    @BindView(R.id.create_group_logo)
    CircleImageView createGroupLogo;

    @BindView(R.id.upload_photo_button)
    Button uploadPhotoButton;

    @OnClick(R.id.upload_photo_button)
    public void uploadPhoto() {
        mCallback.pickImage();
    }

    @BindView(R.id.remove_photo_button)
    Button removePhotoButton;

    @OnClick(R.id.remove_photo_button)
    public void removePhoto() {
        currentImageData = new byte[0];
        createGroupLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_thumbnail, null));
    }

    @BindView(R.id.manage_administrators_button)
    Button manageAdministratorsButton;

    @OnClick(R.id.manage_administrators_button)
    public void manageAdministrators() {

    }

    @BindView(R.id.submit_create_group_button)
    Button submitCreateGroupButton;

    @OnClick(R.id.submit_create_group_button)
    public void submitCreateGroup() {
        System.out.println("Submit Button Pressed");
        submitCreateGroupProgressBar.bringToFront();
        submitCreateGroupProgressBar.setVisibility(View.VISIBLE);
        String groupName = groupNameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        mCallback.onButtonClick(groupUids, groupName, city, state, password, currentImageData, submitCreateGroupProgressBar);
    }

    @BindView(R.id.submit_create_group_progress_bar)
    ProgressBar submitCreateGroupProgressBar;

    ClickListener mCallback;

    public interface ClickListener {
        void onButtonClick(ArrayList<String> groupUids, String groupName, String city, String state, String password, byte[] groupLogo, ProgressBar pb);
        void onFocusChange(View view);
        void pickImage();
    }

    public CreateGroupFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (CreateGroupFragment.ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_group_fragment, container, false);
        ButterKnife.bind(this, rootView);

        currentImageData = new byte[0];
        groupUids = getArguments().getStringArrayList("groupUids");

        ArrayList<EditText> editTexts = new ArrayList<EditText>(
                Arrays.asList(groupNameEditText, cityEditText, stateEditText, passwordEditText));

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

    public void setImage(byte[] bytes) {
        Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        currentImageData = bytes;
        createGroupLogo.setImageBitmap(bm);
    }

}
