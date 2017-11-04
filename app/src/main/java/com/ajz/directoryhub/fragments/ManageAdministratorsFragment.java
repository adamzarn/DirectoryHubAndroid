package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ajz.directoryhub.R;
import com.ajz.directoryhub.adapters.ManageAdministratorsAdapter;
import com.ajz.directoryhub.objects.Group;
import com.ajz.directoryhub.objects.Member;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 10/30/17.
 */

public class ManageAdministratorsFragment extends Fragment {

    private Group group;

    @BindView(R.id.manage_administrators_recycler_view)
    RecyclerView manageAdministratorsRecyclerView;

    @BindView(R.id.manage_administrators_search_view)
    SearchView manageAdministratorsSearchView;

    @BindView(R.id.manage_administrators_progress_bar)
    ProgressBar manageAdministratorsProgressBar;

    public ManageAdministratorsFragment() {
    }

    OnAdminsEditedListener adminsEditedListener;

    public interface OnAdminsEditedListener {
        void adminsEdited(Group group);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            adminsEditedListener = (ManageAdministratorsFragment.OnAdminsEditedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.manage_administrators_fragment, container, false);
        ButterKnife.bind(this, rootView);
        group = getArguments().getParcelable("groupBeingEdited");

        adminsEditedListener.adminsEdited(group);

        manageAdministratorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ManageAdministratorsAdapter manageAdministratorsAdapter = new ManageAdministratorsAdapter();
        manageAdministratorsRecyclerView.setAdapter(manageAdministratorsAdapter);
        manageAdministratorsAdapter.setSearching(false);

        manageAdministratorsProgressBar.setVisibility(View.VISIBLE);
        manageAdministratorsAdapter.setData(group.getAdmins(), group.getUsers());
        manageAdministratorsProgressBar.setVisibility(View.GONE);

        manageAdministratorsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    manageAdministratorsAdapter.setSearching(false);
                } else {
                    manageAdministratorsAdapter.setSearching(true);
                }
                manageAdministratorsAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    manageAdministratorsAdapter.setSearching(false);
                } else {
                    manageAdministratorsAdapter.setSearching(true);
                }
                manageAdministratorsAdapter.filter(newText);
                return true;
            }
        });

        manageAdministratorsAdapter.setOnAdminsChangedListener(new ManageAdministratorsAdapter.OnAdminsChangedListener() {
            @Override
            public void adminsChanged(ArrayList<Member> members) {
                HashMap<String, Object> admins = new HashMap<>();
                HashMap<String, Object> users = new HashMap<>();
                for (Member member : members) {
                    if (member.isAdmin()) {
                        admins.put(member.getUid(), member.getName());
                    } else {
                        users.put(member.getUid(), member.getName());
                    }
                }
                group.setAdmins(admins);
                group.setUsers(users);
                adminsEditedListener.adminsEdited(group);
            }
        });

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

}
