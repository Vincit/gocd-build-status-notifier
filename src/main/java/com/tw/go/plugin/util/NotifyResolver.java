package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Pipeline;
import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.setting.PluginSettings;

import java.io.IOException;

public class NotifyResolver {

    public static final String PASSED_RESULT = "Passed";

    private Server server;
    private PluginSettings pluginSettings;

    public NotifyResolver(Server server, PluginSettings pluginSettings) {
        this.server = server;
        this.pluginSettings = pluginSettings;
    }

    public boolean shouldNotify(String pipelineStage, String pipelineCounter, String result) throws IOException {
        if (pluginSettings.shouldNotify(result)) {
            boolean stagePassed = PASSED_RESULT.equalsIgnoreCase(result);
            boolean passPipelineOnlyAtEnd = pluginSettings.isPassAtEnd();
            boolean isLastStage = isLastStage(pipelineStage, pipelineCounter);

            if (stagePassed && passPipelineOnlyAtEnd) {
                return isLastStage;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    boolean isLastStage(String currentStageName, String counter) throws IOException {
        String[] stageNameParts = currentStageName.split("/");

        Pipeline pipelineInstance = server.getPipelineInstance(stageNameParts[0], counter);
        final String lastStageName = pipelineInstance.getLastStageName();
        return lastStageName.equals(currentStageName);
    }
}
