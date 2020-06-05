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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.UsersActivity;
import com.multilingual.firebase.chat.activities.adapters.UserAdapters;
import com.multilingual.firebase.chat.activities.constants.IFilterListener;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_CHATS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SLASH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TRUE;
import static com.multilingual.firebase.chat.activities.managers.Utils.offline;
import static com.multilingual.firebase.chat.activities.managers.Utils.online;


public class ChatsFragment extends BaseFragment {

    private FirebaseUser firebaseUser;
    private ArrayList<String> userList;
    private RelativeLayout imgNoMessage;
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
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        final FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        imgNoMessage = view.findViewById(R.id.imgNoMessage);
        imgNoMessage.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

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

        userList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(currentId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                uList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(snapshot.getKey());
                    }
                }

                if (userList.size() > 0) {
                    sortChats();
                } else {
                    imgNoMessage.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Utils.uploadToken(newToken);
            }
        });

        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.showCustomScreen(UsersActivity.class);
            }
        });

        return view;
    }

    Map<String, Chat> uList = new HashMap<>();

    private void sortChats() {
        for (int i = 0; i < userList.size(); i++) {
            final String key = userList.get(i);

            Query query = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(currentId + SLASH + key).limitToLast(1);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            uList.put(key, chat);
                        }
                    }

                    if (uList.size() == userList.size()) {

                        if (uList.size() > 0) {
                            uList = Utils.sortByChatDateTime(uList, false);
                        }

                        userList = new ArrayList(uList.keySet());

                        readChats();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void readChats() {
        mUsers = new ArrayList<>();

        Query reference = Utils.getQuerySortBySearch();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if (dataSnapshot.hasChildren()) {
                    try {
                        for (String id : userList) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                //gender = user.getGender();
                                if (user.getId().equalsIgnoreCase(id) && user.isActive()) {
                                    onlineOptionFilter(user);
                                    break;
                                } else {
                                }
                            }
                        }
                    } catch (Exception e) {
                        //Utils.getErrors(e);
                    }
                }

                if (mUsers.size() > 0) {
                    imgNoMessage.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    userAdapters = new UserAdapters(getContext(), mUsers, TRUE,gender);
                    mRecyclerView.setAdapter(userAdapters);
                } else {
                    imgNoMessage.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void onlineOptionFilter(final User user) {
        if (user.getStatus().equalsIgnoreCase(getString(R.string.strOnline))) {
            if (online)
                levelOptionFilter(user);
        } else if (user.getStatus().equalsIgnoreCase(getString(R.string.strOffline))) {
            if (offline)
                levelOptionFilter(user);
        }
    }

    private void levelOptionFilter(final User user) {
        try {
            if (Utils.isEmpty(user.getGender())) {
                //if (notset)
                    addNewUserDataToList(user);
            } else {
                if (user.getGender().equalsIgnoreCase(getString(R.string.french))) {
                   // if (male)
                        addNewUserDataToList(user);
                } else if (user.getGender().equalsIgnoreCase(getString(R.string.english))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.spanish))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.arabic))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.hindi))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.afrikaans))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.tswana))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.zulu))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.russian))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.swahili))) {
                    //if (female)
                    addNewUserDataToList(user);
                }
            }
        } catch (Exception e) {
            addNewUserDataToList(user);
        }
    }

    private void addNewUserDataToList(User user) {
        mUsers.add(user);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);

        MenuItem searchItem = menu.findItem(R.id.itemFilter);

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.filterPopup(getActivity(), new IFilterListener() {
                    @Override
                    public void showFilterUsers() {
                        readChats();
                    }
                });
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
