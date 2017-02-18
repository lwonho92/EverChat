package com.lwonho92.everchat.datas;

/**
 * Created by MY on 2017-02-10.
 */

public class EverChatRoom {
    private String id;
    private String name;
    private String text;

    public EverChatRoom() {}

    public EverChatRoom(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }
}
