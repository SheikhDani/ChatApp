package com.multilingual.firebase.chat.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.multilingual.firebase.chat.activities.constants.Constants;
import com.multilingual.firebase.chat.activities.fcm.ApplicationLifecycleManager;
import com.multilingual.firebase.chat.activities.fragments.ChatsFragment;
import com.multilingual.firebase.chat.activities.fragments.GroupsFragment;
import com.multilingual.firebase.chat.activities.fragments.ProfileFragment;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.multilingual.firebase.chat.activities.constants.IConstants.ADS_SHOWN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_DELAY;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;

public class MainActivity extends BaseActivity {

    private CircleImageView mImageView;
    private TextView mTxtUsername;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private InterstitialAd mInterstitialAd;
    private long exitTime = 0;
    private final int DEFAULT_DELAY = 2000;
    SharedPrefferenceHelper sHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageView);
        mTxtUsername = findViewById(R.id.txtUsername);
        mToolbar = findViewById(R.id.toolbar);

        sHelper = new SharedPrefferenceHelper(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        //mInterstitialAd = new InterstitialAd(this);
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        final User user = dataSnapshot.getValue(User.class);
                        mTxtUsername.setText(user.getUsername());
                        sHelper.setString(Constants.gender,user.getGender());
                        if (Utils.isEmpty(user.getGender())) {
                            Utils.selectGenderPopup(mActivity, firebaseUser.getUid(), "",sHelper);
                        } else {
                        }
                        Utils.setProfileImage(getApplicationContext(), user.getImageURL(), mImageView);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });

        mTabLayout = findViewById(R.id.tabLayout);


        mViewPager = findViewById(R.id.viewPager);

          ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
//        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
//        mViewPager.setAdapter(tabsAdapter);
        viewPageAdapter.addFragment(new ChatsFragment(), getString(R.string.strChats));
        viewPageAdapter.addFragment(new GroupsFragment(), getString(R.string.strGroups));
        viewPageAdapter.addFragment(new ProfileFragment(), getString(R.string.strProfile));
        mViewPager.setAdapter(viewPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

               // Toast.makeText(mActivity, ""+tab.getPosition() ,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setOffscreenPageLimit(viewPageAdapter.getCount() - 1);

    }

    class ViewPageAdapter extends FragmentPagerAdapter {

        public ArrayList<Fragment> fragments;
        public ArrayList<String> titles;

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemSettings:
                screens.openSettingsActivity();
                return true;

            case R.id.itemLogout:
                Utils.logout(mActivity);
                return true;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void exitApp() {
        try {

            if (mViewPager.getCurrentItem() == 0) {
                if ((System.currentTimeMillis() - exitTime) > DEFAULT_DELAY) {
                    try {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.press_again_to_exit), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = sbView.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } catch (Exception e) {
                        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                    }
                    exitTime = System.currentTimeMillis();
                } else {
                    if (ADS_SHOWN) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                    finish();
                }
            } else {
                mViewPager.setCurrentItem(0);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.readStatus(mActivity, getString(R.string.strOnline));
    }

    @Override
    protected void onPause() {
        super.onPause();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!ApplicationLifecycleManager.isAppVisible()) {
                    Utils.readStatus(mActivity, getString(R.string.strOffline));
                }
            }
        }, EXTRA_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.readStatus(mActivity, getString(R.string.strOffline));
    }

}
