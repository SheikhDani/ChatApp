package com.multilingual.firebase.chat.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.multilingual.firebase.chat.activities.constants.IDialogListener;
import com.multilingual.firebase.chat.activities.managers.SessionManager;
import com.multilingual.firebase.chat.activities.managers.Utils;

import static com.multilingual.firebase.chat.activities.constants.IConstants.CLICK_DELAY_TIME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.PATH_ABOUT_US;
import static com.multilingual.firebase.chat.activities.constants.IConstants.PATH_PRIVACY_POLICY;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TRUE;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private SessionManager session;
    private SwitchCompat notificationOnOff, rtlOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        session = new SessionManager(mActivity);

//        final AdView adView = findViewById(R.id.adView);
//        if (ADS_SHOWN) {
//            adView.setVisibility(View.VISIBLE);
//            final AdRequest adRequest = new AdRequest.Builder().build();
//            adView.loadAd(adRequest);
//        } else {
//            adView.setVisibility(View.GONE);
//        }

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.strSettings);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final LinearLayout layoutNotification = findViewById(R.id.layoutNotification);
        final LinearLayout layoutRTL = findViewById(R.id.layoutRTL);
        final LinearLayout layoutRateApp = findViewById(R.id.layoutRateApp);
        final LinearLayout layoutShare = findViewById(R.id.layoutShare);
        final LinearLayout layoutAbout = findViewById(R.id.layoutAbout);
        final LinearLayout layoutPrivacyPolicy = findViewById(R.id.layoutPrivacyPolicy);
        final LinearLayout layoutLogout = findViewById(R.id.layoutLogout);

        rtlOnOff = findViewById(R.id.rtlOnOff);
        rtlOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartApp();
            }
        });

        rtlOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                session.setOnOffRTL(b);
            }
        });

        notificationOnOff = findViewById(R.id.notificationOnOff);
        notificationOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                session.setOnOffNotification(b);
            }
        });

        if (session.isNotificationOn()) {
            notificationOnOff.setChecked(TRUE);
        } else {
            notificationOnOff.setChecked(FALSE);
        }

        if (session.isRTLOn()) {
            rtlOnOff.setChecked(TRUE);
        } else {
            rtlOnOff.setChecked(FALSE);
        }

        layoutNotification.setOnClickListener(this);
        layoutRTL.setOnClickListener(this);
        layoutRateApp.setOnClickListener(this);
        layoutShare.setOnClickListener(this);
        layoutAbout.setOnClickListener(this);
        layoutPrivacyPolicy.setOnClickListener(this);
        layoutLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.layoutNotification:
                if (notificationOnOff.isChecked()) {
                    notificationOnOff.setChecked(FALSE);
                } else {
                    notificationOnOff.setChecked(TRUE);
                }
                break;

            case R.id.layoutRTL:
                if (rtlOnOff.isChecked()) {
                    rtlOnOff.setChecked(FALSE);
                } else {
                    rtlOnOff.setChecked(TRUE);
                }
                restartApp();
                break;

            case R.id.layoutRateApp:
                Utils.rateApp(mActivity);
                break;

            case R.id.layoutShare:
                Utils.shareApp(mActivity);
                break;

            case R.id.layoutAbout:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        screens.openWebViewActivity(getString(R.string.lblAboutUs), PATH_ABOUT_US);
                    }
                }, CLICK_DELAY_TIME);
                break;

            case R.id.layoutPrivacyPolicy:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        screens.openWebViewActivity(getString(R.string.lblPrivacyPolicy), PATH_PRIVACY_POLICY);
                    }
                }, CLICK_DELAY_TIME);
                break;

            case R.id.layoutLogout:
                Utils.logout(mActivity);
                break;
        }
    }

    private void restartApp() {
        Utils.showOKDialog(mActivity, R.string.ref_title, R.string.ref_message, new IDialogListener() {
            @Override
            public void yesButton() {
                screens.showClearTopScreen(MainActivity.class);
            }
        });
    }

}
