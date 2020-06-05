package com.multilingual.firebase.chat.activities.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.adapters.UserAdapters;
import com.multilingual.firebase.chat.activities.constants.IFilterListener;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.managers.Utils.offline;
import static com.multilingual.firebase.chat.activities.managers.Utils.online;


public class UsersFragment extends BaseFragment {

    private AppCompatEditText txtSearch;
    private ImageView imgClear;
    private RelativeLayout imgNoUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        imgNoUsers = view.findViewById(R.id.imgNoUsers);
        imgNoUsers.setVisibility(View.GONE);

        imgClear = view.findViewById(R.id.imgClear);
        txtSearch = view.findViewById(R.id.txtSearch);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mUsers = new ArrayList<>();

        readUsers();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
                if (count > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgClear.setVisibility(View.GONE);
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
                txtSearch.requestFocus();
            }
        });

        return view;
    }

    private void showUsers() {
        if (mUsers.size() > 0) {
            imgNoUsers.setVisibility(View.GONE);
            userAdapters = new UserAdapters(getContext(), mUsers, FALSE,"");
            mRecyclerView.setAdapter(userAdapters);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            imgNoUsers.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }


    private void searchUsers(final String search) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Query query = Utils.getQuerySortBySearch();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equalsIgnoreCase(firebaseUser.getUid()) && user.isActive()) {
                            if (user.getSearch().contains(search)) {
                                onlineOptionFilter(user);
                            }
                        }
                    }
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       // Query query = FirebaseDatabase.getInstance().getReference(REF_USERS).orderByChild(EXTRA_SEARCH).startAt(search).endAt(search + "\uf8ff");
      // Query query = FirebaseDatabase.getInstance().getReference(REF_USERS).orderByChild(EXTRA_SEARCH).startAt(search).endAt(search + "\uf8ff");
       final Query query = Utils.getQuerySortBySearch();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if (dataSnapshot.hasChildren()) {
                    if (txtSearch.getText().toString().trim().equalsIgnoreCase("")) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            assert firebaseUser != null;
                            assert user != null;
                            if (!user.getId().equalsIgnoreCase(firebaseUser.getUid()) && user.isActive()) {
                                onlineOptionFilter(user);
                            }

                        }
                    }
                }
                showUsers();
                try {
                    final String searchHint = String.format(getString(R.string.strSearchWithCount), mUsers.size());
                    txtSearch.setHint(searchHint);
                } catch (Exception e) {
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
                if (user.getGender().equalsIgnoreCase(getString(R.string.english))) {
                   // if (male)
                        addNewUserDataToList(user);
                } else if (user.getGender().equalsIgnoreCase(getString(R.string.french))) {
                   // if (female)
                        addNewUserDataToList(user);
                }
                else if (user.getGender().equalsIgnoreCase(getString(R.string.spanish))) {

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
                        readUsers();
                    }
                });
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
