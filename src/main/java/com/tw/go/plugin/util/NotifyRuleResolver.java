package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Pipeline;
import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.jsonapi.ServerFactory;
import com.tw.go.plugin.setting.PluginSettings;

import java.io.IOException;

public class NotifyRuleResolver {

    private ServerFactory serverFactory;

    public NotifyRuleResolver() {
        serverFactory = new ServerFactory();
    }

    public NotifyRuleResolver(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    public NotifyRule getNotifyRule(PluginSettings settings, String pipelineStage, String counter) throws IOException {
        return new NotifyRule(settings, isLastStage(settings, pipelineStage, counter));
    }

    boolean isLastStage(PluginSettings settings, String pipelineStage, String counter) throws IOException {
        Server server = serverFactory.getServer(settings);

        String[] stageNameParts = pipelineStage.split("/");

        Pipeline pipelineInstance = server.getPipelineInstance(stageNameParts[0], counter);
        final String lastStageName = pipelineInstance.getLastStageName();
        return lastStageName.equals(pipelineStage);
    }

}
