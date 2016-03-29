package com.tw.go.plugin.jsonapi;

import com.tw.go.plugin.setting.PluginSettings;

public class ServerFactory {

    public Server getServer(PluginSettings settings) {
        return new Server(settings);
    }
}
