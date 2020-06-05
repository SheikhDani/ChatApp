package com.multilingual.firebase.chat.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.multilingual.firebase.chat.activities.managers.SessionManager;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.multilingual.firebase.chat.activities.constants.IConstants.ADS_SHOWN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_LINK;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USERNAME;

public class WebViewBrowserActivity extends BaseActivity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        try {
            final Toolbar mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_USERNAME));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            Utils.getErrors(e);
        }

//        mInterstitialAd = new InterstitialAd(this);
//        final AdView adView = findViewById(R.id.adView);
//        if (ADS_SHOWN) {
//            adView.setVisibility(View.VISIBLE);
//            final AdRequest adRequest = new AdRequest.Builder().build();
//            adView.loadAd(adRequest);
//            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_app_id));
//            mInterstitialAd.loadAd(adRequest);
//        } else {
//            adView.setVisibility(View.GONE);
//        }

        final SessionManager session = new SessionManager(mActivity);
        final String linkPath = getIntent().getStringExtra(EXTRA_LINK);
        final WebView webView = findViewById(R.id.webView);

        StringBuilder sb = new StringBuilder();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                InputStream is = getAssets().open(linkPath);
                BufferedReader br = null;
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String str;
                while (true) {
                    if (!((str = br.readLine()) != null)) break;
                    sb.append(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String mimeType = "text/html;charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = sb.toString();
            String rtl = session.isRTLOn() ? "dir=\"rtl\"" : "";

            String text = "<html><head>"
                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/Roboto-Light.ttf\")}body{font-family: MyFont;color: #8D8D8D;}"
                    + "</style></head>"
                    + "<body " + rtl + ">"
                    + htmlText
                    + "</body></html>";

            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        } catch (Exception e) {
            try {
                webView.loadUrl("file:///android_asset/" + linkPath);
                webView.getSettings().setJavaScriptEnabled(true);
            } catch (Exception en) {
                Utils.getErrors(en);
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ADS_SHOWN) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
}
