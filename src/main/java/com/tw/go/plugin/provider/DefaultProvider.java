package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.ResultParser;

import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;

public abstract class DefaultProvider implements Provider {

    private PluginConfigurationView pluginConfigurationView;

    public DefaultProvider(PluginConfigurationView pluginConfigurationView) {
        this.pluginConfigurationView = pluginConfigurationView;
    }

    @Override
    public PluginConfigurationView configurationView() {
        return pluginConfigurationView;
    }

    @Override
    public PluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        ResultParser resultParser = new ResultParser(PLUGIN_SETTING_RESULT_PREFIX, CHECKBOX_TRUE_VALUE);
        return new DefaultPluginSettings(
                responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_END_POINT),
                responseBodyMap.get(PLUGIN_SETTINGS_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_PASSWORD),
                responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                resultParser.toSet(responseBodyMap),
                CHECKBOX_TRUE_VALUE.equals(responseBodyMap.get(PLUGIN_SETTINGS_PASS_AT_END)),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_PASSWORD),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_SERVER_HOST)
        );
    }
}
