package com.tw.go.plugin.jsonapi;

import com.google.gson.annotations.SerializedName;

public class Pipeline {

    @SerializedName("name")
    public String name;

    @SerializedName("counter")
    public String counter;

    @SerializedName("stages")
    public Stage[] stages;


    public String getLastStageName() {
        return String.format("%s/%s", name, stages[stages.length - 1].name);
    }
}
