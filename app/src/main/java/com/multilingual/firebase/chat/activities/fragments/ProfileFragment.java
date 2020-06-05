package com.multilingual.firebase.chat.activities.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.multilingual.firebase.chat.activities.LoginActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.SharedPrefferenceHelper;
import com.multilingual.firebase.chat.activities.managers.SessionManager;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.Groups;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_ABOUT;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_ADMIN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUPS_IN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUPS_IN_BOTH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUP_MEMBERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMAGEURL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_CHATS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUPS_MESSAGES;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_GROUP_MEMBERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_OTHERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_TOKENS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_UPLOAD;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SLASH;


public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView imgAvatar;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ImageView imgEditAbout;
    private ImageView imgEditGender;
    private Button btnDeleteAccount;
    SharedPrefferenceHelper sHelper;
    private Uri imageUri;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private String strDescription = "", strGender = "";
    private String strReEmail = "", strRePassword = "";
    private String viewUserId = "", currentId;
    private List<String> userList = new ArrayList<>();
    private List<String> groupAdminList = new ArrayList<>();
    private List<String> groupAdminMemberList = new ArrayList<>();
    private List<String> groupOthersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        final TextView txtUsername = view.findViewById(R.id.txtUsername);
        final TextView txtEmail = view.findViewById(R.id.txtEmail);
        final TextView txtAbout = view.findViewById(R.id.txtAbout);
        final TextView txtGender = view.findViewById(R.id.txtGender);
        imgEditAbout = view.findViewById(R.id.imgEdit);
        imgEditGender = view.findViewById(R.id.imgEditGender);
        final RelativeLayout layoutAbout = view.findViewById(R.id.layoutAbout);
        final RelativeLayout layoutGender = view.findViewById(R.id.layoutGender);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        sHelper = new SharedPrefferenceHelper(getActivity());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(REF_UPLOAD);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentId = firebaseUser.getUid();

        final Intent i = getActivity().getIntent();
        final String userId = i.getStringExtra(EXTRA_USER_ID);

        if (Utils.isEmpty(userId)) {//
            viewUserId = firebaseUser.getUid();
            showHideViews(View.VISIBLE);
            imgAvatar.setOnClickListener(this);
            layoutAbout.setOnClickListener(this);
            layoutGender.setOnClickListener(this);
            btnDeleteAccount.setOnClickListener(this);
        } else {
            viewUserId = userId;
            showHideViews(View.GONE);
        }

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(viewUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    txtUsername.setText(user.getUsername());
                    strReEmail = user.getEmail();
                    strRePassword = user.getPassword();
                    txtEmail.setText(strReEmail);
                    strGender = user.getGender();
                    strDescription = user.getAbout();
                    txtAbout.setText(Utils.isEmpty(strDescription) ? mActivity.getString(R.string.strAboutStatus) : strDescription);
                    txtGender.setText(Utils.isEmpty(strGender) ? mActivity.getString(R.string.strUnspecified) : strGender);

                    Utils.setProfileImage(getContext(), user.getImageURL(), imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void showHideViews(int isShow) {
        imgEditAbout.setVisibility(isShow);
        imgEditGender.setVisibility(isShow);
       // btnDeleteAccount.setVisibility(isShow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgAvatar:
                openImageCropper();
                break;

            case R.id.layoutAbout:
                popupForAbout();
                break;

            case R.id.layoutGender:
                Utils.selectGenderPopup(mActivity, firebaseUser.getUid(), strGender,sHelper);
                break;

            case R.id.btnDeleteAccount:
                openAuthenticatePopup();
                break;
        }
    }

    private void openAuthenticatePopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CardView view = (CardView) mActivity.getLayoutInflater().inflate(R.layout.dialog_reauthenticate, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager sessionManager = new SessionManager(getContext());
            if (sessionManager.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        builder.setView(view);

        final TextView txtEmail = view.findViewById(R.id.txtEmail);
        final TextView txtPassword = view.findViewById(R.id.txtPassword);
        final Button btnSignup = view.findViewById(R.id.btnSignUp);
        final Button btnCancel = view.findViewById(R.id.btnCancel);

        final AlertDialog alert = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strEmail = txtEmail.getText().toString().trim();
                final String strPassword = txtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    screens.showToast(mActivity.getString(R.string.strAllFieldsRequired));
                } else if (!strEmail.equalsIgnoreCase(strReEmail) || !strPassword.equalsIgnoreCase(strRePassword)) {
                    screens.showToast(mActivity.getString(R.string.strInvalidEmailPassword));
                } else {
                    alert.dismiss();
                    deleteChatsAndOtherData();
                }


            }
        });

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    private void deleteInsideUsersChat() {
        for (int i = 0; i < userList.size(); i++) {
            final String key = userList.get(i);
            Query query = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(key);
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getKey().equalsIgnoreCase(currentId)) {
                                    snapshot.getRef().removeValue();
                                    break;
                                }
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
    }

    private void deleteChatsAndOtherData() {
        showProgress();
        userList.clear();
        groupAdminList.clear();
        groupAdminMemberList.clear();
        groupOthersList.clear();

        final Query chats = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(currentId);
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            userList.add(snapshot.getKey());

                            snapshot.getRef().removeValue();

                        }

                        deleteInsideUsersChat();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deleteGroupData();

    }

    //***************************************************************************************************************************
    //************************************************** GROUPS ADMIN DELETE - START **************************************************
    //***************************************************************************************************************************
    private void deleteGroupData() {

        //Delete first our groups, where we are admin
        final Query groupsAdmin = FirebaseDatabase.getInstance().getReference(REF_GROUPS).orderByChild(EXTRA_ADMIN).equalTo(currentId);
        groupsAdmin.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        try {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Groups grp = snapshot.getValue(Groups.class);

                                groupAdminList.add(grp.getId());

                                groupAdminMemberList.addAll(grp.getMembers());

                                snapshot.getRef().removeValue();// Delete our admin group
                            }
                        } catch (Exception e) {
                        }

                        //Remove group messages where we are Group Admin, Whole Group can be deleted
                        for (int i = 0; i < groupAdminList.size(); i++) {
                            String keyGroupId = null;
                            try {
                                keyGroupId = groupAdminList.get(i);

                                final Query groupsAdminMessages = FirebaseDatabase.getInstance().getReference(REF_GROUPS_MESSAGES).child(keyGroupId);

                                groupsAdminMessages.getRef().removeValue();// Delete our created Group All Messages

                            } catch (Exception e) {
                            }

                            try {
                                for (int j = 0; j < groupAdminMemberList.size(); j++) {
                                    String keyMem = groupAdminMemberList.get(j);

                                    final Query groupsAdminGroupsIn = FirebaseDatabase.getInstance().getReference(REF_GROUP_MEMBERS).child(keyMem + EXTRA_GROUPS_IN_BOTH + keyGroupId);

                                    groupsAdminGroupsIn.getRef().removeValue();//Remove Group Id from Other member's groupIn, So they are not part of our group because we're deleted

                                }
                            } catch (Exception e) {
                            }
                        }
                        deleteOtherGroupData();
                    } else {
                        deleteOtherGroupData();
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

//        Utils.updateUserActive(firebaseUser.getUid());
    }

    /**
     * ===================================================================================================
     * =========================== START OTHER GROUPS DATA AND DELETE IF FOUND ===========================
     * ===================================================================================================
     */
    private void deleteOtherGroupData() {

        //Delete myself from Other groups where added by thier other users.
        final Query groupsAdminGroupsIn = FirebaseDatabase.getInstance().getReference(REF_GROUP_MEMBERS).child(currentId + SLASH + EXTRA_GROUPS_IN);
        groupsAdminGroupsIn.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            groupOthersList.add(snapshot.getKey());
                            snapshot.getRef().removeValue();//Remove from other Group created by other user.
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    //Delete other groups from where I did chat with other guys, So I only deleted my message from that groups.
                    for (int i = 0; i < groupOthersList.size(); i++) {
                        String grpOtherMsg = groupOthersList.get(i);

                        final Query groupsOtherMsg = FirebaseDatabase.getInstance().getReference(REF_GROUPS_MESSAGES).child(grpOtherMsg);

                        groupsOtherMsg.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            Chat chat = snapshot.getValue(Chat.class);

                                            if (chat.getSender().equalsIgnoreCase(currentId)) {

                                                snapshot.getRef().removeValue();//Delete mine message only from Other groups

                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        //Delete from Other Groups info where I added inside the 'members' attribute. So delete myself from their.
                        final Query groupsOther = FirebaseDatabase.getInstance().getReference(REF_GROUPS).child(grpOtherMsg);
                        groupsOther.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        Groups groups = dataSnapshot.getValue(Groups.class);
                                        List list = groups.getMembers();

                                        list.remove(currentId);//Delete our Id to remove from that group and update it list

                                        HashMap<String, Object> hashMap = new HashMap<>();

                                        hashMap.put(EXTRA_GROUP_MEMBERS, list);

                                        dataSnapshot.getRef().updateChildren(hashMap);// Delete/Update myself from members in other groups.
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                } catch (Exception e) {
                }

                deleteTokenOtherData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //***************************************************************************************************************************
    //*************************************************** TOKENS DELETE - END ***************************************************
    //***************************************************************************************************************************
    private void deleteTokenOtherData() {

        final Query tokens = FirebaseDatabase.getInstance().getReference(REF_TOKENS).orderByKey().equalTo(firebaseUser.getUid());
        tokens.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            snapshot.getRef().removeValue();
                        }
                    }
                } catch (Exception e) {
                }

                final Query others = FirebaseDatabase.getInstance().getReference(REF_OTHERS).orderByKey().equalTo(firebaseUser.getUid());
                others.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    snapshot.getRef().removeValue();
                                }
                            }
                        } catch (Exception e) {
                        }
                        deActivateAccount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deActivateAccount() {
        //Getting the user instance.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //You need to get here the token you saved at logging-in time.
            String token = null;

            AuthCredential credential;

            //This means you didn't have the token because user used like Facebook Sign-in method.
            if (token == null) {
                credential = EmailAuthProvider.getCredential(strReEmail, strRePassword);
            } else {
                //Doesn't matter if it was Facebook Sign-in or others. It will always work using GoogleAuthProvider for whatever the provider.
                //credential = GoogleAuthProvider.getCredential(token, null);
                return;
            }


            //We have to reauthenticate user because we don't know how long
            //it was the sign-in. Calling reauthenticate, will update the
            //user login and prevent FirebaseException (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Calling delete to remove the user and wait for a result.

                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Ok, user remove
                                        try {
                                            hideProgress();
                                            final Query reference3 = FirebaseDatabase.getInstance().getReference(REF_USERS).orderByKey().equalTo(user.getUid());
                                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            snapshot.getRef().removeValue();
                                                            screens.showClearTopScreen(LoginActivity.class);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            Utils.getErrors(e);
                                        }
                                    } else {
                                        //Handle the exception
                                        task.getException();
                                        Utils.getErrors(task.getException());
                                    }
                                }
                            });
                        }
                    });
        }
    }

    public void popupForAbout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getText(R.string.strEnterAbout));

        CardView view = (CardView) getLayoutInflater().inflate(R.layout.dialog_description, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SessionManager sessionManager = new SessionManager(getContext());
            if (sessionManager.isRTLOn()) {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        final AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        final AppCompatButton btnDone = view.findViewById(R.id.btnDone);

        builder.setView(view);

        final EditText txtAbout = view.findViewById(R.id.txtAbout);
        txtAbout.setText(strDescription);

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
                try {
                    final String strAbout = txtAbout.getText().toString().trim();

                    if (Utils.isEmpty(strAbout)) {
                        screens.showToast(getString(R.string.msgErrorEnterDesc));
                        Toast.makeText(getContext(), getString(R.string.msgErrorEnterDesc), Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(EXTRA_ABOUT, strAbout);
                        reference.updateChildren(hashMap);
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }

                } catch (Exception e) {
                    Utils.getErrors(e);
                }
                alert.dismiss();
            }
        });

        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    private void openImageCropper() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                .start(getContext(), this);
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Utils.getExtension(getContext(), imageUri));
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
                                Uri downloadUri = task.getResult();
                                String mUrl = downloadUri.toString();

                                reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(firebaseUser.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(EXTRA_IMAGEURL, mUrl);
                                reference.updateChildren(hashMap);

                            } else {
                                Toast.makeText(getContext(), getString(R.string.msgFailedToUplod), Toast.LENGTH_LONG).show();
                            }
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.getErrors(e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Image selected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), mActivity.getString(R.string.msgUploadInProgress), Toast.LENGTH_LONG).show();
                } else {
                    uploadImage();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
