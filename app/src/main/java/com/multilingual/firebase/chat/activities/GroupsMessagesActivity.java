package com.multilingual.firebase.chat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
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
import com.multilingual.firebase.chat.activities.adapters.GroupsMessageAdapters;
import com.multilingual.firebase.chat.activities.constants.Constants;
import com.multilingual.firebase.chat.activities.constants.IDialogListener;
import com.multilingual.firebase.chat.activities.fcm.APIService;
import com.multilingual.firebase.chat.activities.fcm.RetroClient;
import com.multilingual.firebase.chat.activities.fcmmodels.Data;
import com.multilingual.firebase.chat.activities.fcmmodels.MyResponse;
import com.multilingual.firebase.chat.activities.fcmmodels.Sender;
import com.multilingual.firebase.chat.activities.fcmmodels.Token;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.Groups;
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
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_DATETIME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUPS_IN_BOTH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUP_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMGPATH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_LAST_MSG;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_LAST_TIME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_MESSAGE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_OBJ_GROUP;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_RECEIVER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SEEN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SENDER;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_TYPE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FALSE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.FCM_URL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS_MESSAGES;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS_S;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUP_MEMBERS_S;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUP_PHOTO_UPLOAD;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_TOKENS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REQUEST_EDIT_GROUP;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REQUEST_PARTICIPATE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SLASH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TRUE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TWO;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_IMAGE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_TEXT;

public class GroupsMessagesActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImageView, imgAvatar;
    private TextView txtGroupName, txtTyping;
    private RecyclerView mRecyclerView;
    TranslateOptions options;
    Translate translate;
    private Groups groups;
    private String groupId, groupName = "", userName = "Sender";
    private AppCompatEditText mEditTextChat;
    private FloatingActionButton mButtonChatSend;
    private Toolbar mToolbar;
    private Intent intent;
    private ArrayList<Chat> chats;
    private GroupsMessageAdapters messageAdapters;
    private String  userGender,translatelangauge;

    private Map<String, User> userList;
    private List<String> memberList;
    private APIService apiService;
    boolean notify = false;
    private String strUsername, strGroups;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Query referencesinglevalue;
    private static final String API_KEY = "AIzaSyCL9zsTgJdmU4dY8nRdeCNafjv5UoIfL0k";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_for_group);
        mActivity = this;
        apiService = RetroClient.getClient(FCM_URL).create(APIService.class);
        Constants.grouopnotify=false;
        imgAvatar = findViewById(R.id.imgAvatar);
        mImageView = findViewById(R.id.imageView);
        txtTyping = findViewById(R.id.txtTyping);
        txtGroupName = findViewById(R.id.txtGroupName);
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEditTextChat = findViewById(R.id.editTextChat);
        mButtonChatSend = findViewById(R.id.buttonChatSend);
        getTranslateService();
        txtTyping.setText(getString(R.string.strGroupInfo));

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

        intent = getIntent();
        groups = (Groups) intent.getSerializableExtra(EXTRA_OBJ_GROUP);
        groupId = groups.getId();
        groupName = groups.getGroupName();
        strGroups = new Gson().toJson(groups);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(REF_GROUP_PHOTO_UPLOAD + SLASH + groupId);

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(firebaseUser.getUid());
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

//        try {
//            final String letter = String.valueOf(groups.getGroupName().charAt(0));
//            final TextDrawable drawable = TextDrawable.builder().buildRound(letter, Utils.getImageColor(groups.getGroupName()));
//            mImageView.setImageDrawable(drawable);
//        } catch (Exception e) {
//            Utils.getErrors(e);
//        }

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        readGroupTitle();

        readMessages(groupId);

        mButtonChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMessage = mEditTextChat.getText().toString().trim();
                if (TextUtils.isEmpty(txtMessage)) {
                    screens.showToast(getString(R.string.strEmptyMsg));
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
                screens.openGroupParticipantActivity(groups);
            }
        });

        imgAvatar.setOnClickListener(this);

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

    private void readGroupTitle() {
        groupName = groups.getGroupName();
        txtGroupName.setText(groupName);
        Utils.setGroupImage(groups.getGroupImg(), mImageView);
    }

    private void sendMessage(String type, String message) {
        notify = true;
        Constants.grouopnotify=true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        final String date = Utils.getDateTime();
        hashMap.put(EXTRA_SENDER, firebaseUser.getUid());
        hashMap.put(EXTRA_RECEIVER, groupId);
        hashMap.put(EXTRA_TYPE, type);
        hashMap.put(EXTRA_MESSAGE, message);

        if (type.equalsIgnoreCase(TYPE_TEXT)) {


        } else if (type.equalsIgnoreCase(TYPE_IMAGE)) {

            hashMap.put(EXTRA_IMGPATH, message);

        }

        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, date);

        reference.child(REF_GROUPS_MESSAGES + SLASH + groupId).push().setValue(hashMap);

        HashMap<String, Object> mapGroupLastMsg = new HashMap<>();
        mapGroupLastMsg.put(EXTRA_LAST_MSG, message);
        mapGroupLastMsg.put(EXTRA_TYPE, type);
        mapGroupLastMsg.put(EXTRA_LAST_TIME, date);

        reference.child(REF_GROUPS + SLASH + groupId).updateChildren(mapGroupLastMsg);

        Utils.chatSendSound(getApplicationContext());

        try {
            final String msg = message;

            final List<String> memberList = groups.getMembers();

            memberList.remove(firebaseUser.getUid());//Remove current Logged in ID from list, cause no need to send notification to us.
            if (notify) {
                for (int i = 0; i < memberList.size(); i++) {

                    final String memReceiver = memberList.get(i);

                    sendNotification(memReceiver, strUsername, msg, type);
                }
                notify = false;
            }
        } catch (Exception e) {
        }

    }

    private void sendNotification(final String receiver, final String username, final String message, final String type) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(REF_TOKENS);
        Query query = tokenRef.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);

                        final Data data = new Data(firebaseUser.getUid(), R.drawable.ic_stat_ic_notification, username, message, getString(R.string.strNewGroupMessage), receiver, strGroups, type);

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

    private void readMessages(final String groupId) {
        chats = new ArrayList<>();
        final ProgressBar mDialouge = findViewById(R.id.progressBar);
        mDialouge.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference(REF_GROUPS_MESSAGES + SLASH + groupId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(Constants.grouopnotify==false) {
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
                    mDialouge.setVisibility(View.GONE);
                    Constants.grouopnotify=true;
                    readUsers();
                }
                else {
                    lastMessage();
                    //Constants.count=0;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        userList = new HashMap<>();
        memberList = groups.getMembers();

        Query query = FirebaseDatabase.getInstance().getReference(REF_USERS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (int i = 0; i < memberList.size(); i++) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final User user = snapshot.getValue(User.class);
                            if (memberList.get(i).equalsIgnoreCase(user.getId())) {
                                userList.put(user.getId(), user);
                                break;
                            }
                        }
                    }
                }
                try {
                    messageAdapters = new GroupsMessageAdapters(mActivity, chats, userList);
                    mRecyclerView.setAdapter(messageAdapters);
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
                readGroupMembers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void lastMessage() {


        try {
            referencesinglevalue = FirebaseDatabase.getInstance().getReference(REF_GROUPS_MESSAGES + SLASH + groupId).limitToLast(1);
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
                        userList = new HashMap<>();
                        memberList = groups.getMembers();

                        Query query = FirebaseDatabase.getInstance().getReference(REF_USERS);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userList.clear();
                                if (dataSnapshot.hasChildren()) {
                                    for (int i = 0; i < memberList.size(); i++) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            final User user = snapshot.getValue(User.class);
                                            if (memberList.get(i).equalsIgnoreCase(user.getId())) {
                                                userList.put(user.getId(), user);
                                                break;
                                            }
                                        }
                                    }
                                }
                                try {
                                    messageAdapters.notifyDataSetChanged();
                                    mRecyclerView.scrollToPosition(messageAdapters.getItemCount() - 1);

                                } catch (Exception e) {
                                    Utils.getErrors(e);
                                }
                                readGroupMembers();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        // messageAdapters = new MessageAdapters(mActivity, chats, userName, userImage);



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

    private void readGroupMembers() {
        userList = Utils.sortByUser(userList, FALSE);
        String u = "";
        for (User user : userList.values()) {
            if (!user.getId().equalsIgnoreCase(firebaseUser.getUid())) {
                u = u + (user.getUsername() + ", ");
            }
        }
        txtTyping.setText(u + getString(R.string.strYou));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_groups, menu);
//        MenuItem item = menu.findItem(R.id.itemAddGroup);
//        item.setVisible(false);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemGroupInfo:
                screens.openGroupParticipantActivity(groups);
                break;
            case R.id.itemClearMyChats:

                Utils.showYesNoDialog(mActivity, R.string.strDelete, R.string.strDeleteOwnChats, new IDialogListener() {
                    @Override
                    public void yesButton() {
                        showProgress();
                        deleteOwnChats(FALSE);//False means don't close current screen, just delete my own chats
                    }
                });

                break;

            case R.id.itemEditGroup:
                if (isAdmin()) {
                    final Intent intent = new Intent(mActivity, GroupsAddActivity.class);
                    intent.putExtra(EXTRA_GROUP_ID, groupId);
                    intent.putExtra(EXTRA_OBJ_GROUP, groups);
                    startActivityForResult(intent, REQUEST_EDIT_GROUP);
                } else {
                    screens.showToast(getString(R.string.msgOnlyAdminEdit));
                }

                break;

            case R.id.itemLeaveGroup:

                Utils.showYesNoDialog(mActivity, R.string.strLeave, R.string.strLeaveFromGroup, new IDialogListener() {
                    @Override
                    public void yesButton() {
                        showProgress();
                        if (isAdmin()) {

                            if (groups.getMembers().size() >= TWO) {//Make other Person to Admin for this group cause more than 2 person available.

                                String newAdminId = groups.getMembers().get(1);//Make Admin to next USER.

                                groups.setAdmin(newAdminId);

                                groups.getMembers().remove(firebaseUser.getUid());

                                leaveFromGroup(TRUE);//True means close current screen, cause first we leave from group and than delete own chats

                            } else {//You are alone in this Groups. So Delete group and its DATA.

                                deleteWholeGroupsData(); // In this case only groups have Single User and can delete whole groups data.

                            }

                        } else {

                            List<String> removeId = groups.getMembers();
                            removeId.remove(firebaseUser.getUid());
                            groups.setMembers(removeId);

                            leaveFromGroup(TRUE);//True means close current screen, cause first we leave from group and than delete own chats

                        }
                    }
                });


                break;

            case R.id.itemDeleteGroup:
                if (isAdmin()) {
                    Utils.showYesNoDialog(mActivity, R.string.strDelete, R.string.strDeleteWholeGroup, new IDialogListener() {
                        @Override
                        public void yesButton() {
                            showProgress();
                            deleteWholeGroupsData();// Delete whole /groups, /messagesGroups and /membersJoined for that groups
                        }
                    });
                } else {
                    screens.showToast(getString(R.string.msgOnlyAdminDelete));
                }
                break;
        }
        return true;
    }

    private void leaveFromGroup(final boolean isFinishActivity) {
        //Remove from Main Group info /groupId/members/<removeId>
        FirebaseDatabase.getInstance().getReference().child(REF_GROUPS_S + groupId).setValue(groups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //Remove from MembersGroup/groupsIn/<groupId>
                FirebaseDatabase.getInstance().getReference().child(REF_GROUP_MEMBERS_S + firebaseUser.getUid() + EXTRA_GROUPS_IN_BOTH + groupId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgress();
                                deleteOwnChats(isFinishActivity);//True means close current screen, cause first we leave from group and than delete own chats
                            }
                        });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
            }
        });
    }

    private void deleteWholeGroupsData() {
        final int members = groups.getMembers().size();
        FirebaseDatabase.getInstance().getReference().child(REF_GROUPS_S + groupId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (int i = 0; i < members; i++) {
                    FirebaseDatabase.getInstance().getReference().child(REF_GROUP_MEMBERS_S + groups.getMembers().get(i) + EXTRA_GROUPS_IN_BOTH + groupId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    if (i == (members - 1)) {
                        hideProgress();
                        finish();
                    }
                }
                FirebaseDatabase.getInstance().getReference().child(REF_GROUPS_MESSAGES + SLASH + groupId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
    }

    /**
     * False means don't close current screen, just delete my own chats
     * True  means close current screen, cause first we leave from group and than delete own chats
     */
    private void deleteOwnChats(final boolean isFinishActivity) {
        FirebaseDatabase.getInstance().getReference().child(REF_GROUPS_MESSAGES + SLASH + groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat.getSender().equalsIgnoreCase(firebaseUser.getUid())) {

                                if (!Utils.isEmpty(chat.getType())) {
                                    if (chat.getType().equalsIgnoreCase(TYPE_IMAGE)) {
                                        StorageReference photoRef = storage.getReferenceFromUrl(chat.getImgPath());
                                        photoRef.delete();
                                    }
                                }
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                } catch (Exception e) {
                }
                hideProgress();
                if (isFinishActivity) {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "_" + System.currentTimeMillis() + "." + Utils.getExtension(mActivity, imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUrl = downloadUri.toString();
                        if (!Utils.isEmpty(groupId)) {
                            sendMessage(TYPE_IMAGE, mUrl);
                        }
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

        if (requestCode == REQUEST_PARTICIPATE && resultCode == RESULT_FIRST_USER && data != null) {
            finish();
        }
        if (requestCode == REQUEST_EDIT_GROUP && resultCode == RESULT_OK && data != null) {
            groups = (Groups) data.getSerializableExtra(EXTRA_OBJ_GROUP);
            refreshGroupsData();
        }

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
    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
        translate = options.getService();

    }

    private void refreshGroupsData() {
        readGroupTitle();
        readUsers();
    }

    private boolean isAdmin() {
        if (groups.getAdmin().equalsIgnoreCase(firebaseUser.getUid())) {
            return TRUE;
        }
        return FALSE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
