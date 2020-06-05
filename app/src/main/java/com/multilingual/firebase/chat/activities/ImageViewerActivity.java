package com.multilingual.firebase.chat.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.multilingual.firebase.chat.activities.managers.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_GROUP_NAME;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_IMGPATH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.IMG_DEFAULTS;

public class ImageViewerActivity extends AppCompatActivity {

    int placeholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_image_fullscreen);
        final String imgPath = extras.getString(EXTRA_IMGPATH);
        final Uri imageUri = Uri.parse(imgPath);
        final String groupName = extras.getString(EXTRA_GROUP_NAME, "");

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        PhotoView imageViewZoom = findViewById(R.id.imgPath);

        if (!Utils.isEmpty(groupName) && imgPath.equalsIgnoreCase(IMG_DEFAULTS)) {
            placeholder = R.drawable.img_group_default_orange;
        } else {
            placeholder = R.drawable.profile_avatar;
        }

        try {
            Picasso.get()
                    .load(imageUri)
                    .placeholder(placeholder)
                    .into(imageViewZoom);
        } catch (Exception e) {

        }

    }

}
