package com.example.bindingconcept.models;

import java.lang.reflect.Member;

public class MembersInfo {
    public String name;
    public int id;
    public String image;
    public boolean hasReadMessage;

    public MembersInfo(String name, int id, String image, boolean hasReadMessage) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.hasReadMessage = hasReadMessage;
    }

    public MembersInfo(){

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public boolean isHasReadMessage() {
        return hasReadMessage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setHasReadMessage(boolean hasReadMessage) {
        this.hasReadMessage = hasReadMessage;
    }
}
