package com.lwonho92.everchat.data;

import com.google.firebase.database.ServerValue;

/**
 * Created by MY on 2017-02-10.
 */

public class EverChatRoom {
    private String id;
    private String name;
    private String roomPhotoUrl;

    private String text;
    private Long timestamp;

    public EverChatRoom() {}

    public EverChatRoom(String name, String text, String roomPhotoUrl) {
        this.name = name;
        this.text = text;
        this.roomPhotoUrl = roomPhotoUrl;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRoomPhotoUrl() {
        return roomPhotoUrl;
    }
    public void setRoomPhotoUrl(String roomPhotoUrl) {
        this.roomPhotoUrl = roomPhotoUrl;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public java.util.Map<String, String> getTimestamp() { return ServerValue.TIMESTAMP; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public Long getTimestampLong() { return timestamp; }
}
