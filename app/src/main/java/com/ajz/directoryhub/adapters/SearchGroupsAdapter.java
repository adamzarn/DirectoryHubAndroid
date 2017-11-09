package com.ajz.directoryhub.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adamzarn on 10/26/17.
 */

public class SearchGroupsAdapter extends RecyclerView.Adapter<SearchGroupsAdapter.ViewHolder> {

    private ArrayList<Group> groups = new ArrayList<>();
    private FirebaseStorage mStorage;

    private OnGroupClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.group_logo)
        CircleImageView groupLogoImageView;

        @BindView(R.id.group_name_text_view)
        TextView groupNameTextView;

        @BindView(R.id.city_state_text_view)
        TextView cityStateTextView;

        @BindView(R.id.created_by_text_view)
        TextView createdByTextView;

        @BindView(R.id.delete_group_button)
        Button deleteGroupButton;

        @BindView(R.id.edit_group_button)
        Button editGroupButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mStorage = FirebaseStorage.getInstance();
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onGroupClick(groups.get(getAdapterPosition()));
        }

    }

    @Override
    public SearchGroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameTextView.setText(group.getName());
        holder.cityStateTextView.setText(group.getCity() + ", " + group.getState());
        String createdByString = holder.createdByTextView.getContext().getResources().getString(R.string.created_by_prefix) + group.getCreatedBy();
        holder.createdByTextView.setText(createdByString);
        holder.deleteGroupButton.setVisibility(View.GONE);
        holder.editGroupButton.setVisibility(View.GONE);

        holder.groupLogoImageView.setImageBitmap(null);
        mStorage.getReference().child(group.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(holder.groupLogoImageView.getContext()).load(uri.toString()).into(holder.groupLogoImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.groupLogoImageView.setImageDrawable(ResourcesCompat.getDrawable(holder.groupLogoImageView.getContext().getResources(), R.drawable.image_thumbnail, null));
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public interface OnGroupClickListener {
        void onGroupClick(Group selectedGroup);
    }

    public void setOnGroupClickListener(final OnGroupClickListener groupClickListener) {
        this.clickListener = groupClickListener;
    }

    public void deliverGroup(Group group) {
        groups.clear();
        groups.add(group);
        notifyDataSetChanged();
    }

    public void deliverGroups(ArrayList<Group> receivedGroups) {
        groups.clear();
        groups = receivedGroups;
        notifyDataSetChanged();
    }

    public void clearData() {
        groups.clear();
        notifyDataSetChanged();
    }

}
