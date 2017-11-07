package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.StringUtils;
import com.ajz.directoryhub.adapters.SearchGroupsAdapter;
import com.ajz.directoryhub.objects.Group;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adamzarn on 10/26/17.
 */

public class SearchGroupsFragment extends Fragment {

    private String searchKey;
    private SearchGroupsAdapter searchGroupsAdapter;
    private ArrayList<String> groupUids;

    @BindView(R.id.by_name)
    Button byName;

    @OnClick(R.id.by_name)
    public void byNameClicked() {
        byName.setSelected(true);
        searchKey = "lowercasedName";
        byCreator.setSelected(false);
        byUniqueID.setSelected(false);
    }

    @BindView(R.id.by_creator)
    Button byCreator;

    @OnClick(R.id.by_creator)
    public void byCreatorClicked() {
        byName.setSelected(false);
        byCreator.setSelected(true);
        searchKey = "lowercasedCreatedBy";
        byUniqueID.setSelected(false);
    }

    @BindView(R.id.by_uniqueID)
    Button byUniqueID;

    @OnClick(R.id.by_uniqueID)
    public void byUniqueIdClicked() {
        byName.setSelected(false);
        byCreator.setSelected(false);
        byUniqueID.setSelected(true);
        searchKey = "";
    }

    @BindView(R.id.search_groups_recycler_view)
    RecyclerView searchGroupsRecyclerView;

    @BindView(R.id.search_groups_search_view)
    SearchView searchGroupsSearchView;

    @BindView(R.id.searching_groups_progress_bar)
    ProgressBar searchingGroupsProgressBar;

    public SearchGroupsFragment() {
    }

    OnGroupClickListener mCallback;

    public interface OnGroupClickListener {
        void onGroupSelected(Group selectedGroup);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnGroupClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(get(R.string.must_implement_interface));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_groups_fragment, container, false);
        ButterKnife.bind(this, rootView);

        byName.setSelected(true);
        searchKey = "lowercasedName";

        searchGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchGroupsAdapter = new SearchGroupsAdapter();
        searchGroupsRecyclerView.setAdapter(searchGroupsAdapter);

        searchGroupsAdapter.setOnGroupClickListener(new SearchGroupsAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(Group selectedGroup) {
                mCallback.onGroupSelected(selectedGroup);
            }
        });

        searchGroupsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query, searchKey);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText, searchKey);
                return true;
            }
        });

        searchingGroupsProgressBar.setVisibility(View.INVISIBLE);

        return rootView;

    }

    public void search(String query, String searchKey) {
        if (StringUtils.isMissing(query)) {
            searchGroupsAdapter.clearData();
        } else {
            searchingGroupsProgressBar.setVisibility(View.VISIBLE);
            searchGroupsRecyclerView.setVisibility(View.INVISIBLE);
            FirebaseClient fb = new FirebaseClient();
            if (byUniqueID.isSelected()) {
                fb.queryGroupsByUniqueID(groupUids, searchGroupsAdapter, searchGroupsRecyclerView, query, searchingGroupsProgressBar);
            } else {
                fb.queryGroups(groupUids, searchGroupsAdapter, searchGroupsRecyclerView, query.toLowerCase(), searchKey, searchingGroupsProgressBar);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        groupUids = getArguments().getStringArrayList("groupUids");
    }

    public String get(int i) {
        return getContext().getResources().getString(i);
    }
}