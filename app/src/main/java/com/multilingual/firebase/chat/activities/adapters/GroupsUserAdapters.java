package com.multilingual.firebase.chat.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.constants.IGroupListener;
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Groups;
import com.multilingual.firebase.chat.activities.models.User;
import com.multilingual.firebase.chat.activities.views.smoothcb.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupsUserAdapters extends RecyclerView.Adapter<GroupsUserAdapters.ViewHolder> {

    private Context mContext;
    private Screens screens;
    private ArrayList<User> mUsers;
    private ArrayList<User> mSelectedUsers;
    private List<String> mSelectedMembersId;
    private IGroupListener groupListener;
    private boolean isEditGroup;
    private Groups groups;
    private Set<String> mDeletedMembersId;

    public GroupsUserAdapters(Context mContext, ArrayList<User> usersList, ArrayList<User> mSelectedUsers, List<String> mSelectedMembersId, final Set<String> mDeletedMembersId, final boolean isEditGroup, final Groups groups, IGroupListener groupListener) {
        this.mContext = mContext;
        this.mUsers = Utils.removeDuplicates(usersList);
        this.mSelectedUsers = mSelectedUsers;
        this.mSelectedMembersId = mSelectedMembersId;
        this.mDeletedMembersId = mDeletedMembersId;
        this.groupListener = groupListener;
        screens = new Screens(mContext);

        this.isEditGroup = isEditGroup;
        this.groups = groups;

        if (isEditGroup) {
            for (int i = 0; i < mUsers.size(); i++) {
                if (groups.getMembers().contains(mUsers.get(i).getId())) {
                    this.mSelectedUsers.add(mUsers.get(i));
                    this.mSelectedMembersId.add(mUsers.get(i).getId());
                }
            }
            groupListener.setSubTitle();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_users_group, viewGroup, false);
        return new GroupsUserAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);
        final String strAbout = user.getAbout();

        viewHolder.txtUsername.setText(user.getUsername());
        Utils.setProfileImage(mContext, user.getImageURL(), viewHolder.imageView);

        viewHolder.txtLastMsg.setVisibility(View.VISIBLE);

        if (Utils.isEmpty(strAbout)) {
            viewHolder.txtLastMsg.setText(mContext.getString(R.string.strAboutStatus));
        } else {
            viewHolder.txtLastMsg.setText(strAbout);
        }

        if (user.getStatus().equalsIgnoreCase(mContext.getString(R.string.strOnline))) {
            viewHolder.imgOn.setVisibility(View.VISIBLE);
            viewHolder.imgOff.setVisibility(View.GONE);
        } else {
            viewHolder.imgOn.setVisibility(View.GONE);
            viewHolder.imgOff.setVisibility(View.VISIBLE);
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
                user.setChecked(!user.isChecked());
                viewHolder.cb.setChecked(user.isChecked(), true);
                if (user.isChecked()) {
                    mSelectedUsers.add(user);
                    mSelectedMembersId.add(user.getId());
                    mDeletedMembersId.remove(user.getId());
                } else {
                    mSelectedUsers.remove(user);
                    mSelectedMembersId.remove(user.getId());
                    mDeletedMembersId.add(user.getId());
                }
                groupListener.setSubTitle();
            }
        });

        viewHolder.cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                user.setChecked(isChecked);
            }
        });

        if (isEditGroup) {
            if (groups.getMembers().contains(user.getId())) {
                viewHolder.cb.setChecked(true);
            } else {
                viewHolder.cb.setChecked(false);
            }
        } else {
            viewHolder.cb.setChecked(user.isChecked());
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView txtUsername;
        private ImageView imgOn;
        private ImageView imgOff;
        private TextView txtLastMsg;
        private SmoothCheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgOn = itemView.findViewById(R.id.imgOn);
            imgOff = itemView.findViewById(R.id.imgOff);
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
            cb = itemView.findViewById(R.id.scb);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
