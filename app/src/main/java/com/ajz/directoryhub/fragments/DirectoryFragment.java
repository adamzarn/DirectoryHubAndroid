package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 10/21/17.
 */

public class DirectoryFragment extends Fragment {

    private DirectoryAdapter directoryAdapter;
    int currentVisiblePosition = 0;
    private Boolean dataLoaded = false;

    String groupUid;
    String groupName;
    Boolean isAdmin;

    @BindView(R.id.directory_recycler_view)
    RecyclerView directoryRecyclerView;

    @BindView(R.id.directory_search_view)
    SearchView directorySearchView;

    @BindView(R.id.directory_progress_bar)
    ProgressBar directoryProgressBar;

    @BindView(R.id.add_entry_fab)
    FloatingActionButton addEntryFab;

    @OnClick(R.id.add_entry_fab)
    public void addEntry() {
        dataLoaded = false;
        mCallback.onAddEntrySelected();
    }

    @BindView(R.id.directory_footer_banner_ad)
    AdView mAdView;

    public DirectoryFragment() {
    }

    OnEntryClickListener mCallback;

    public void directoryReceived(ArrayList<Entry> recievedEntries) {
        directoryRecyclerView.setVisibility(View.VISIBLE);
        directoryProgressBar.setVisibility(View.INVISIBLE);
        directoryAdapter.setData(recievedEntries);
    }

    public interface OnEntryClickListener {
        void onEntrySelected(Entry selectedEntry);
        void onDeleteEntryClicked(Entry entryToDelete);
        void onAddEntrySelected();
        void updateWidget();
        void noInternet();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEntryClickListener) context;
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

        View rootView = inflater.inflate(R.layout.directory_fragment, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager llManager = new LinearLayoutManager(getActivity());
        directoryRecyclerView.setLayoutManager(llManager);

        if (directoryAdapter == null) {

            directoryAdapter = new DirectoryAdapter();
            directoryRecyclerView.setAdapter(directoryAdapter);
            directoryAdapter.setSearching(false);

            groupUid = getArguments().getString("groupUid");
            groupName = getArguments().getString("groupName");
            isAdmin = getArguments().getBoolean("isAdmin");

            directoryAdapter.setIsAdmin(isAdmin);

            directoryAdapter.setOnEntryClickListener(new DirectoryAdapter.OnEntryClickListener() {
                @Override
                public void onEntryClick(Entry selectedEntry) {
                    dataLoaded = false;
                    mCallback.onEntrySelected(selectedEntry);
                }

                @Override
                public void deleteEntryClick(Entry entryToDelete) {
                    dataLoaded = false;
                    mCallback.onDeleteEntryClicked(entryToDelete);
                }
            });

        } else {
            directoryRecyclerView.setAdapter(directoryAdapter);
        }

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

        if (!isAdmin) {
            addEntryFab.setVisibility(View.GONE);
        }

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataLoaded) {
            directoryRecyclerView.setVisibility(View.INVISIBLE);
            loadData();
        } else {
            directoryProgressBar.setVisibility(View.INVISIBLE);
            scrollToSavedPosition();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        currentVisiblePosition = ((LinearLayoutManager) directoryRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    public void loadData() {

        if (isConnected()) {

            dataLoaded = true;
            directoryProgressBar.setVisibility(View.VISIBLE);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("groupUid", groupUid);
            editor.putString("groupName", groupName);
            editor.putBoolean("isAdmin", isAdmin);
            editor.apply();
            mCallback.updateWidget();

            new FirebaseClient().getDirectory(groupUid);

        } else {
            mCallback.noInternet();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void scrollToSavedPosition() {
        directoryRecyclerView.getLayoutManager().scrollToPosition(currentVisiblePosition);
        currentVisiblePosition = 0;
    }

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}
