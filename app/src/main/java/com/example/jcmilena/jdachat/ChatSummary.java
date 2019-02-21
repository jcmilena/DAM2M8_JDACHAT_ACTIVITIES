package com.example.jcmilena.jdachat;

public class ChatSummary {

    String name;
    String touid;
    int isallreaded;


    public ChatSummary() {
    }


    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsallreaded() {
        return isallreaded;
    }

    public void setIsallreaded(int isallreaded) {
        this.isallreaded = isallreaded;
    }
}
