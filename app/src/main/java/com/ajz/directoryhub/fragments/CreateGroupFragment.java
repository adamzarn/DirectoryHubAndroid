package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

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
    private Group groupToEdit;
    private FirebaseStorage mStorage;
    private byte[] currentImageData = new byte[0];
    private Boolean imageDownloaded = false;

    @BindView(R.id.group_uid_relative_layout)
    RelativeLayout groupUidRelativeLayout;

    @BindView(R.id.group_uid_text_view)
    TextView groupUidTextView;

    @BindView(R.id.share_button)
    Button shareButton;

    @OnClick(R.id.share_button)
    public void shareButtonClicked() {
        mCallback.shareGroup(groupToEdit);
    }

    @BindView(R.id.group_name_edit_text)
    EditText groupNameEditText;

    @BindView(R.id.city_edit_text)
    EditText cityEditText;

    @BindView(R.id.state_spinner)
    Spinner stateSpinner;

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
        mCallback.manageAdministratorsButtonClicked(groupToEdit);
    }

    @BindView(R.id.delete_group_button)
    Button deleteGroupButton;

    @OnClick(R.id.delete_group_button)
    public void deleteGroup() {
        mCallback.deleteGroup(groupToEdit);
    }

    @BindView(R.id.submit_create_group_button)
    Button submitCreateGroupButton;

    @OnClick(R.id.submit_create_group_button)
    public void submitCreateGroup() {
        submitCreateGroupProgressBar.bringToFront();
        submitCreateGroupProgressBar.setVisibility(View.VISIBLE);
        String groupUid = null;
        if (groupToEdit != null) {
            groupUid = groupToEdit.getUid();
        }
        String groupName = groupNameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateSpinner.getSelectedItem().toString();
        String password = passwordEditText.getText().toString();
        mCallback.onButtonClick(groupUid, groupUids, groupName, city, state, password, currentImageData, submitCreateGroupProgressBar);
    }

    @BindView(R.id.submit_create_group_progress_bar)
    ProgressBar submitCreateGroupProgressBar;

    ClickListener mCallback;

    public interface ClickListener {
        void onButtonClick(String groupUid, ArrayList<String> groupUids, String groupName, String city, String state, String password, byte[] groupLogo, ProgressBar pb);
        void onFocusChange(View view);
        void pickImage();
        void manageAdministratorsButtonClicked(Group groupBeingEdited);
        void deleteGroup(Group groupToDelete);
        void shareGroup(Group groupToShare);
    }

    public CreateGroupFragment() {
        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (CreateGroupFragment.ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(get(R.string.must_implement_interface));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_group_fragment, container, false);
        ButterKnife.bind(this, rootView);

        final ArrayList<String> stateArray = new ArrayList<String>(Arrays.asList("", "IL", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
                "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"));
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, stateArray) {
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

        if (getArguments().getStringArrayList("groupUids") != null) {
            groupUids = getArguments().getStringArrayList("groupUids");
            submitCreateGroupButton.setText(get(R.string.submit));
            manageAdministratorsButton.setVisibility(View.GONE);
            deleteGroupButton.setVisibility(View.GONE);
        }

        if (getArguments().getParcelable("groupToEdit") != null) {
            groupToEdit = getArguments().getParcelable("groupToEdit");

            groupUidRelativeLayout.setVisibility(View.VISIBLE);
            String groupUidText = get(R.string.unique_id_label) + groupToEdit.getUid();
            groupUidTextView.setText(groupUidText);
            groupNameEditText.setText(groupToEdit.getName());
            cityEditText.setText(groupToEdit.getCity());
            stateSpinner.setSelection(stateAdapter.getPosition(groupToEdit.getState()));
            passwordEditText.setText(groupToEdit.getPassword());

            if (currentImageData.length == 0 && !imageDownloaded) {
                final long ONE_MEGABYTE = 1024 * 1024;
                mStorage.getReference().child(groupToEdit.getUid() + ".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        currentImageData = bytes;
                        setImage(currentImageData);
                        imageDownloaded = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        createGroupLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_thumbnail, null));
                        currentImageData = new byte[0];
                    }
                });
            } else {
                if (currentImageData.length == 0) {
                    createGroupLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_thumbnail, null));
                } else {
                    setImage(currentImageData);
                }
            }

            manageAdministratorsButton.setVisibility(View.VISIBLE);
            submitCreateGroupButton.setText(get(R.string.submit_changes));

        } else {
            createGroupLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_thumbnail, null));
        }

        ArrayList<EditText> editTexts = new ArrayList<EditText>(
                Arrays.asList(groupNameEditText, cityEditText, passwordEditText));

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

    public void setImage(byte[] bytes) {
        Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        currentImageData = bytes;
        createGroupLogo.setImageBitmap(bm);
    }

    public void setGroupToEdit(Group newGroupToEdit) {
        groupToEdit = newGroupToEdit;
    }

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}
