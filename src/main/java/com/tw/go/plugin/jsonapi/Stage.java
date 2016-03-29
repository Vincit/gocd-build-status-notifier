package com.tw.go.plugin.jsonapi;

import com.google.gson.annotations.SerializedName;

public class Stage {

    @SerializedName("name")
    public String name;

    public Stage() {
    }

    public Stage(String name) {
        this.name = name;
    }
}
