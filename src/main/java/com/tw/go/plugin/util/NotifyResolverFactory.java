package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Pipeline;
import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.jsonapi.ServerFactory;
import com.tw.go.plugin.setting.PluginSettings;

import java.io.IOException;

public class NotifyResolverFactory {

    private ServerFactory serverFactory;

    public NotifyResolverFactory() {
        serverFactory = new ServerFactory();
    }

    public NotifyResolverFactory(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    public NotifyResolver getNotifyRule(PluginSettings settings, String pipelineStage, String counter) throws IOException {
        return new NotifyResolver(settings, isLastStage(settings, pipelineStage, counter));
    }

    boolean isLastStage(PluginSettings settings, String pipelineStage, String counter) throws IOException {
        Server server = serverFactory.getServer(settings);

        String[] stageNameParts = pipelineStage.split("/");

        Pipeline pipelineInstance = server.getPipelineInstance(stageNameParts[0], counter);
        final String lastStageName = pipelineInstance.getLastStageName();
        return lastStageName.equals(pipelineStage);
    }

}
