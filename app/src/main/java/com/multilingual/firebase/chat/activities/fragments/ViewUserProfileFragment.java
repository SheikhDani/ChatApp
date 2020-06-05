package com.multilingual.firebase.chat.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.MessageActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_USERS;


public class ViewUserProfileFragment extends BaseFragment {

    private CircleImageView imgAvatar;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private ImageView imgBlurImage;
    private String strDescription = "", strGender = "", strAvatarImg;
    private String viewUserId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_new, container, false);

        final FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        imgBlurImage = view.findViewById(R.id.imgRelativeBlue);
        final TextView txtUsername = view.findViewById(R.id.txtUsername);
        final TextView txtEmail = view.findViewById(R.id.txtEmail);
        final TextView txtAbout = view.findViewById(R.id.txtAbout);
        final TextView txtGender = view.findViewById(R.id.txtGender);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Intent i = getActivity().getIntent();
        final String userId = i.getStringExtra(EXTRA_USER_ID);

        if (Utils.isEmpty(userId)) {
            viewUserId = firebaseUser.getUid();
        } else {
            viewUserId = userId;
        }
        if (viewUserId.equalsIgnoreCase(firebaseUser.getUid())) {
            fabChat.hide();
        } else {
            fabChat.show();
        }

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.openFullImageViewActivity(v, strAvatarImg);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(viewUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    txtUsername.setText(user.getUsername());
                    txtEmail.setText(user.getEmail());
                    strGender = user.getGender();
                    strDescription = user.getAbout();
                    strAvatarImg = user.getImageURL();
                    txtAbout.setText(Utils.isEmpty(strDescription) ? mActivity.getString(R.string.strAboutStatus) : strDescription);
                    txtGender.setText(Utils.isEmpty(strGender) ? mActivity.getString(R.string.strUnspecified) : strGender);

                    Utils.setProfileImage(getContext(), strAvatarImg, imgAvatar);
                    Utils.setProfileBlurImage(getContext(), strAvatarImg, imgBlurImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra(EXTRA_USER_ID, viewUserId);
                mContext.startActivity(intent);
            }
        });


        return view;
    }

}
