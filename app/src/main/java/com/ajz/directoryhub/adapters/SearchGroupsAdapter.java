package com.ajz.directoryhub.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Group;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 10/26/17.
 */

public class SearchGroupsAdapter extends RecyclerView.Adapter<SearchGroupsAdapter.ViewHolder> {

    private ArrayList<Group> groups = new ArrayList<>();

    private OnGroupClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.group_name_text_view)
        TextView groupNameTextView;

        @BindView(R.id.city_state_text_view)
        TextView cityStateTextView;

        @BindView(R.id.created_by_text_view)
        TextView createdByTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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
    public void onBindViewHolder(SearchGroupsAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameTextView.setText(group.getName());
        holder.cityStateTextView.setText(group.getCity() + ", " + group.getState());
        String createdByString = "Created by: " + group.getCreatedBy();
        holder.createdByTextView.setText(createdByString);
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
