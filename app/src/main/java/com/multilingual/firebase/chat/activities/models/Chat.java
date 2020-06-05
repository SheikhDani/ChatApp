package com.multilingual.firebase.chat.activities.models;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean msgseen;
    private String datetime;
    private String type; //IMAGE
    private String imgPath; //Full Image Path

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isMsgseen() {
        return msgseen;
    }

    public void setMsgseen(boolean msgseen) {
        this.msgseen = msgseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", msgseen=" + msgseen +
                ", datetime='" + datetime + '\'' +
                ", type='" + type + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
