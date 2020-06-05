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
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_TEXT;

public class GroupsMessageAdapters extends RecyclerView.Adapter<GroupsMessageAdapters.ViewHolder> {

    private final int MSG_TYPE_RIGHT = 0;
    private final int MSG_TYPE_LEFT = 1;

    private Context mContext;
    private ArrayList<Chat> mChats;
    private FirebaseUser firebaseUser;
    private Map<String, User> userList;

    public GroupsMessageAdapters(Context mContext, ArrayList<Chat> usersList, final Map<String, User> userList) {
        this.mContext = mContext;
        this.mChats = usersList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_chat_right, viewGroup, false);
            return new GroupsMessageAdapters.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_chat_left, viewGroup, false);
            return new GroupsMessageAdapters.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Chat chat = mChats.get(position);

        if (Utils.isEmpty(chat.getType())) {
            viewHolder.txtShowMessage.setVisibility(View.VISIBLE);
            viewHolder.imgPath.setVisibility(View.GONE);
            viewHolder.txtShowMessage.setText(chat.getMessage());
        } else {
            if (chat.getType().equalsIgnoreCase(TYPE_TEXT)) {
                viewHolder.txtShowMessage.setVisibility(View.VISIBLE);
                viewHolder.imgPath.setVisibility(View.GONE);
                viewHolder.txtShowMessage.setText(chat.getMessage());
            } else {
                viewHolder.txtShowMessage.setVisibility(View.GONE);
                viewHolder.imgPath.setVisibility(View.VISIBLE);
                Utils.setChatImage(chat.getImgPath(), viewHolder.imgPath);
                viewHolder.imgPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Screens screens = new Screens(mContext);
                        screens.openFullImageViewActivity(v, chat.getImgPath());
                    }
                });
            }
        }

        viewHolder.txtOnlyDate.setVisibility(View.GONE);
        try {
            final long first = Utils.dateToMillis(mChats.get(position - 1).getDatetime());
            final long second = Utils.dateToMillis(chat.getDatetime());
            if (!Utils.hasSameDate(first, second)) {
                viewHolder.txtOnlyDate.setVisibility(View.VISIBLE);
                viewHolder.txtOnlyDate.setText(Utils.formatFullDate(chat.getDatetime()));
            }
        } catch (Exception e) {
            if (position == 0) {
                viewHolder.txtOnlyDate.setVisibility(View.VISIBLE);
                viewHolder.txtOnlyDate.setText(Utils.formatFullDate(chat.getDatetime()));
            }
        }

        final User user = userList.get(chat.getSender());

        switch (viewHolder.getItemViewType()) {
            case MSG_TYPE_LEFT:
                try {

                    viewHolder.txtName.setText(user.getUsername());
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
                break;
            case MSG_TYPE_RIGHT:
                break;
        }

        long timeMilliSeconds = 0;
        try {
            timeMilliSeconds = Utils.dateToMillis(chat.getDatetime());
        } catch (Exception e) {
        }

        if (timeMilliSeconds > 0) {
            viewHolder.txtMsgTime.setText(Utils.formatLocalTime(timeMilliSeconds));
        } else {
            viewHolder.txtMsgTime.setVisibility(View.GONE);
        }

        viewHolder.imgMsgSeen.setVisibility(View.GONE);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView chatImageView;
        public TextView txtName;
        public TextView txtShowMessage;
        public TextView txtMsgTime;
        public ImageView imgMsgSeen;
        public TextView txtOnlyDate;
        public ImageView imgPath;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOnlyDate = itemView.findViewById(R.id.txtOnlyDate);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            txtShowMessage = itemView.findViewById(R.id.txtShowMessage);
            txtName = itemView.findViewById(R.id.txtName);
            imgMsgSeen = itemView.findViewById(R.id.imgMsgSeen);
            txtMsgTime = itemView.findViewById(R.id.txtMsgTime);
            imgPath = itemView.findViewById(R.id.imgPath);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Chat chat = mChats.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chat.getSender().equalsIgnoreCase(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
