package com.multilingual.firebase.chat.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.MessageActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;

public class GroupsParticipantsAdapters extends RecyclerView.Adapter<GroupsParticipantsAdapters.ViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;
    private Screens screens;

    public GroupsParticipantsAdapters(Context mContext, ArrayList<User> usersList) {
        this.mContext = mContext;
        this.mUsers = Utils.removeDuplicates(usersList);
        screens = new Screens(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_participants, viewGroup, false);
        return new GroupsParticipantsAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        final String strAbout = user.getAbout();

        viewHolder.txtUsername.setText(user.getUsername());
        Utils.setProfileImage(mContext, user.getImageURL(), viewHolder.imageView);

        viewHolder.txtLastMsg.setVisibility(View.VISIBLE);
        if (Utils.isEmpty(strAbout)) {
            viewHolder.txtLastMsg.setText(mContext.getString(R.string.strAboutStatus));
        } else {
            viewHolder.txtLastMsg.setText(strAbout);
        }

        viewHolder.txtAdmin.setVisibility(View.GONE);

        if (user.isAdmin()) {
            viewHolder.txtAdmin.setVisibility(View.VISIBLE);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.openViewProfileActivity(user.getId());
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(user.getId())) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra(EXTRA_USER_ID, user.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView txtUsername;
        private TextView txtLastMsg;
        private TextView txtAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
            txtAdmin = itemView.findViewById(R.id.txtAdmin);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
