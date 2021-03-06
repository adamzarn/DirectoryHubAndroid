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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adamzarn on 10/21/17.
 */

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    private Boolean searching;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Group> filteredGroups = new ArrayList<>();

    @Override
    public MyGroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Group group;
        if (searching) {
            group = filteredGroups.get(position);
        } else {
            group = groups.get(position);
        }
        if (!group.getAdminKeys().contains(mAuth.getCurrentUser().getUid())) {
            holder.editGroupButton.setVisibility(View.GONE);
        } else {
            holder.editGroupButton.setVisibility(View.VISIBLE);
        }
        holder.groupNameTextView.setText(group.getName());
        holder.cityStateTextView.setText(group.getCity() + ", " + group.getState());
        holder.createdByTextView.getContext().getResources().getString(R.string.created_by_prefix);
        String createdByString = holder.createdByTextView.getContext().getResources().getString(R.string.created_by_prefix) + group.getCreatedBy();
        holder.createdByTextView.setText(createdByString);

        holder.deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searching) {
                    clickListener.onDeleteGroupClick(filteredGroups.get(position));
                } else {
                    clickListener.onDeleteGroupClick(groups.get(position));
                }
            }
        });

        holder.editGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searching) {
                    clickListener.onEditGroupClick(filteredGroups.get(position));
                } else {
                    clickListener.onEditGroupClick(groups.get(position));
                }
            }
        });

        holder.groupLogoImageView.setImageBitmap(null);
        mStorage.getReference().child(group.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("Success");
                Picasso.with(holder.groupLogoImageView.getContext()).load(uri.toString()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.groupLogoImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Failure");
                holder.groupLogoImageView.setImageDrawable(ResourcesCompat.getDrawable(holder.groupLogoImageView.getContext().getResources(), R.drawable.image_thumbnail, null));
            }
        });

    }

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
            mAuth = FirebaseAuth.getInstance();
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (searching) {
                clickListener.onGroupClick(filteredGroups.get(getAdapterPosition()));
            } else {
                clickListener.onGroupClick(groups.get(getAdapterPosition()));
            }
        }
    }

    private OnGroupClickListener clickListener;

    public interface OnGroupClickListener {
        void onGroupClick(Group selectedGroup);
        void onEditGroupClick(Group groupToEdit);
        void onDeleteGroupClick(Group groupToDelete);
    }

    public void setOnGroupClickListener(final OnGroupClickListener groupClickListener) {
        this.clickListener = groupClickListener;
    }

    @Override
    public int getItemCount() {
        if (searching) {
            return filteredGroups.size();
        } else {
            return groups.size();
        }
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public void appendGroup(Group receivedGroup) {
        groups.add(receivedGroup);

        Collections.sort(groups, new Comparator<Group>() {
            @Override public int compare(Group g1, Group g2) {
                return g1.getName().compareTo(g2.getName());
            }
        });

        notifyDataSetChanged();
    }

    public void clearData() {
        groups.clear();
        notifyDataSetChanged();
    }

    public void filter(String text) {
        filteredGroups.clear();
        text = text.toLowerCase();
        for(Group group: groups) {
            if (group.getName().toLowerCase().contains(text)) {
                filteredGroups.add(group);
            }
        }
        notifyDataSetChanged();
    }

}
