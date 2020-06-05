package com.multilingual.firebase.chat.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.multilingual.firebase.chat.activities.adapters.MessageAdapters;
import com.multilingual.firebase.chat.activities.constants.Constants;
import com.multilingual.firebase.chat.activities.fcm.APIService;
import com.multilingual.firebase.chat.activities.fcm.RetroClient;
import com.multilingual.firebase.chat.activities.fcmmodels.Data;
import com.multilingual.firebase.chat.activities.fcmmodels.MyResponse;
import com.multilingual.firebase.chat.activities.fcmmodels.Sender;
import com.multilingual.firebase.chat.activities.fcmmodels.Token;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.Others;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_DATETIME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMGPATH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_MESSAGE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_RECEIVER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SEEN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SENDER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_TYPE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_TYPING;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_TYPINGWITH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_TYPING_DELAY;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_URL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_CHATS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_CHAT_PHOTO_UPLOAD;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_OTHERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_TOKENS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SLASH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TRUE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_IMAGE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_TEXT;

public class MessageActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView mImageView;
    private TextView mTxtUsername, txtTyping;
    private RecyclerView mRecyclerView;
    private String currentId, userId, userName = "Sender", userImage, userGender,translatelangauge;
    private String strSender, strReceiver, strSingle;
    private AppCompatEditText mEditTextChat;
    private FloatingActionButton mButtonChatSend;
    private Toolbar mToolbar;
    private Intent intent;
    private ArrayList<Chat> chats;
    private ArrayList<Chat> newList = new ArrayList<Chat>();
    private MessageAdapters messageAdapters;

    private ValueEventListener seenListenerSender;
    private Query seenReferenceSender;
    private Query referencesinglevalue;

    private APIService apiService;


    ProgressDialog mProgressBar;
    ProgressBar mDialouge;

    private String onlineStatus, strUsername;
    TranslateOptions options;
    Translate translate;

    private ImageView imgAvatar;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayoutManager mManager;
    private String viewUserId = "";

    private static final String API_KEY = "AIzaSyCL9zsTgJdmU4dY8nRdeCNafjv5UoIfL0k";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mActivity = this;
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        mProgressBar = new ProgressDialog(this);
        Constants.notify = false;


        apiService = RetroClient.getClient(FCM_URL).create(APIService.class);
        getTranslateService();
        imgAvatar = findViewById(R.id.imgAvatar);

        mImageView = findViewById(R.id.imageView);
        txtTyping = findViewById(R.id.txtTyping);
        mTxtUsername = findViewById(R.id.txtUsername);
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mManager);

        mEditTextChat = findViewById(R.id.editTextChat);
        mButtonChatSend = findViewById(R.id.buttonChatSend);

        txtTyping.setText("");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    strUsername = user.getUsername();
                    userGender = user.getGender();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);




        strSender = currentId + SLASH + userId;
        strReceiver = userId + SLASH + currentId;
        strSingle = firebaseUser.getUid() + SLASH + userId;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(REF_CHAT_PHOTO_UPLOAD + SLASH + strSender);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final User user = dataSnapshot.getValue(User.class);
                    mTxtUsername.setText(user.getUsername());
                    userName = user.getUsername();
                    //userGender = user.getGender();
                    userImage = user.getImageURL();
                    onlineStatus = user.getStatus();
                    txtTyping.setText(onlineStatus);
                    Utils.setProfileImage(getApplicationContext(), user.getImageURL(), mImageView);
                    if (Constants.notify == false) {
                        readMessages(user.getImageURL());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mButtonChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMessage = mEditTextChat.getText().toString().trim();
                if (TextUtils.isEmpty(txtMessage)) {
                    screens.showToast(getString(R.string.strCantSendMsg));
                } else {
                    sendMessage(TYPE_TEXT, txtMessage);
                }
                mEditTextChat.setText("");
            }
        });

        final LinearLayout viewProfile = findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.openViewProfileActivity(userId);
            }
        });

        imgAvatar.setOnClickListener(this);

        Utils.uploadTypingStatus();
        typingListening();
        readTyping();
        //seenMessage();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAvatar:
                openImageCropper();
                break;
        }
    }

    private void openImageCropper() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this);
    }

    private void sendMessage(final String type, final String message) {
        Constants.notify = true;

        final String sender = currentId;
        final String receiver = userId;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(EXTRA_SENDER, sender);
        hashMap.put(EXTRA_RECEIVER, receiver);
        hashMap.put(EXTRA_MESSAGE, message);
        hashMap.put(EXTRA_TYPE, type);

        if (type.equalsIgnoreCase(TYPE_TEXT)) {

        } else if (type.equalsIgnoreCase(TYPE_IMAGE)) {

            hashMap.put(EXTRA_IMGPATH, message);

        }

        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, Utils.getDateTime());

        reference.child(REF_CHATS).child(strSender).push().setValue(hashMap);
        reference.child(REF_CHATS).child(strReceiver).push().setValue(hashMap);
        //reference.child(REF_CHATS).child(strSingle).push().setValue(hashMap);
        Utils.chatSendSound(getApplicationContext());

        try {
            final String msg = message;
            if (Constants.notify) {
                //lastMessage();
                sendNotification(receiver, strUsername, msg, type);
            }

        } catch (Exception e) {
        }
    }

    private void sendNotification(String receiver, final String username, final String message, final String type) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(REF_TOKENS);
        Query query = tokenRef.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);

                        final Data data = new Data(currentId, R.drawable.ic_stat_ic_notification, username, message, getString(R.string.strNewMessage), userId, type);

                        final Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success != 1) {
                                        //Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void lastMessage() {


        try {
            referencesinglevalue = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver).limitToLast(1);
            referencesinglevalue.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Chat chat = snapshot.getValue(Chat.class);
                            if (userGender.equals("French")) {
                                translatelangauge= "fr";
                            } else if (userGender.equals("English")) {
                                translatelangauge = "en";
                            }
                            else if (userGender.equals("Spanish")) {
                                translatelangauge = "es";
                            }
                            else if (userGender.equals("Arabic")) {
                                translatelangauge = "ar";
                            }
                            else if (userGender.equals("Hindi")) {
                                translatelangauge = "hi";
                            }
                            else if (userGender.equals("Afrikaans")) {
                                translatelangauge = "af";
                            }
                            else if (userGender.equals("Sesotho")) {
                                translatelangauge = "st";
                            }
                            else if (userGender.equals("Zulu")) {
                                translatelangauge = "zu";
                            }
                            else if (userGender.equals("Russian")) {
                                translatelangauge = "ru";
                            }
                            else {
                                translatelangauge = "sw";
                            }
                            Translation translation = translate.translate(chat.getMessage(), Translate.TranslateOption.targetLanguage(translatelangauge));
                            String text = String.valueOf(Html.fromHtml(translation.getTranslatedText()));
                            chat.setMessage(text);
                            assert chat != null;
                            assert firebaseUser != null;
                            try {
                                if (!Utils.isEmpty(chat.getMessage())) {
                                    chats.add(chat);
                                }


                            } catch (Exception e) {
                            }
                        }


                    }

                    try {

                        // messageAdapters = new MessageAdapters(mActivity, chats, userName, userImage);
                        messageAdapters.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(messageAdapters.getItemCount() - 1);


                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {

        }
    }

    private void readMessages(final String imageUrl) {

        final ProgressBar mDialouge = findViewById(R.id.progressBar);
        mDialouge.setVisibility(View.VISIBLE);

        //Constants.notify = false;
        chats = new ArrayList<>();


        //showProgress();
        reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Constants.count++;
                if (Constants.notify == false) {
                    chats.clear();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                Chat chat = snapshot.getValue(Chat.class);
                                if (userGender.equals("French")) {
                                    translatelangauge= "fr";
                                } else if (userGender.equals("English")) {
                                    translatelangauge = "en";
                                }
                                else if (userGender.equals("Spanish")) {
                                    translatelangauge = "es";
                                }
                                else if (userGender.equals("Arabic")) {
                                    translatelangauge = "ar";
                                }
                                else if (userGender.equals("Hindi")) {
                                    translatelangauge = "hi";
                                }
                                else if (userGender.equals("Afrikaans")) {
                                    translatelangauge = "af";
                                }
                                else if (userGender.equals("Sesotho")) {
                                    translatelangauge = "st";
                                }
                                else if (userGender.equals("Zulu")) {
                                    translatelangauge = "zu";
                                }
                                else if (userGender.equals("Russian")) {
                                    translatelangauge = "ru";
                                }
                                else {
                                    translatelangauge = "sw";
                                }
                                Translation translation = translate.translate(chat.getMessage(), Translate.TranslateOption.targetLanguage(translatelangauge));
                                String text = String.valueOf(Html.fromHtml(translation.getTranslatedText()));
                                chat.setMessage(text);
                                if (!Utils.isEmpty(chat.getMessage())) {
                                    chats.add(chat);

                                }

                            } catch (Exception e) {
                            }
                        }

                    }
                    try {
                        messageAdapters = new MessageAdapters(mActivity, chats, userName, imageUrl);
                        mRecyclerView.setAdapter(messageAdapters);
                        mDialouge.setVisibility(View.GONE);
                        Constants.notify = true;
                        // hideProgress();

                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                } else {
                        lastMessage();



                    //Constants.count=0;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void seenMessage() {
        seenReferenceSender = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strSender).orderByChild(EXTRA_SEEN).equalTo(false);
        seenListenerSender = seenReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (!Utils.isEmpty(chat.getMessage())) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(EXTRA_SEEN, TRUE);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_groups, menu);
//        MenuItem itemViewUser = menu.findItem(R.id.itemGroupInfo);
//        MenuItem itemAdd = menu.findItem(R.id.itemAddGroup);
//        MenuItem itemEdit = menu.findItem(R.id.itemEditGroup);
//        MenuItem itemLeave = menu.findItem(R.id.itemLeaveGroup);
//        MenuItem itemDelete = menu.findItem(R.id.itemDeleteGroup);
//        itemAdd.setVisible(false);
//        itemEdit.setVisible(false);
//        itemLeave.setVisible(false);
//        itemDelete.setVisible(false);
//        itemViewUser.setTitle(R.string.strUserInfo);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemGroupInfo:
                screens.openViewProfileActivity(userId);
                break;
            case R.id.itemClearMyChats:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageActivity.this);
                alertDialogBuilder
                        .setTitle(MessageActivity.this.getString(R.string.strDelete))
                        .setMessage(MessageActivity.this.getString(R.string.strDeleteOwnChats))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                //new DeleteChatTask().execute();
                                deleteOwnChats();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        }
                );
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
//                Utils.showYesNoDialog(mActivity, R.string.strDelete, R.string.strDeleteOwnChats, new IDialogListener() {
//                    @Override
//                    public void yesButton() {
//
//                    }
//                });

                break;
        }
        return true;
    }

    /**
     * False means don't close current screen, just delete my own chats
     * True  means close current screen, cause first we leave from group and than delete own chats
     */


    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
        translate = options.getService();

    }

    //    private class DeleteChatTask extends AsyncTask<Void, Void, Void> {
//        private ProgressDialog pDialog;
//
//
//        @Override
//        protected void onPreExecute() {
//            mDialouge.setVisibility(View.VISIBLE);
//
////            pDialog = new ProgressDialog(MessageActivity.this);
////            pDialog.setMessage(getResources().getString(R.string.msg_please_wait));
////            pDialog.setIndeterminate(true);
////            pDialog.setCancelable(false);
////            pDialog.show();
//        }
//        @Override
//        protected Void doInBackground(Void... args) {
//            deleteOwnChats();
//            // do background work here
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void result) {
//            mDialouge.setVisibility(View.GONE);
//            // do UI work here
////            if (pDialog.isShowing()) {
////                pDialog.dismiss();
////            }
//        }
//    }
    private void deleteOwnChats() {
        final ProgressBar mDialouge = findViewById(R.id.progressBar);
        mDialouge.setVisibility(View.VISIBLE);
        Constants.notify = false;
        final Query chatsSender = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strSender).orderByChild(EXTRA_SENDER).equalTo(currentId);
        chatsSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        //showProgress();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            //Constants.check++;
                            if (!Utils.isEmpty(chat.getType())) {
                                if (chat.getType().equalsIgnoreCase(TYPE_IMAGE)) {
                                    StorageReference photoRef = storage.getReferenceFromUrl(chat.getImgPath());
                                    photoRef.delete();
                                }
                            }
                            snapshot.getRef().removeValue();
                        }
                    }
                    mDialouge.setVisibility(View.INVISIBLE);

                    //mDialouge.dismiss();
                    //hideProgress();
                    //chats.clear();
                    //messageAdapters.notifyDataSetChanged();
                    //mRecyclerView.scrollToPosition(messageAdapters.getItemCount() - 1);

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //        mDialouge.setVisibility(View.INVISIBLE);
            }
        });

        final Query chatsReceiver = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver).orderByChild(EXTRA_SENDER).equalTo(currentId);
        chatsReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {

                        // showProgress();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }

                    }

                    mDialouge.setVisibility(View.INVISIBLE);

                    // hideProgress();
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // mDialouge.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void readTyping() {
        reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        Others user = dataSnapshot.getValue(Others.class);
                        if (user.isTyping() && user.getTypingwith().equalsIgnoreCase(userId)) {
                            txtTyping.setText(getString(R.string.strTyping));
                        } else {
                            txtTyping.setText(onlineStatus);
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void typingListening() {
        mEditTextChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() == 0) {
                        stopTyping();
                    } else if (s.length() > 0) {
                        startTyping();
                        idleTyping(s.length());
                    }
                } catch (Exception e) {
                    stopTyping();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void idleTyping(final int currentLen) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int newLen = mEditTextChat.getText().length();
                if (currentLen == newLen) {
                    stopTyping();
                }

            }
        }, EXTRA_TYPING_DELAY);
    }

    private void startTyping() {
        typingStatus(TRUE);
    }

    private void stopTyping() {
        typingStatus(FALSE);
    }

    /**
     * typingStatus - Update typing and userId with db
     * isTyping = True means 'startTyping' method called
     * isTyping = False means 'stopTyping' method called
     */
    private void typingStatus(boolean isTyping) {
        try {
            reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_TYPINGWITH, currentId);
            hashMap.put(EXTRA_TYPING, isTyping);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Utils.getExtension(mActivity, imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return fileReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Uri downloadUri = task.getResult();
                                final String mUrl = downloadUri.toString();
                                sendMessage(TYPE_IMAGE, mUrl);
                            } else {
                                Toast.makeText(mActivity, getString(R.string.msgFailedToUplod), Toast.LENGTH_LONG).show();
                            }
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.getErrors(e);
                    Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(mActivity, "No Image selected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.msgUploadInProgress), Toast.LENGTH_LONG).show();
                } else {
                    uploadImage();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            seenReferenceSender.removeEventListener(seenListenerSender);
            stopTyping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            seenReferenceSender.removeEventListener(seenListenerSender);
            stopTyping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
