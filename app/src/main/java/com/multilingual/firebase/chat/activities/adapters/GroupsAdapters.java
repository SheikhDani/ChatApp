package com.multilingual.firebase.chat.activities.adapters;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.multilingual.firebase.chat.activities.GroupsMessagesActivity;
import com.multilingual.firebase.chat.activities.R;
import com.multilingual.firebase.chat.activities.managers.Screens;
import com.multilingual.firebase.chat.activities.managers.Utils;
import com.multilingual.firebase.chat.activities.models.Groups;

import java.util.ArrayList;

import static com.multilingual.firebase.chat.activities.constants.IConstants.EXTRA_OBJ_GROUP;
import static com.multilingual.firebase.chat.activities.constants.IConstants.TYPE_IMAGE;

public class GroupsAdapters extends RecyclerView.Adapter<GroupsAdapters.ViewHolder> {

    private Context mContext;
    private ArrayList<Groups> mGroups;
    private Screens screens;
    TranslateOptions options;
    String gender,translatelangauge;
    private FirebaseUser firebaseUser;
    Translate translate;
    private static final String API_KEY = "AIzaSyCL9zsTgJdmU4dY8nRdeCNafjv5UoIfL0k";

    public GroupsAdapters(Context mContext, ArrayList<Groups> groupsList,String gender) {
        this.mContext = mContext;
        this.mGroups = groupsList;
        this.gender = gender;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        screens = new Screens(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_groups, viewGroup, false);
        return new GroupsAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Groups groups = mGroups.get(i);

        viewHolder.txtGroupName.setText(groups.getGroupName());
        getTranslateService();

        try {
//            final String letter = String.valueOf(groups.getGroupName().charAt(0));
//            final TextDrawable drawable = TextDrawable.builder().buildRound(letter, Utils.getImageColor(groups.getGroupName()));
//            viewHolder.imageView.setImageDrawable(drawable);
            Utils.setGroupImage(groups.getGroupImg(), viewHolder.imageView);
        } catch (Exception e) {
        }

       // viewHolder.txtLastMsg.setVisibility(View.VISIBLE);
        viewHolder.txtLastDate.setVisibility(View.VISIBLE);
        viewHolder.imgPhoto.setVisibility(View.GONE);
        try {
            if (Utils.isEmpty(groups.getType())) {
                if (Utils.isEmpty(groups.getLastMsg())) {
                    viewHolder.txtLastMsg.setText(mContext.getString(R.string.msgTapToStartChat));
                } else {
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
                    Translation translation = translate.translate(groups.getLastMsg(), Translate.TranslateOption.targetLanguage(translatelangauge));
                    String text = String.valueOf(Html.fromHtml(translation.getTranslatedText()));
                   // lastMessage(user.getId(), viewHolder.txtLastMsg, viewHolder.txtLastDate, viewHolder.imgPhoto);
                    viewHolder.txtLastMsg.setText(text);
                   //viewHolder.txtLastMsg.setVisibility(View.GONE);
                }
            } else {
                if (groups.getType().equalsIgnoreCase(TYPE_IMAGE)) {
                    viewHolder.imgPhoto.setVisibility(View.VISIBLE);
                    viewHolder.txtLastMsg.setText(mContext.getString(R.string.lblPhoto));
                } else {
                    if (Utils.isEmpty(groups.getLastMsg())) {
                        viewHolder.txtLastMsg.setText(mContext.getString(R.string.msgTapToStartChat));
                    } else {
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
                        else if (gender.equals("Tswana")) {
                            translatelangauge = "tn";
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
                        Translation translation = translate.translate(groups.getLastMsg(), Translate.TranslateOption.targetLanguage(translatelangauge));
                        String text = String.valueOf(Html.fromHtml(translation.getTranslatedText()));
                        viewHolder.txtLastMsg.setText(text);

                    }
                }
            }
            if (Utils.isEmpty(groups.getLastMsgTime())) {
                viewHolder.txtLastDate.setText("");
            } else {
                viewHolder.txtLastDate.setText(Utils.formatDateTime(mContext, groups.getLastMsgTime()));
            }
        } catch (Exception e) {

        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screens.openGroupParticipantActivity(groups);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupsMessagesActivity.class);
                intent.putExtra(EXTRA_OBJ_GROUP, groups);
                mContext.startActivity(intent);
            }
        });

    }


    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
        translate = options.getService();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView txtGroupName;
        private TextView txtLastMsg;
        private TextView txtLastDate;
        private ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            txtGroupName = itemView.findViewById(R.id.txtGroupName);
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
            txtLastDate = itemView.findViewById(R.id.txtLastDate);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }
}
