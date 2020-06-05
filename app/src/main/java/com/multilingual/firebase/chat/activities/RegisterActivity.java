package com.multilingual.firebase.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.managers.Utils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_ACTIVE;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_EMAIL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMAGEURL;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_PASSWORD;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SEARCH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_STATUS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USERNAME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_VERSION;
import static com.multilingual.firebase.chat.activities.constants.IConstants.IMG_DEFAULTS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private View mRelativeBlue;
    private EditText mTxtEmail;
    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private Button mBtnRegister;
    private TextView mTxtExistingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRelativeBlue = findViewById(R.id.relativeBlue);
        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtUsername = findViewById(R.id.txtUsername);
        mTxtPassword = findViewById(R.id.txtPassword);
        mBtnRegister = findViewById(R.id.btnRegister);
        mTxtExistingUser = findViewById(R.id.txtExistingUser);

        mBtnRegister.setOnClickListener(this);
        mTxtExistingUser.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                String strEmail = mTxtEmail.getText().toString().trim();
                String strUsername = mTxtUsername.getText().toString().trim();
                String strPassword = mTxtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strPassword)) {
                    screens.showToast(getString(R.string.strAllFieldsRequired));
                } else {
                    register(strEmail, strUsername, strPassword);
                }
                break;
            case R.id.txtExistingUser:
                finish();
                break;
        }
    }

    private void register(final String email, final String username, final String password) {
        showProgress();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(EXTRA_ID, userId);
                    hashMap.put(EXTRA_EMAIL, email);
                    hashMap.put(EXTRA_USERNAME, Utils.getCapsWord(username));
                    hashMap.put(EXTRA_PASSWORD, password);
                    hashMap.put(EXTRA_IMAGEURL, IMG_DEFAULTS);
                    hashMap.put(EXTRA_ACTIVE, true);
                    hashMap.put(EXTRA_STATUS, getString(R.string.strOnline));
                    hashMap.put(EXTRA_SEARCH, username.toLowerCase().trim());
                    hashMap.put(EXTRA_VERSION, BuildConfig.VERSION_NAME);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgress();
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                screens.showToast(e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                hideProgress();
            }
        });
    }
}
