package com.multilingual.firebase.chat.activities.constants;

public interface IConstants {
    boolean ADS_SHOWN = true;

    String SLASH = "/";
    String REF_USERS = "Users";
    //    String REF_CHATS = "Chats";
    String REF_CHATS = "Chats_v2";
    String REF_UPLOAD = "Uploads";
    String REF_GROUP_UPLOAD = "GroupUploads";
    String REF_GROUP_PHOTO_UPLOAD = "GroupPhotos";
    String REF_CHAT_PHOTO_UPLOAD = "ChatPhotos";
    String REF_TOKENS = "Tokens";
    String REF_OTHERS = "Others";

    String REF_GROUPS = "Groups";
    String REF_GROUPS_S = REF_GROUPS + SLASH;
    String REF_GROUP_MEMBERS = "MembersGroups";
    String REF_GROUP_MEMBERS_S = REF_GROUP_MEMBERS + SLASH;
    String REF_GROUPS_MESSAGES = "GroupsMessages";

    String IMG_DEFAULTS = "default";

    String EXTRA_USER_ID = "userId";

    String EXTRA_SENDER = "sender";
    String EXTRA_RECEIVER = "receiver";
    String EXTRA_MESSAGE = "message";
    String EXTRA_TYPE = "type";
    String EXTRA_IMGPATH = "imgPath";
    String EXTRA_DATETIME = "datetime";
    String EXTRA_SEEN = "msgseen";
    String EXTRA_STATUS = "status";
    String EXTRA_SEARCH = "search";
    String EXTRA_VERSION = "version";

    String EXTRA_ID = "id";
    String EXTRA_EMAIL = "email";
    String EXTRA_USERNAME = "username";
    String EXTRA_PASSWORD = "password";
    String EXTRA_IMAGEURL = "imageURL";
    String EXTRA_ACTIVE = "active";
    String EXTRA_TYPING = "typing";
    String EXTRA_TYPINGWITH = "typingwith";
    String EXTRA_LINK = "linkPath";
    String EXTRA_ABOUT = "about";
    String EXTRA_GENDER = "gender";
    String EXTRA_LASTSEEN = "lastSeen";
    String EXTRA_GROUPS_IN = "groupsIn";
    String EXTRA_GROUPS_IN_BOTH = SLASH + EXTRA_GROUPS_IN + SLASH;

    String EXTRA_GROUP_ID = "groupId";
    String EXTRA_GROUP_NAME = "name";
    String EXTRA_ADMIN = "admin";
    String EXTRA_GROUP_MEMBERS = "members";
    String EXTRA_GROUP_IMG = "groupImg";
    String EXTRA_LAST_MSG = "lastMsg";
    String EXTRA_LAST_TIME = "lastMsgTime";
    String EXTRA_CREATED_AT = "createdAt";

    String EXTRA_OBJ_GROUP = "groupObject";

    boolean TRUE = true;
    boolean FALSE = false;

    String FCM_ICON = "icon";
    String FCM_USER = "user";
    String FCM_SENT = "sent";
    String FCM_TITLE = "title";
    String FCM_BODY = "body";
    String FCM_GROUPS = "groups";
    String FCM_USERNAME = "username";
    String FCM_TYPE = "type";

    String FCM_URL = "https://fcm.googleapis.com/";

    long CLICK_DELAY_TIME = 250;
    int EXTRA_TYPING_DELAY = 800;
    int EXTRA_DELAY = 1000;
    int ZERO = 0; // Don't change
    int ONE = 1; // Don't change
    int TWO = 2; //Don't edit this
    int THREE = 2; //Minimum groups member

    int REQUEST_EDIT_GROUP = 1357;
    int REQUEST_PARTICIPATE = 1487;
    int THRE = 3;
    int FOUR = 4;
    int FIVE = 5;
    int SIX = 6;
    int SEVEN= 7;
    int EIGHT= 8;
    int NINE= 9;



    String TYPE_TEXT = "TEXT";
    String TYPE_IMAGE = "IMAGE";


    String PATH_ABOUT_US = "about_us.html";
    String PATH_PRIVACY_POLICY = "privacy_policy.html";
    String DEFAULT_UPDATE_URL = "https://play.google.com/store/apps/details?id=";
    String DEFAULT_UPDATE_URL_2 = "market://details?id=";
}
