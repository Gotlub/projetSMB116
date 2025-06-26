package com.smb116.project.model;

import com.google.gson.annotations.SerializedName;

public class NContact {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public NContact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
