package com.tw.go.plugin.setting;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class DefaultPluginConfigurationView implements PluginConfigurationView {

    public static final String PLUGIN_SETTINGS_SERVER_BASE_URL = "server_base_url";
    public static final String PLUGIN_SETTINGS_END_POINT = "end_point";
    public static final String PLUGIN_SETTINGS_USERNAME = "username";
    public static final String PLUGIN_SETTINGS_PASSWORD = "password";
    public static final String PLUGIN_SETTINGS_OAUTH_TOKEN = "oauth_token";
    public static final String PLUGIN_SETTING_RESULT_PREFIX = "result_";
    public static final String PLUGIN_SETTINGS_PASS_AT_END = "pass_at_end";
    public static final String PLUGIN_SETTINGS_GO_API_SERVER_HOST = "go_api_server_host";
    public static final String PLUGIN_SETTINGS_GO_API_USERNAME = "go_api_username";
    public static final String PLUGIN_SETTINGS_GO_API_PASSWORD = "go_api_password";


    public static final String CHECKBOX_TRUE_VALUE = "on";

    @Override
    public String templateName() {
        return "plugin-settings.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_GO_API_SERVER_HOST, createField("Go API Server Host", "", true, false, "0"));
        response.put(PLUGIN_SETTINGS_GO_API_USERNAME, createField("Go API Username", "", true, false, "1"));
        response.put(PLUGIN_SETTINGS_GO_API_PASSWORD, createField("Go API Password", "", true, true, "2"));
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "3"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "4"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, true, false, "5"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, true, true, "6"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "7"));
        response.put(PLUGIN_SETTING_RESULT_PREFIX + "unknown", createField("Result Unknown", CHECKBOX_TRUE_VALUE, false, false, "8"));
        response.put(PLUGIN_SETTING_RESULT_PREFIX + "failed", createField("Result Failed", CHECKBOX_TRUE_VALUE, false, false, "9"));
        response.put(PLUGIN_SETTING_RESULT_PREFIX + "passed", createField("Result Passed", CHECKBOX_TRUE_VALUE, false, false, "10"));
        response.put(PLUGIN_SETTING_RESULT_PREFIX + "cancelled", createField("Result Cancelled", CHECKBOX_TRUE_VALUE, false, false, "11"));
        response.put(PLUGIN_SETTINGS_PASS_AT_END, createField("Pass at end", CHECKBOX_TRUE_VALUE, false, false, "12"));

        return response;
    }
}
