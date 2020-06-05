package com.multilingual.firebase.chat.activities.managers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.multilingual.firebase.chat.activities.LoginActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.SharedPrefferenceHelper;
import com.multilingual.firebase.chat.activities.constants.IDialogListener;
import com.multilingual.firebase.chat.activities.constants.IFilterListener;
import com.multilingual.firebase.chat.activities.fcmmodels.Token;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.Groups;
import com.multilingual.firebase.chat.activities.models.Others;
import com.multilingual.firebase.chat.activities.models.User;
import com.multilingual.firebase.chat.activities.views.customimage.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.multilingual.firebase.chat.activities.constants.Constants.gender;
import static com.multilingual.firebase.chat.activities.constants.IConstants.CLICK_DELAY_TIME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.DEFAULT_UPDATE_URL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.DEFAULT_UPDATE_URL_2;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EIGHT;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_ACTIVE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GENDER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_LASTSEEN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SEARCH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_STATUS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FIVE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FOUR;
import static com.multilingual.firebase.chat.activities.constants.IConstants.IMG_DEFAULTS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.NINE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.ONE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_OTHERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_TOKENS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SEVEN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SIX;
import static com.multilingual.firebase.chat.activities.constants.IConstants.THRE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TWO;
import static com.multilingual.firebase.chat.activities.constants.IConstants.ZERO;


public class Utils {

    public static final boolean IS_TRIAL = false;
    private static final int DEFAULT_VIBRATE = 500;
    public static boolean online = true, offline = true;
    public static boolean male = true, female = true, notset = true;
    private static String strSelectedGender = "";
    private static String[] strArray = null;
    private static int index = -1;

    public static void sout(String msg) {
        if (IS_TRIAL) {
            System.out.println("Prashant :: " + msg);
        }
    }

    public static boolean isEmpty(final Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<?>) s).isEmpty();
        }
        if (s instanceof Object[]) {
            return (((Object[]) s).length == 0);
        }
        return false;
    }

    public static void getErrors(final Exception e) {
        if (IS_TRIAL) {
            final String stackTrace = "Prashant ::" + Log.getStackTraceString(e);
            System.out.println(stackTrace);
        }
    }

    public static String getDateTime() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String getCapsWord(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatDateTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatLocalTime(long timeInMillis) {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatUTC.parse(formatTime(timeInMillis));
        } catch (ParseException e) {
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (date == null) {
            return dateFormat.format(timeInMillis);
        }
        return dateFormat.format(date);
    }

    public static String formatLocalFullTime(long timeInMillis) {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatUTC.parse(formatDateTime(timeInMillis));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (date == null) {
            return dateFormat.format(timeInMillis);
        }
        return dateFormat.format(date);
    }

    public static String formatDateTime(final Context context, final String timeInMillis) {
        long localTime = 0L;
        try {
            //localTime = dateToMillis(formatLocalTime(timeInMillis));
            localTime = dateToMillis(formatLocalFullTime(dateToMillis(timeInMillis)));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        if (isToday(localTime)) {
            return formatTime(context, localTime);
//            return formatLocalTime(context, timeInMillis);
        } else {
            return formatDateNew(localTime);
        }
    }

    public static long dateToMillis(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = sdf.parse(dateString);
        return date.getTime();
    }

    public static String formatFullDate(String timeString) {
        long timeInMillis = 0;
        try {
            timeInMillis = dateToMillis(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timeInMillis).toUpperCase();
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDateNew(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(Calendar.getInstance().getTimeInMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }

    public static String formatLocalTime(Context context, long when) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        int flags = DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL;

        if (then.year != now.year) {
            flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(context, when, flags);
    }

    public static String formatTime(Context context, long when) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        int flags = DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL;

        if (then.year != now.year) {
            flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(context, when, flags);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();

        // Add the elements to set
        set.addAll(list);

        // Clear the list
        list.clear();

        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);

        // return the list
        return list;
    }

    public static void setProfileImage(Context context, String imgUrl, ImageView mImageView) {
        try {

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {

                Picasso.get().load(imgUrl).fit().placeholder(R.drawable.profile_avatar).into(mImageView);
            } else {

                Picasso.get().load(R.drawable.profile_avatar).fit().into(mImageView);
            }
        } catch (Exception e) {
        }
    }


    public static void setProfileBlurImage(Context context, String imgUrl, ImageView mImageView) {
        try {
            BlurTransformation blur = new BlurTransformation(context, 25, 1);

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {

                Picasso.get().load(imgUrl).transform(blur).placeholder(R.drawable.profile_avatar).into(mImageView);
            } else {

                Picasso.get().load(R.drawable.profile_avatar).transform(blur).placeholder(R.drawable.profile_avatar).into(mImageView);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void setGroupImage(String imgUrl, ImageView mImageView) {
        try {

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {

                Picasso.get().load(imgUrl).fit().placeholder(R.drawable.img_group_default).into(mImageView);
            } else {

                Picasso.get().load(R.drawable.img_group_default).fit().into(mImageView);
            }
        } catch (Exception e) {
        }
    }

    public static void setGroupParticipateImage(String imgUrl, ImageView mImageView) {
        try {

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {

                Picasso.get().load(imgUrl).placeholder(R.drawable.img_group_default).into(mImageView);
            } else {

                Picasso.get().load(R.drawable.img_group_default).into(mImageView);
            }
        } catch (Exception e) {
        }
    }

    public static void setChatImage(String imgUrl, ImageView mImageView) {
        try {

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {

                Picasso.get().load(imgUrl).placeholder(R.drawable.image_load).into(mImageView);
            } else {

                Picasso.get().load(R.drawable.image_load).fit().into(mImageView);
            }
        } catch (Exception e) {
        }
    }

    public static void uploadToken(String referenceToken) {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_TOKENS);
                Token token = new Token(referenceToken);
                reference.child(firebaseUser.getUid()).setValue(token);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void uploadTypingStatus() {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS);
                Others token = new Others(FALSE);
                reference.child(firebaseUser.getUid()).setValue(token);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void setWindow(final Window w) {
        //make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static void RTLSupport(Window window) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void shareApp(final Activity mActivity, final String title) {
        try {
            final String app_name = android.text.Html.fromHtml(title).toString();
            final String share_text = android.text.Html.fromHtml(mActivity.getResources().getString(R.string.strShareContent)).toString();
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n\n" + share_text + "\n\n" + "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName());
            sendIntent.setType("text/plain");
            mActivity.startActivity(sendIntent);
        } catch (Resources.NotFoundException e) {
            Utils.getErrors(e);
        }
    }

    public static void shareApp(final Activity mActivity) {
        shareApp(mActivity, mActivity.getResources().getString(R.string.strShareTitle));
    }

    public static ArrayList<User> sortByUser(ArrayList<User> mUsers) {
        Collections.sort(mUsers, new Comparator<User>() {
            public int compare(User s1, User s2) {
                // notice the cast to (Integer) to invoke compareTo
                return (s1.getUsername()).compareTo(s2.getUsername());
            }
        });
        return mUsers;
    }

    public static void rateApp(final Activity mActivity) {
        final String appName = mActivity.getPackageName();
        try {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEFAULT_UPDATE_URL_2 + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEFAULT_UPDATE_URL + appName)));
        }
    }

    public static Map<String, User> sortByUser(Map<String, User> unsortMap, final boolean order) {

        List<Entry<String, User>> list = new LinkedList<Entry<String, User>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, User>>() {

            public int compare(Entry<String, User> o1, Entry<String, User> o2) {
                try {
                    return (o1.getValue().getUsername()).compareTo(o2.getValue().getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, User> sortedMap = new LinkedHashMap<>();
        for (Entry<String, User> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static Map<String, String> sortByString(Map<String, String> unsortMap, final boolean order) {

        List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, String>>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                try {
                    if (order) {
                        return dateFormat.parse(o1.getValue()).compareTo(dateFormat.parse(o2.getValue()));
                    } else {
                        return dateFormat.parse(o2.getValue()).compareTo(dateFormat.parse(o1.getValue()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void printMap(Map<String, Chat> map) {
        for (Entry<String, Chat> entry : map.entrySet()) {
            Utils.sout("Key : " + entry.getKey() + " Value : " + entry.getValue().getMessage() + " >> " + entry.getValue().getDatetime());
        }
    }

    public static Map<String, Chat> sortByChatDateTime(Map<String, Chat> unsortMap, final boolean order) {

        List<Entry<String, Chat>> list = new LinkedList<Entry<String, Chat>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, Chat>>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            public int compare(Entry<String, Chat> o1, Entry<String, Chat> o2) {
                try {
                    if (order) {
                        return dateFormat.parse(o1.getValue().getDatetime()).compareTo(dateFormat.parse(o2.getValue().getDatetime()));
                    } else {
                        return dateFormat.parse(o2.getValue().getDatetime()).compareTo(dateFormat.parse(o1.getValue().getDatetime()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, Chat> sortedMap = new LinkedHashMap<>();
        for (Entry<String, Chat> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static Map<String, Groups> sortByGroupDateTime(Map<String, Groups> unsortMap, final boolean order) {

        List<Entry<String, Groups>> list = new LinkedList<Entry<String, Groups>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, Groups>>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            public int compare(Entry<String, Groups> o1, Entry<String, Groups> o2) {
                try {
                    if (order) {
                        return dateFormat.parse(o1.getValue().getLastMsgTime()).compareTo(dateFormat.parse(o2.getValue().getLastMsgTime()));
                    } else {
                        return dateFormat.parse(o2.getValue().getLastMsgTime()).compareTo(dateFormat.parse(o1.getValue().getLastMsgTime()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, Groups> sortedMap = new LinkedHashMap<>();
        for (Entry<String, Groups> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void setVibrate(final Context mContext) {
        try {
            Vibrator vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(DEFAULT_VIBRATE, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vib.vibrate(DEFAULT_VIBRATE);
            }
        } catch (Exception e) {
        }
    }

    public static void selectGenderPopup(final Activity mContext, final String userId, final String selectGender, final SharedPrefferenceHelper sharedPrefferenceHelper) {

        strArray = mContext.getResources().getStringArray(R.array.arrGender);
        if (Utils.isEmpty(selectGender)) {
            index = -1;
        } else {
            index = Arrays.asList(strArray).indexOf(selectGender);
            strSelectedGender = selectGender;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        CardView view = (CardView) mContext.getLayoutInflater().inflate(R.layout.dialog_gender, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager sessionManager = new SessionManager(mContext);
            if (sessionManager.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        final RadioGroup radioGenderGroup = view.findViewById(R.id.rdoGroupGender);
        final RadioButton radioMale = view.findViewById(R.id.rdoMale);
        final RadioButton radioFemale = view.findViewById(R.id.rdoFemale);
        final RadioButton radioSpanish = view.findViewById(R.id.rdospanish);
        final RadioButton radioSwahili = view.findViewById(R.id.rdoswahili);
        final RadioButton radioArabic = view.findViewById(R.id.rdoarabic);
        final RadioButton radioHindi = view.findViewById(R.id.rdohindi);
        final RadioButton radioAfrikaans = view.findViewById(R.id.rdoaffricans);
        final RadioButton radioTswana = view.findViewById(R.id.rdotswanu);
        final RadioButton radioZulu = view.findViewById(R.id.rdozulu);
        final RadioButton radioRussian = view.findViewById(R.id.rdorussian);

        final AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        final AppCompatButton btnDone = view.findViewById(R.id.btnDone);

        builder.setView(view);

        if (index == ZERO) {
            radioMale.setChecked(true);
            radioFemale.setChecked(false);
        } else if (index == ONE) {
            radioMale.setChecked(false);
            radioFemale.setChecked(true);
        }
        else if (index == TWO) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(true);
        }
        else if (index == THRE) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(true);
        }
        else if (index == FOUR) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(true);
        }
        else if (index == FIVE) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(true);
        }
        else if (index == SIX) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(false);
            radioTswana.setChecked(true);
        }
        else if (index == SEVEN) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(false);
            radioTswana.setChecked(false);
            radioZulu.setChecked(true);
        }
        else if (index == EIGHT) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(false);
            radioTswana.setChecked(false);
            radioZulu.setChecked(false);
            radioRussian.setChecked(true);
        }
        else if (index == NINE) {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(false);
            radioTswana.setChecked(false);
            radioZulu.setChecked(false);
            radioRussian.setChecked(false);
            radioSwahili.setChecked(true);
        }
        else {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
            radioSpanish.setChecked(false);
            radioArabic.setChecked(false);
            radioHindi.setChecked(false);
            radioAfrikaans.setChecked(false);
            radioTswana.setChecked(false);
            radioZulu.setChecked(false);
            radioRussian.setChecked(false);
            radioSwahili.setChecked(false);
        }

        radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    strSelectedGender = rb.getText().toString();
                }

            }
        });

        final AlertDialog alert = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmpty(strSelectedGender)) {
                    Toast.makeText(mContext, mContext.getString(R.string.msgSelectGender), Toast.LENGTH_LONG).show();
                } else {
                    Utils.updateGender(userId, strSelectedGender);
                    sharedPrefferenceHelper.setString(gender,strSelectedGender );
                    alert.dismiss();
                }


                //alert.dismiss();
            }
        });

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    public static void showYesNoDialog(final Activity mActivity, int title, int message, final IDialogListener iDialogListener) {
        showYesNoDialog(mActivity, title == ZERO ? "" : mActivity.getString(title), mActivity.getString(message), iDialogListener);
    }

    public static void showYesNoDialog(final Activity mActivity, String title, int message, final IDialogListener iDialogListener) {
        showYesNoDialog(mActivity, title, mActivity.getString(message), iDialogListener);
    }

    public static void showYesNoDialog(final Activity mActivity, String title, String message, final IDialogListener iDialogListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        CardView view = (CardView) mActivity.getLayoutInflater().inflate(R.layout.dialog_custom, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager sessionManager = new SessionManager(mActivity);
            if (sessionManager.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        final TextView txtTitle = view.findViewById(R.id.txtTitle);
        final TextView txtMessage = view.findViewById(R.id.txtMessage);
        final AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        final AppCompatButton btnDone = view.findViewById(R.id.btnDone);

        if (Utils.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(title);
        }
        txtMessage.setText(message);

        builder.setView(view);

        final AlertDialog alert = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogListener.yesButton();
                alert.dismiss();
            }
        });

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();

    }

    public static void showOKDialog(final Activity mActivity, int title, int message, final IDialogListener iDialogListener) {
        showOKDialog(mActivity, mActivity.getString(title), mActivity.getString(message), iDialogListener);
    }

    public static void showOKDialog(final Activity mActivity, String title, String message, final IDialogListener iDialogListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        CardView view = (CardView) mActivity.getLayoutInflater().inflate(R.layout.dialog_custom, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager sessionManager = new SessionManager(mActivity);
            if (sessionManager.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        final TextView txtTitle = view.findViewById(R.id.txtTitle);
        final TextView txtMessage = view.findViewById(R.id.txtMessage);
        final AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        final AppCompatButton btnDone = view.findViewById(R.id.btnDone);

        if (Utils.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(title);
        }
        txtMessage.setText(message);

        builder.setView(view);

        final AlertDialog alert = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        btnCancel.setVisibility(View.GONE);
        btnDone.setText(R.string.strOK);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogListener.yesButton();
                alert.dismiss();
            }
        });

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();

    }

    public static void updateOnlineStatus(final Context mContext, final String userId, final String status) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_STATUS, status);
            if (status.equalsIgnoreCase(mContext.getString(R.string.strOffline)))
                hashMap.put(EXTRA_LASTSEEN, Utils.getDateTime());
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void updateOfflineStatus(final String userId, final String status) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_STATUS, status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void updateGender(final String userId, final String strGender) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_GENDER, strGender);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void updateUserActive(final String userId) {
        try {
            final DatabaseReference referenceUpdate = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_ACTIVE, FALSE);
            referenceUpdate.updateChildren(hashMap);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void filterPopup(final Activity context, final IFilterListener filterListener) {

        final Screens screens = new Screens(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getText(R.string.strFilterTitle));

        LinearLayout view = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog_search_filter, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager session = new SessionManager(context);
            if (session.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        builder.setView(view);

        final CheckBox mOnlineChk = view.findViewById(R.id.chkOnline);
        final CheckBox mOfflineChk = view.findViewById(R.id.chkOffline);
//        final CheckBox mMaleChk = view.findViewById(R.id.chkMale);
//        final CheckBox mFemaleChk = view.findViewById(R.id.chkFemale);
//        final CheckBox mNotSetChk = view.findViewById(R.id.chkNotSet);
        final AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        final AppCompatButton btnDone = view.findViewById(R.id.btnDone);

        mOnlineChk.setChecked(online);
        mOfflineChk.setChecked(offline);
//        mMaleChk.setChecked(male);
//        mFemaleChk.setChecked(female);
       // mNotSetChk.setChecked(notset);

        final AlertDialog alert = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mOfflineChk.isChecked() && !mOnlineChk.isChecked()) {
                    screens.showToast(context.getString(R.string.msgErrorUserOnline));
                    return;
                }
//                if (!mMaleChk.isChecked() && !mFemaleChk.isChecked() && !mNotSetChk.isChecked()) {
//                    screens.showToast(context.getString(R.string.msgErrorGender));
//                    return;
//                }
                online = mOnlineChk.isChecked();
                offline = mOfflineChk.isChecked();
//                male = mMaleChk.isChecked();
//                female = mFemaleChk.isChecked();
//                notset = mNotSetChk.isChecked();

                filterListener.showFilterUsers();
                alert.dismiss();
            }
        });

        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    public static void chatSendSound(Context context) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd("chat_tone.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }


    public static Query getQuerySortBySearch() {
        return FirebaseDatabase.getInstance().getReference(REF_USERS).orderByChild(EXTRA_SEARCH).startAt("").endAt("" + "\uf8ff");
    }

    public static String getGroupUniqueId() {
        return FirebaseDatabase.getInstance().getReference().child(REF_GROUPS).child("").push().getKey();
    }

    public static int getImageColor(String strName) {
        final ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(strName);
        return color;
    }


    public static String getExtension(Context context, final Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static void readStatus(Context mActivity, String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Utils.updateOnlineStatus(mActivity, firebaseUser.getUid(), status);
        }
    }

    public static void logout(final Activity mActivity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Screens screens = new Screens(mActivity);
                Utils.showYesNoDialog(mActivity, R.string.logout_title, R.string.logout_message, new IDialogListener() {
                    @Override
                    public void yesButton() {
                        Utils.readStatus(mActivity, mActivity.getString(R.string.strOffline));
                        FirebaseAuth.getInstance().signOut();
                        screens.showClearTopScreen(LoginActivity.class);
                    }
                });
            }
        }, CLICK_DELAY_TIME);

    }

    public static int getAppVersionCode(Context context) {
        Integer appVersionDetails = 1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionDetails = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Utils.getErrors(e);
        }
        return appVersionDetails;
    }
}
