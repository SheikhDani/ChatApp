package com.multilingual.firebase.chat.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class BaseActivity extends AppCompatActivity {

    public Activity mActivity;
    public FirebaseAuth auth; //Auth init
    public DatabaseReference reference; //Database releated
    public FirebaseUser firebaseUser; //Current User
    public Screens screens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        screens = new Screens(mActivity);
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
