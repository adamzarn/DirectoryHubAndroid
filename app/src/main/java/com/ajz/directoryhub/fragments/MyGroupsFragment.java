package com.ajz.directoryhub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adamzarn on 10/21/17.
 */

public class MyGroupsFragment extends Fragment {

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
        mAddGroupCallback.onAddGroupFabClicked();
    }

    private FirebaseAuth mAuth;
    private String userUid;
    private MyGroupsAdapter myGroupsAdapter;

    public MyGroupsFragment() {
    }

    OnGroupClickListener mCallback;
    OnAddGroupFabClickListener mAddGroupCallback;

    public interface OnGroupClickListener {
        void onGroupSelected(Group selectedGroup);
        void onGroupToEditSelected(Group groupToEdit);
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
            throw new ClassCastException("Must implement ClickListeners");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.my_groups_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mAuth = FirebaseAuth.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(myGroupsRecyclerView);

        myGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myGroupsAdapter = new MyGroupsAdapter();
        myGroupsRecyclerView.setAdapter(myGroupsAdapter);
        myGroupsAdapter.setSearching(false);

        myGroupsAdapter.setOnGroupClickListener(new MyGroupsAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(Group selectedGroup) {
                mCallback.onGroupSelected(selectedGroup);
            }
            @Override
            public void onEditGroupClick(Group groupToEdit) {
                mCallback.onGroupToEditSelected(groupToEdit);
            }
        });

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

        return rootView;

    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String groupUid = myGroupsAdapter.getGroup(position).getUid();
            new FirebaseClient().deleteFromMyGroups(MyGroupsFragment.this, ((MyGroupsActivity) getActivity()).getGroupUids(), groupUid);
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    public void reloadData() {
        loadingGroupsProgressBar.setVisibility(View.VISIBLE);
        myGroupsAdapter.clearData();
        FirebaseClient fb = new FirebaseClient();
        fb.getUserGroups((MyGroupsActivity) getActivity(), myGroupsAdapter, userUid, loadingGroupsProgressBar);
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
