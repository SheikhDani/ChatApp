package com.multilingual.firebase.chat.activities;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private TextView mTxtVersionName;
    private FirebaseUser firebaseUser; //Current User

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setWindow(getWindow());
        setContentView(R.layout.activity_splash);
        final String versionName = BuildConfig.VERSION_NAME;

        mTxtVersionName = findViewById(R.id.txtVersionName);
        mTxtVersionName.setText("v " + versionName);
        StartAnimations();
        load();
    }

    private void load() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Screens screens = new Screens(SplashActivity.this);
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    screens.showClearTopScreen(MainActivity.class);
                } else {
                    screens.showClearTopScreen(LoginActivity.class);
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 4000);
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        LinearLayout iv = findViewById(R.id.layout);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }

}
