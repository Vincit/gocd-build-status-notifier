package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.setting.PluginSettings;

public class NotifyResolverFactory {

    public NotifyResolver getResolver(PluginSettings settings) {
        return new NotifyResolver(new Server(settings), settings);
    }

}
