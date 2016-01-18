package com.tw.go.plugin.setting;

import java.util.Set;

public interface PluginSettings {
    String getServerBaseURL();

    String getEndPoint();

    String getUsername();

    String getPassword();

    String getOauthToken();

    Set<String> getResults();

    boolean shouldNotify(String status);
}
