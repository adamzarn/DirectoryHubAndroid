package com.ajz.directoryhub.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajz.directoryhub.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 10/30/17.
 */

public class ManageAdministratorsAdapter extends RecyclerView.Adapter<ManageAdministratorsAdapter.ViewHolder> {

    private Boolean searching;

    private HashMap<String, Object> admins;
    private HashMap<String, Object> users;
    private HashMap<String, Object> members;
    private ArrayList<String> memberNames;

    @Override
    public ManageAdministratorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_administrators_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.memberName.setText(memberNames.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.member_name)
        TextView memberName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public int getItemCount() {
        return memberNames.size();
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public void setData(HashMap<String, Object> admins, HashMap<String, Object> users) {
        this.members = new HashMap<String, Object>();
        members.putAll(admins);
        members.putAll(users);

        for(Map.Entry<String, Object> member: members.entrySet()) {
            if (memberNames != null) {
                memberNames.add(member.getValue().toString());
            } else {
                memberNames = new ArrayList<String>(Arrays.asList(member.getValue().toString()));
            }
        }

        notifyDataSetChanged();
    }

}
