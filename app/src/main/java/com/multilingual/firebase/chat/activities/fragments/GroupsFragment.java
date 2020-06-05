package com.multilingual.firebase.chat.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.multilingual.firebase.chat.activities.GroupsAddActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.adapters.GroupsAdapters;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Groups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.multilingual.firebase.chat.activities.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUPS_IN_BOTH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUP_MEMBERS_S;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;


public class GroupsFragment extends BaseFragment {

    private FirebaseUser firebaseUser;
    private ArrayList<String> groupList;
    private RelativeLayout imgNoMessage;
    private GroupsAdapters groupsAdapters;
    private ArrayList<Groups> mGroups;
    public DatabaseReference reference;
    private String currentId;
    private String gender;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        final FloatingActionButton fabGroupAdd = view.findViewById(R.id.fabGroupAdd);
        imgNoMessage = view.findViewById(R.id.imgNoMessage);
        imgNoMessage.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        groupList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentId = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    //strUsername = user.getUsername();
                    gender = user.getGender();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference(REF_GROUP_MEMBERS_S + firebaseUser.getUid() + EXTRA_GROUPS_IN_BOTH);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String strGroupId = snapshot.getValue(String.class);
                        groupList.add(strGroupId);
                    }
                }

                if (groupList.size() > 0) {
                    imgNoMessage.setVisibility(View.GONE);
                    readGroups();
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    imgNoMessage.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabGroupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.showCustomScreen(GroupsAddActivity.class);
            }
        });


        return view;
    }

    public void readGroups() {
        mGroups = new ArrayList<>();

        Query reference = FirebaseDatabase.getInstance().getReference(REF_GROUPS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroups.clear();
                if (dataSnapshot.hasChildren()) {
                    Map<String, Groups> uList = new HashMap<>();
                    try {

                        for (String id : groupList) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Groups groups = snapshot.getValue(Groups.class);
                                if (groups.getId().equalsIgnoreCase(id) && groups.isActive()) {
                                    uList.put(groups.getId(), groups);
                                    break;
                                } else {
                                }
                            }
                        }
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }

                    if (uList.size() > 0) {
                        uList = Utils.sortByGroupDateTime(uList, false);

                        for (Groups groups : uList.values()) {
                            mGroups.add(groups);
                        }
                    }
                    groupsAdapters = new GroupsAdapters(getContext(), mGroups,gender);
                    mRecyclerView.setAdapter(groupsAdapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}