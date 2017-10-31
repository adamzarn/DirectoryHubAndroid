package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ajz.directoryhub.FirebaseClient;
import com.ajz.directoryhub.R;
import com.ajz.directoryhub.adapters.DirectoryAdapter;
import com.ajz.directoryhub.objects.Entry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamzarn on 10/21/17.
 */

public class DirectoryFragment extends Fragment {

    @BindView(R.id.directory_recycler_view)
    RecyclerView directoryRecyclerView;

    @BindView(R.id.directory_search_view)
    SearchView directorySearchView;

    @BindView(R.id.directory_progress_bar)
    ProgressBar directoryProgressBar;

    @BindView(R.id.add_entry_fab)
    FloatingActionButton addEntryFab;

    public DirectoryFragment() {
    }

    OnEntryClickListener mCallback;

    public interface OnEntryClickListener {
        void onEntrySelected(Entry selectedEntry);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEntryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.directory_fragment, container, false);
        ButterKnife.bind(this, rootView);

        directoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final DirectoryAdapter directoryAdapter = new DirectoryAdapter();
        directoryRecyclerView.setAdapter(directoryAdapter);
        directoryAdapter.setSearching(false);

        directoryAdapter.setOnEntryClickListener(new DirectoryAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(Entry selectedEntry) {
                mCallback.onEntrySelected(selectedEntry);
            }

        });

        directorySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    directoryAdapter.setSearching(false);
                } else {
                    directoryAdapter.setSearching(true);
                }
                directoryAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    directoryAdapter.setSearching(false);
                } else {
                    directoryAdapter.setSearching(true);
                }
                directoryAdapter.filter(newText);
                return true;
            }
        });

        if (!getArguments().getBoolean("isAdmin")) {
            addEntryFab.setVisibility(View.GONE);
        }

        directoryProgressBar.setVisibility(View.VISIBLE);
        FirebaseClient fb = new FirebaseClient();
        fb.getDirectory(directoryAdapter, getArguments().getString("groupUid"), directoryProgressBar);

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
