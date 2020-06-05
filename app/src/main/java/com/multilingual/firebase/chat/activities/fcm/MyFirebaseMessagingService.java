package com.multilingual.firebase.chat.activities.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.database.DatabaseReference;
import com.multilingual.firebase.chat.activities.GroupsMessagesActivity;
import com.multilingual.firebase.chat.activities.MessageActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.SharedPrefferenceHelper;
import com.multilingual.firebase.chat.activities.constants.Constants;
import com.multilingual.firebase.chat.activities.managers.SessionManager;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Groups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_OBJ_GROUP;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_BODY;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_GROUPS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_ICON;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_SENT;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_TITLE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_TYPE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_USER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_USERNAME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_IMAGE;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private SessionManager session;
    String gender,translatelangauge;
    SharedPrefferenceHelper sHelper;
    TranslateOptions options;
    private FirebaseUser firebaseUser;
    public DatabaseReference reference;
    private String currentId;
    Translate translate;
    private static final String API_KEY = "AIzaSyCL9zsTgJdmU4dY8nRdeCNafjv5UoIfL0k";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Utils.uploadToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null)
            return;

        session = new SessionManager(getApplicationContext());
        if (!session.isNotificationOn()) {
            return;
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            getTranslateService();
            String sent = remoteMessage.getData().get(FCM_SENT);
            if (!ApplicationLifecycleManager.isAppVisible()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null && sent.equalsIgnoreCase(firebaseUser.getUid())) {
                    sendNotification(remoteMessage);
                }
            }
        }

    }

    private Groups groups = null;
    private String strGroups = "";
    private Intent intent;
    private Bitmap bitmap = null;
    private String body = "";
    private String type = "";
    private String username = "";

    private void sendNotification(RemoteMessage remoteMessage) {
        final String user = remoteMessage.getData().get(FCM_USER);
        final String icon = remoteMessage.getData().get(FCM_ICON);
        final String title = remoteMessage.getData().get(FCM_TITLE);
        final String message = remoteMessage.getData().get(FCM_BODY);
       sHelper = new SharedPrefferenceHelper(MyFirebaseMessagingService.this);



        try {
            type = remoteMessage.getData().get(FCM_TYPE);
            username = remoteMessage.getData().get(FCM_USERNAME);
        } catch (Exception e) {
        }
        try {
            strGroups = remoteMessage.getData().get(FCM_GROUPS);
        } catch (Exception e) {
        }

        bitmap = null;

        if (!Utils.isEmpty(type)) {
            if (type.equalsIgnoreCase(TYPE_IMAGE)) {
                bitmap = getBitmapFromURL(message);
                body = String.format(getString(R.string.strPhotoSent), username);
            } else {
                body =  message;
            }
        } else {
            body = message;
        }

        Bundle bundle = new Bundle();

        if (Utils.isEmpty(strGroups)) {
            intent = new Intent(this, MessageActivity.class);
            bundle.putString(EXTRA_USER_ID, user);
        } else {
            intent = new Intent(this, GroupsMessagesActivity.class);
            groups = new Gson().fromJson(strGroups, Groups.class);
            bundle.putSerializable(EXTRA_OBJ_GROUP, groups);
        }

        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

if(sHelper!=null) {
    gender  = sHelper.getString(Constants.gender);
}
        if (gender.equals("French")) {
            translatelangauge= "fr";
        } else if (gender.equals("English")) {
            translatelangauge = "en";
        }
        else if (gender.equals("Spanish")) {
            translatelangauge = "es";
        }
        else if (gender.equals("Arabic")) {
            translatelangauge = "ar";
        }
        else if (gender.equals("Hindi")) {
            translatelangauge = "hi";
        }
        else if (gender.equals("Afrikaans")) {
            translatelangauge = "af";
        }
        else if (gender.equals("Sesotho")) {
            translatelangauge = "st";
        }
        else if (gender.equals("Zulu")) {
            translatelangauge = "zu";
        }
        else if (gender.equals("Russian")) {
            translatelangauge = "ru";
        }
        else {
            translatelangauge = "sw";
        }
        Translation translation = translate.translate(body, Translate.TranslateOption.targetLanguage(translatelangauge));
        String text = String.valueOf(Html.fromHtml(translation.getTranslatedText()));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(username + ": " +text)
                .setTicker(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (bitmap != null) {
            notificationBuilder.setLargeIcon(bitmap);
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(bitmap));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Chat Notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) new Date().getTime(), notificationBuilder.build());
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */


    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
        translate = options.getService();

    }
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            final URL url = new URL(strURL);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            final InputStream input = connection.getInputStream();
            final Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            Utils.getErrors(e);
            return null;
        }
    }

}
