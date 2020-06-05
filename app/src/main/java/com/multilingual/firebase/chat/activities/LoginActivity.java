package com.multilingual.firebase.chat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.managers.Screens;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mTxtEmail;
    private EditText mTxtPassword;
    private Button mBtnSignUp;
    private TextView mTxtNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;

        screens = new Screens(mActivity);

        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtPassword = findViewById(R.id.txtPassword);
        mBtnSignUp = findViewById(R.id.btnSignUp);
        mTxtNewUser = findViewById(R.id.txtNewUser);

        auth = FirebaseAuth.getInstance();

        mBtnSignUp.setOnClickListener(this);
        mTxtNewUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                String strEmail = mTxtEmail.getText().toString().trim();
                String strPassword = mTxtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    screens.showToast(mActivity.getString(R.string.strAllFieldsRequired));
                } else {
                    login(strEmail, strPassword);
                }
                break;
            case R.id.txtNewUser:
                screens.showCustomScreen(RegisterActivity.class);
                break;
        }
    }

    private void login(String email, String password) {
        showProgress();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgress();
                if (task.isSuccessful()) {
                    screens.showClearTopScreen(MainActivity.class);
                } else {
                    screens.showToast(getString(R.string.strInvalidEmailPassword));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                hideProgress();
            }
        });
    }
}
