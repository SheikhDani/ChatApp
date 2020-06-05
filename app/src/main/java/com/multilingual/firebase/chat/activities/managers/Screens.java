package com.multilingual.firebase.chat.activities.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Toast;

import com.multilingual.firebase.chat.activities.GroupsParticipantsActivity;
import com.multilingual.firebase.chat.activities.ImageViewerActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.SettingsActivity;
import com.multilingual.firebase.chat.activities.ViewUserProfileActivity;
import com.multilingual.firebase.chat.activities.WebViewBrowserActivity;
import com.multilingual.firebase.chat.activities.models.Groups;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUP_NAME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMGPATH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_LINK;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_OBJ_GROUP;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USERNAME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REQUEST_PARTICIPATE;


public class Screens {

    private static Context context;

    public Screens(Context context) {
        Screens.context = context;
    }

    public void showClearTopScreen(final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void showCustomScreen(final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public void startHomeScreen() {
        Intent startHome = new Intent(Intent.ACTION_MAIN);
        startHome.addCategory(Intent.CATEGORY_HOME);
        startHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startHome);
    }

    public void showToast(String strMsg) {
        Toast.makeText(context, strMsg, Toast.LENGTH_LONG).show();
    }


    public void openViewProfileActivity(final String userId) {
        Intent intent = new Intent(context, ViewUserProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startActivity(intent);
    }

    public void openGroupParticipantActivity(final Groups groups) {
        Intent intent = new Intent(context, GroupsParticipantsActivity.class);
        intent.putExtra(EXTRA_OBJ_GROUP, groups);
        ((Activity) context).startActivityForResult(intent, REQUEST_PARTICIPATE);
    }

    public void openFullImageViewActivity(final View view, final String imgPath) {
        openFullImageViewActivity(view, imgPath, "");
    }

    public void openFullImageViewActivity(final View view, final String imgPath, final String groupName) {
        final Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_IMGPATH, imgPath);
        intent.putExtra(EXTRA_GROUP_NAME, groupName);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(intent);
            } else {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, view, context.getString(R.string.app_name));
                context.startActivity(intent, options.toBundle());
            }
        } catch (Exception e) {
            context.startActivity(intent);
        }
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public void openWebViewActivity(final String title, final String path) {
        Intent intent = new Intent(context, WebViewBrowserActivity.class);
        intent.putExtra(EXTRA_USERNAME, title);
        intent.putExtra(EXTRA_LINK, path);
        context.startActivity(intent);
    }
}
