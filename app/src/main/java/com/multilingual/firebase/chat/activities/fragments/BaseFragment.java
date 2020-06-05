package com.multilingual.firebase.chat.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.adapters.GroupsAdapters;
import com.multilingual.firebase.chat.activities.adapters.UserAdapters;
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.User;

import java.util.ArrayList;

public class BaseFragment extends Fragment {

    public RecyclerView mRecyclerView;
    public ArrayList<User> mUsers;
    public UserAdapters userAdapters;
    public GroupsAdapters groupAdapters;
    public Screens screens;
    public Activity mActivity;
    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        screens = new Screens(getActivity());
        mContext = getContext();
    }

    private ProgressDialog pd = null;

    public void showProgress() {
        try {
            if (pd == null) {
                pd = new ProgressDialog(mActivity);
            }
            pd.setMessage(getString(R.string.msg_please_wait));
            pd.show();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void hideProgress() {
        try {
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
}
