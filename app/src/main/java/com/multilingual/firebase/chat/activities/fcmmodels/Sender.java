package com.multilingual.firebase.chat.activities.fcmmodels;

public class Sender {
    private Data data;
    private String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
