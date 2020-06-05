package com.multilingual.firebase.chat.activities.models;


public class Setting {

    private int force_version_code; //This must be integer and updated versionCode from build.gradle
    private boolean force_update; //1= Yes , 0=No
    private String force_title;
    private String force_message;
    private String force_yes_button;
    private String force_no_button;
    private String force_source;//Google Playstore OR Live Server APK URL
    private String force_apk_link;
    private int max_group_size = 10;
    private String max_group_msg = "";

    public int getForce_version_code() {
        return force_version_code;
    }

    public void setForce_version_code(int force_version_code) {
        this.force_version_code = force_version_code;
    }

    public boolean isForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public String getForce_title() {
        return force_title;
    }

    public void setForce_title(String force_title) {
        this.force_title = force_title;
    }

    public String getForce_message() {
        return force_message;
    }

    public void setForce_message(String force_message) {
        this.force_message = force_message;
    }

    public String getForce_yes_button() {
        return force_yes_button;
    }

    public void setForce_yes_button(String force_yes_button) {
        this.force_yes_button = force_yes_button;
    }

    public String getForce_no_button() {
        return force_no_button;
    }

    public void setForce_no_button(String force_no_button) {
        this.force_no_button = force_no_button;
    }

    public String getForce_source() {
        return force_source;
    }

    public void setForce_source(String force_source) {
        this.force_source = force_source;
    }

    public String getForce_apk_link() {
        return force_apk_link;
    }

    public void setForce_apk_link(String force_apk_link) {
        this.force_apk_link = force_apk_link;
    }

    public int getMax_group_size() {
        return max_group_size;
    }

    public void setMax_group_size(int max_group_size) {
        this.max_group_size = max_group_size;
    }

    public String getMax_group_msg() {
        return max_group_msg;
    }

    public void setMax_group_msg(String max_group_msg) {
        this.max_group_msg = max_group_msg;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "force_version_code=" + force_version_code +
                ", force_update=" + force_update +
                ", force_title='" + force_title + '\'' +
                ", force_message='" + force_message + '\'' +
                ", force_yes_button='" + force_yes_button + '\'' +
                ", force_no_button='" + force_no_button + '\'' +
                ", force_source='" + force_source + '\'' +
                ", force_apk_link='" + force_apk_link + '\'' +
                ", max_group_size='" + max_group_size + '\'' +
                ", max_group_msg='" + max_group_msg+ '\'' +
                '}';
    }
}