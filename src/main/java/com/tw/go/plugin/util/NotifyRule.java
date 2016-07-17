package com.tw.go.plugin.util;

import com.tw.go.plugin.setting.PluginSettings;

import java.io.IOException;

public class NotifyRule {

    public static final String PASSED_RESULT = "Passed";

    private final PluginSettings pluginSettings;
    private final boolean isLastStage;

    public NotifyRule(PluginSettings pluginSettings, boolean isLastStage) {
        this.pluginSettings = pluginSettings;
        this.isLastStage = isLastStage;
    }

    public boolean shouldNotify(String result) throws IOException {
        if (pluginSettings.shouldNotify(result)) {
            boolean stagePassed = PASSED_RESULT.equalsIgnoreCase(result);
            boolean passPipelineOnlyAtEnd = pluginSettings.isPassAtEnd();

            if (stagePassed && passPipelineOnlyAtEnd) {
                return isLastStage;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

}
