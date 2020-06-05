package com.multilingual.firebase.chat.activities.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TRUE;



public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "BytesBeeChatV1";
    private static final String KEY_ON_OFF_NOTIFICATION = "onOffNotification";
    private static final String KEY_ON_OFF_RTL = "onOffRTL";

    private final SharedPreferences pref;

    public SessionManager(final Context context) {
        pref = context.getSharedPreferences(context.getPackageName() + PREF_NAME, 0);
    }

    public void setOnOffNotification(final boolean value) {
        final Editor editor = pref.edit();
        editor.putBoolean(KEY_ON_OFF_NOTIFICATION, value);
        editor.apply();
    }

    public boolean isNotificationOn() {
        return pref.getBoolean(KEY_ON_OFF_NOTIFICATION, TRUE);
    }

    public void setOnOffRTL(final boolean value) {
        final Editor editor = pref.edit();
        editor.putBoolean(KEY_ON_OFF_RTL, value);
        editor.apply();
    }

    public boolean isRTLOn() {
        return pref.getBoolean(KEY_ON_OFF_RTL, FALSE);
    }

    public void clearAll() {
        final Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
