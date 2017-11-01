package com.ajz.directoryhub.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.objects.Member;

import java.util.ArrayList;
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
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Member> filteredMembers = new ArrayList<>();

    private int adminCount = 0;

    @Override
    public ManageAdministratorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_administrators_item, parent, false);
        return new ViewHolder(v);
    }

    OnAdminsChangedListener mCallback;

    public interface OnAdminsChangedListener {
        void adminsChanged(ArrayList<Member> members);
    }

    public void setOnAdminsChangedListener(final OnAdminsChangedListener adminsChangedListener) {
        this.mCallback = adminsChangedListener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Member currentMember;
        if (searching) {
            currentMember = filteredMembers.get(position);
        } else {
            currentMember = members.get(position);
        }
        holder.memberName.setText(currentMember.getName());
        if (currentMember.isAdmin()) {
            holder.administratorCheckbox.setSelected(true);
        } else {
            holder.administratorCheckbox.setSelected(false);
        }

        holder.administratorCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.administratorCheckbox.isSelected()) {
                    if (adminCount > 1) {
                        holder.administratorCheckbox.setSelected(false);
                        currentMember.setIsAdmin(false);
                        adminCount--;
                        mCallback.adminsChanged(members);
                    }
                } else {
                    holder.administratorCheckbox.setSelected(true);
                    currentMember.setIsAdmin(true);
                    adminCount++;
                    mCallback.adminsChanged(members);
                }
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.member_name)
        TextView memberName;

        @BindView(R.id.administrator_checkbox)
        Button administratorCheckbox;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public int getItemCount() {
        if (searching) {
            return filteredMembers.size();
        } else {
            return members.size();
        }
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public void setData(HashMap<String, Object> admins, HashMap<String, Object> users) {

        if (admins != null) {
            for (Map.Entry<String, Object> entry : admins.entrySet()) {
                members.add(createMember(entry, true));
                adminCount++;
            }
        }

        if (users != null) {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                members.add(createMember(entry, false));
            }
        }

        notifyDataSetChanged();
    }

    public Member createMember(Map.Entry<String, Object> entry, Boolean isAdmin) {
        return new Member(entry.getKey(), entry.getValue().toString(), isAdmin);
    }

    public void filter(String text) {
        filteredMembers.clear();
        text = text.toLowerCase();
        for(Member member : members) {
            if (member.getName().toLowerCase().contains(text)) {
                filteredMembers.add(member);
            }
        }
        notifyDataSetChanged();
    }

}
