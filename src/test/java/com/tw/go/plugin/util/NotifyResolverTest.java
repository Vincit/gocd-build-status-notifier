package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Pipeline;
import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.jsonapi.Stage;
import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.setting.PluginSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class NotifyResolverTest {

    public static final String PASSED_STATE = "Passed";
    public static final String FAILED_STATE = "Failed";
    public static final String CANCELLED_STATE = "Cancelled";

    public static final String PIPELINE_NAME = "pipeline";
    public static final String COUNTER = "1";
    public static final String FIRST_STAGE_NAME = "first-stage";
    public static final String LAST_STAGE_NAME = "last-stage";


    @Mock
    private Server server;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldNotify_OnlyStage_Passed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithOneStage();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, LAST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, PASSED_STATE), is(true));
    }

    private Pipeline getPipelineWithOneStage() {
        return pipelineWithStages(PIPELINE_NAME, COUNTER, LAST_STAGE_NAME);
    }

    @Test
    public void shouldNotify_LastStage_Passed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, LAST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, PASSED_STATE), is(true));
    }

    private Pipeline getPipelineWithManyStages() {
        return pipelineWithStages(PIPELINE_NAME, COUNTER, FIRST_STAGE_NAME, LAST_STAGE_NAME);
    }

    @Test
    public void shouldNotify_NotLastStage_Passed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, FIRST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, PASSED_STATE), is(false));
    }

    @Test
    public void shouldNotify_NotLastStage_Passed_NotifyAlways() throws Exception {
        final PluginSettings settings = getSettingsNotifyAlways();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, FIRST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, PASSED_STATE), is(true));
    }

    @Test
    public void shouldNotify_NotLastStage_Failed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, FIRST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, FAILED_STATE), is(true));
    }

    @Test
    public void shouldNotify_LastStage_Failed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, LAST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, FAILED_STATE), is(true));
    }

    @Test
    public void shouldNotify_NotificationTypeTurnedOff() throws Exception {
        final PluginSettings settings = getSettingsNotifyOnlyFailed();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithOneStage();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, LAST_STAGE_NAME);
        assertThat(notifyResolver.shouldNotify(pipelineStage, COUNTER, PASSED_STATE), is(false));
    }

    @Test
    public void isLastStage_isLast() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, LAST_STAGE_NAME);
        assertThat(notifyResolver.isLastStage(pipelineStage, COUNTER), is(true));
    }


    @Test
    public void isLastStage_NotLast() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(server, settings);

        final Pipeline pipeline = getPipelineWithManyStages();

        when(server.getPipelineInstance(PIPELINE_NAME, COUNTER)).thenReturn(pipeline);

        final String pipelineStage = getPipelineStage(PIPELINE_NAME, FIRST_STAGE_NAME);
        assertThat(notifyResolver.isLastStage(pipelineStage, COUNTER), is(false));
    }

    private String getPipelineStage(String pipelineName, String stageName) {
        return String.format("%s/%s", pipelineName, stageName);
    }

    private Pipeline pipelineWithStages(String pipelineName, String counter, String... stageNames) {
        Pipeline pipeline = new Pipeline();
        pipeline.name = pipelineName;
        pipeline.counter = counter;

        pipeline.stages = new Stage[stageNames.length];
        for (int i = 0; i < stageNames.length; ++i) {
            pipeline.stages[i] = new Stage(stageNames[i]);
        }

        return pipeline;
    }

    private PluginSettings getSettingsNotifyPassedAtEnd() {
        DefaultPluginSettings settings = new DefaultPluginSettings();
        settings.setEndPoint("http://localhost:8153");
        settings.setPassAtEnd(true);

        settings.setResults(withResults(PASSED_STATE, FAILED_STATE, CANCELLED_STATE));

        return settings;
    }

    private PluginSettings getSettingsNotifyAlways() {
        DefaultPluginSettings settings = new DefaultPluginSettings();
        settings.setEndPoint("http://localhost:8153");
        settings.setPassAtEnd(false);

        settings.setResults(withResults(PASSED_STATE, FAILED_STATE, CANCELLED_STATE));

        return settings;
    }

    private PluginSettings getSettingsNotifyOnlyFailed() {
        DefaultPluginSettings settings = new DefaultPluginSettings();
        settings.setEndPoint("http://localhost:8153");
        settings.setPassAtEnd(true);

        settings.setResults(withResults(FAILED_STATE));

        return settings;
    }

    private Set<String> withResults(String... resultNames) {
        Set<String> results = new HashSet<String>();
        for (String result : resultNames) {
            results.add(result.toUpperCase());
        }
        return results;
    }

}