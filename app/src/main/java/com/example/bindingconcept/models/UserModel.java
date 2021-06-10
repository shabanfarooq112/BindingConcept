package com.example.bindingconcept.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    int id;
    String name;



    public UserModel(){

    }


    public UserModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
