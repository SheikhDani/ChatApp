package com.multilingual.firebase.chat.activities.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.multilingual.firebase.chat.activities.MessageActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.constants.IDialogListener;
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Chat;
import com.multilingual.firebase.chat.activities.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_SEEN;
import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.multilingual.firebase.chat.activities.constants.IConstants.REF_CHATS;
import static com.multilingual.firebase.chat.activities.constants.IConstants.SLASH;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_TEXT;

public class UserAdapters extends RecyclerView.Adapter<UserAdapters.ViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;
    private boolean isChat;
    private FirebaseUser firebaseUser;
    private String theLastMsg, txtLastDate;
    private boolean isMsgSeen = false;
    private int unReadCount = 0;
    private Screens screens;
    TranslateOptions options;
    String gender,translatelangauge;
    Translate translate;
    private static final String API_KEY = "AIzaSyCL9zsTgJdmU4dY8nRdeCNafjv5UoIfL0k";

    public UserAdapters(Context mContext, ArrayList<User> usersList, boolean isChat,String gender) {
        this.mContext = mContext;
        this.mUsers = Utils.removeDuplicates(usersList);
        this.isChat = isChat;
        this.gender = gender;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        screens = new Screens(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_users, viewGroup, false);
        return new UserAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        final String strAbout = user.getAbout();

        viewHolder.txtUsername.setText(user.getUsername());
        Utils.setProfileImage(mContext, user.getImageURL(), viewHolder.imageView);

        //viewHolder.txtLastMsg.setVisibility(View.VISIBLE);
        viewHolder.imgPhoto.setVisibility(View.GONE);
        if (isChat) {
            viewHolder.txtUnreadCounter.setVisibility(View.INVISIBLE);
            getTranslateService();
            lastMessage(user.getId(), viewHolder.txtLastMsg, viewHolder.txtLastDate, viewHolder.imgPhoto);
            //lastMessageCount(user.getId(), viewHolder.txtUnreadCounter);
            viewHolder.txtLastDate.setVisibility(View.VISIBLE);
        } else {
           // viewHolder.txtUnreadCounter.setVisibility(View.GONE);
            viewHolder.txtLastDate.setVisibility(View.GONE);

            if (Utils.isEmpty(strAbout)) {
                viewHolder.txtLastMsg.setText(mContext.getString(R.string.strAboutStatus));
            } else {
                viewHolder.txtLastMsg.setText(strAbout);
            }
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
                Intent intent = new Intent(mContext, MessageActivity.class);

                intent.putExtra(EXTRA_USER_ID, user.getId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isChat) {
                    Utils.setVibrate(mContext);
                    final String receiverId = user.getId();
                    final String currentUser = firebaseUser.getUid();

                    Utils.showYesNoDialog(((Activity) mContext), R.string.strDelete, R.string.strDeleteConversion, new IDialogListener() {
                        @Override
                        public void yesButton() {
                            Query queryCurrent = FirebaseDatabase.getInstance().getReference().child(REF_CHATS).child(currentUser + SLASH + receiverId);
                            queryCurrent.getRef().removeValue();
                            Query queryReceiver = FirebaseDatabase.getInstance().getReference().child(REF_CHATS).child(receiverId + SLASH + currentUser);
                            queryReceiver.getRef().removeValue();
                        }
                    });

                } else {
                }
                return true;
            }
        });
    }

    private void lastMessage(final String userId, final TextView lastMsg, final TextView lastDate, final ImageView imgPath) {

        theLastMsg = "default";
        txtLastDate = "Now";
        try {
            Query reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(firebaseUser.getUid() + SLASH + userId).limitToLast(1);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Chat chat = snapshot.getValue(Chat.class);

                            if (gender.equals("French")) {
                                translatelangauge= "fr";
                            } else if (gender.equals("English")) {
                                translatelangauge = "en";
                            }
                            else if (gender.equals("Spanish")) {
                                translatelangauge = "es";
                            }
                            else if (gender.equals("Arabic")) {
                                translatelangauge = "ar";
                            }
                            else if (gender.equals("Hindi")) {
                                translatelangauge = "hi";
                            }
                            else if (gender.equals("Afrikaans")) {
                                translatelangauge = "af";
                            }
                            else if (gender.equals("Sesotho")) {
                                translatelangauge = "st";
                            }
                            else if (gender.equals("Zulu")) {
                                translatelangauge = "zu";
                            }
                            else if (gender.equals("Russian")) {
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
                                if (Utils.isEmpty(chat.getType())) {
                                    if (!Utils.isEmpty(chat.getMessage())) {
                                        theLastMsg = chat.getMessage();
                                        txtLastDate = chat.getDatetime();
                                    }
                                } else {
                                    if (chat.getType().equalsIgnoreCase(TYPE_TEXT)) {
                                        imgPath.setVisibility(View.GONE);
                                        theLastMsg = chat.getMessage();
                                        txtLastDate = chat.getDatetime();
                                    } else {
                                        theLastMsg = mContext.getString(R.string.lblPhoto);
                                        txtLastDate = chat.getDatetime();
                                        imgPath.setVisibility(View.VISIBLE);
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                        switch (theLastMsg) {
                            case "default":
                                lastMsg.setText("No message!");
                                lastDate.setText("");
                                break;
                            default:
                                lastMsg.setText(theLastMsg);
                                try {
                                    lastDate.setText(Utils.formatDateTime(mContext, txtLastDate));
                                } catch (Exception e) {
                                }
                                break;
                        }

                        theLastMsg = "default";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {

        }
    }
    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
        translate = options.getService();

    }

    private void lastMessageCount(final String userId, final TextView txtUnreadCounter) {

        isMsgSeen = false;
        unReadCount = 0;

        try {
            Query reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(firebaseUser.getUid() + SLASH + userId).orderByChild(EXTRA_SEEN).equalTo(false);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            assert firebaseUser != null;
                            try {
                                if (!Utils.isEmpty(chat.getMessage())) {
                                    if (chat.getSender().equalsIgnoreCase(firebaseUser.getUid())) {
                                        isMsgSeen = true;
                                    } else {
                                        isMsgSeen = chat.isMsgseen();
                                        if (!isMsgSeen) {
                                            unReadCount++;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
//                    if (isMsgSeen || unReadCount == ZERO) {
//                       // txtUnreadCounter.setVisibility(View.INVISIBLE);
//                    } else {
//                        final String readCount = unReadCount > 99 ? "99+" : String.valueOf(unReadCount);
//                        txtUnreadCounter.setVisibility(View.VISIBLE);
//                        txtUnreadCounter.setText(readCount);
//                    }
                    //unReadCount = 0;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView txtUsername;
        private ImageView imgBGWhite;
        private ImageView imgOn;
        private ImageView imgOff;
        private TextView txtLastMsg;
        private TextView txtLastDate;
        private TextView txtUnreadCounter;
        private ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgBGWhite = itemView.findViewById(R.id.imgBGWhite);
            imgOn = itemView.findViewById(R.id.imgOn);
            imgOff = itemView.findViewById(R.id.imgOff);
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
            txtLastDate = itemView.findViewById(R.id.txtLastDate);
            txtUnreadCounter = itemView.findViewById(R.id.txtUnreadCounter);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
