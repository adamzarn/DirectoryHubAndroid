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
import com.ajz.directoryhub.activities.MyGroupsActivity;
import com.ajz.directoryhub.adapters.MyGroupsAdapter;
import com.ajz.directoryhub.objects.Group;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ajz.directoryhub.ConnectivityReceiver.isConnected;

/**
 * Created by adamzarn on 10/21/17.
 */

public class MyGroupsFragment extends Fragment {

    private MyGroupsAdapter myGroupsAdapter;
    int currentVisiblePosition = 0;
    private Boolean dataLoaded = false;
    private FirebaseAuth mAuth;
    private String userUid;

    @BindView(R.id.my_groups_recycler_view)
    RecyclerView myGroupsRecyclerView;

    @BindView(R.id.my_groups_search_view)
    SearchView myGroupsSearchView;

    @BindView(R.id.loading_groups_progress_bar)
    ProgressBar loadingGroupsProgressBar;

    @BindView(R.id.add_group_fab)
    FloatingActionButton addGroupFab;

    @OnClick(R.id.add_group_fab)
    public void addGroupFabClicked() {
        dataLoaded = false;
        mAddGroupCallback.onAddGroupFabClicked();
    }

    @BindView(R.id.my_groups_footer_banner_ad)
    AdView mAdView;

    public MyGroupsFragment() {
    }

    OnGroupClickListener mCallback;
    OnAddGroupFabClickListener mAddGroupCallback;

    public interface OnGroupClickListener {
        void onGroupSelected(Group selectedGroup);
        void onGroupToEditSelected(Group groupToEdit);
        void onGroupToDeleteSelected(Group groupToDelete);
        void noInternet();
    }

    public interface OnAddGroupFabClickListener {
        void onAddGroupFabClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnGroupClickListener) context;
            mAddGroupCallback = (OnAddGroupFabClickListener) context;
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

        View rootView = inflater.inflate(R.layout.my_groups_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mAuth = FirebaseAuth.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        myGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (myGroupsAdapter == null) {

            myGroupsAdapter = new MyGroupsAdapter();
            myGroupsRecyclerView.setAdapter(myGroupsAdapter);
            myGroupsAdapter.setSearching(false);

            myGroupsAdapter.setOnGroupClickListener(new MyGroupsAdapter.OnGroupClickListener() {
                @Override
                public void onGroupClick(Group selectedGroup) {
                    dataLoaded = false;
                    mCallback.onGroupSelected(selectedGroup);
                }

                @Override
                public void onEditGroupClick(Group groupToEdit) {
                    dataLoaded = false;
                    mCallback.onGroupToEditSelected(groupToEdit);
                }

                @Override
                public void onDeleteGroupClick(Group groupToDelete) {
                    dataLoaded = false;
                    mCallback.onGroupToDeleteSelected(groupToDelete);
                }
            });

        } else {
            myGroupsRecyclerView.setAdapter(myGroupsAdapter);
        }

        myGroupsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    myGroupsAdapter.setSearching(false);
                } else {
                    myGroupsAdapter.setSearching(true);
                }
                myGroupsAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    myGroupsAdapter.setSearching(false);
                } else {
                    myGroupsAdapter.setSearching(true);
                }
                myGroupsAdapter.filter(newText);
                return true;
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataLoaded) {
            myGroupsRecyclerView.setVisibility(View.INVISIBLE);
            myGroupsAdapter.clearData();
            loadData();
        } else {
            loadingGroupsProgressBar.setVisibility(View.INVISIBLE);
            scrollToSavedPosition();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        currentVisiblePosition = ((LinearLayoutManager) myGroupsRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    public void loadData() {
        dataLoaded = true;
        loadingGroupsProgressBar.setVisibility(View.VISIBLE);
        if (isConnected()) {
            new FirebaseClient().getUserGroups((MyGroupsActivity) getActivity(), myGroupsAdapter, userUid, myGroupsRecyclerView, loadingGroupsProgressBar);
        } else {
            loadingGroupsProgressBar.setVisibility(View.INVISIBLE);
            mCallback.noInternet();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void scrollToSavedPosition() {
        myGroupsRecyclerView.getLayoutManager().scrollToPosition(currentVisiblePosition);
        currentVisiblePosition = 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public String get(int i) {
        return getContext().getResources().getString(i);
    }

}

