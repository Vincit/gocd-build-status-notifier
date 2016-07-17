package com.tw.go.plugin;

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.provider.github.GitHubProvider;
import com.tw.go.plugin.provider.stash.StashProvider;
import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.JSONUtils;
import com.tw.go.plugin.util.NotifyRule;
import com.tw.go.plugin.util.NotifyRuleResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.BuildStatusNotifierPlugin.PLUGIN_SETTINGS_GET_CONFIGURATION;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTING_RESULT_PREFIX;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BuildStatusNotifierPluginTest {
    @Mock
    private GoApplicationAccessor goApplicationAccessor;
    @Mock
    private Provider provider;
    @Mock
    private NotifyRuleResolver notifyRuleResolver;
    @Mock
    private NotifyRule notifyRule;

    private BuildStatusNotifierPlugin plugin;

    @Before
    public void setUp() throws IOException {
        initMocks(this);

        plugin = new BuildStatusNotifierPlugin();

        DefaultGoApiResponse pluginSettingsResponse = new DefaultGoApiResponse(200);
        Map<String, String> pluginSettings = new HashMap<String, String>();
        pluginSettings.put(PLUGIN_SETTING_RESULT_PREFIX + "cancelled", "on");
        pluginSettings.put(PLUGIN_SETTING_RESULT_PREFIX + "passed", "on");
        pluginSettingsResponse.setResponseBody(JSONUtils.toJSON(pluginSettings));
        when(goApplicationAccessor.submit(any(GoApiRequest.class))).thenReturn(pluginSettingsResponse);
        when(provider.pluginId()).thenReturn(GitHubProvider.PLUGIN_ID);
        when(provider.pollerPluginId()).thenReturn(GitHubProvider.GITHUB_PR_POLLER_PLUGIN_ID);

        when(notifyRule.shouldNotify(anyString())).thenReturn(true);
        when(notifyRuleResolver.getNotifyRule(any(PluginSettings.class), anyString(), anyString())).thenReturn(notifyRule);

        plugin.initializeGoApplicationAccessor(goApplicationAccessor);
        plugin.setProvider(provider);
        plugin.setNotifyRuleResolver(notifyRuleResolver);
    }

    @Test
    public void shouldRegisterForStageStatusChange() {
        assertThat(plugin.handleNotificationsInterestedIn().responseBody(), is("{\"notifications\":[\"stage-status\"]}"));
    }

    @Test
    public void shouldDelegateUpdateStatusToProviderWithCorrectParameters() throws Exception {
        PluginSettings mockSettings = mock(PluginSettings.class);
        when(plugin.getPluginSettings()).thenReturn(mockSettings);
        when(mockSettings.shouldNotify(anyString())).thenReturn(true);
        when(mockSettings.isPassAtEnd()).thenReturn(true);

        String expectedURL = "url";
        String expectedUsername = "username";
        String expectedRevision = "sha-1";
        String expectedPRId = "1";
        String pipelineName = "pipeline";
        String pipelineCounter = "1";
        String stageName = "stage";
        String stageCounter = "1";
        String expectedPipelineStage = String.format("%s/%s", pipelineName, stageName);
        String expectedPipelineInstance = String.format("%s/%s/%s/%s", pipelineName, pipelineCounter, stageName, stageCounter);
        String stageResult = "Passed";
        String expectedStageResult = "Passed";

        Map requestBody = createRequestBodyMap(expectedURL, expectedUsername, expectedRevision, expectedPRId, pipelineName, pipelineCounter, stageName, stageCounter, stageResult);
        plugin.handleStageNotification(createGoPluginAPIRequest(requestBody));

        verify(provider).updateStatus(eq(expectedURL), any(PluginSettings.class), eq("1"), eq(expectedRevision), eq(expectedPipelineStage), eq(expectedStageResult), eq("http://localhost:8153/go/pipelines/" + expectedPipelineInstance));
    }

    @Test
    public void shouldReturnPluginSettings() throws Exception {
        Provider mockProvider = mock(Provider.class);
        PluginConfigurationView mockConfigView = mock(PluginConfigurationView.class);
        when(mockProvider.configurationView()).thenReturn(mockConfigView);
        Map<String, Object> fields = new HashMap<String, Object>();
        when(mockConfigView.fields()).thenReturn(fields);

        plugin.setProvider(mockProvider);

        Map<String, Object> configuration = new Gson().fromJson(
                plugin.handle(createRequest(PLUGIN_SETTINGS_GET_CONFIGURATION)
        ).responseBody(), Map.class);

        assertThat(configuration, is(fields));
    }

    @Test
    public void shouldReturnCorrectConfigForStashPlugin() throws Exception {
        plugin.setProvider(new StashProvider());

        Map<String, Object> configuration = new Gson().fromJson(
                plugin.handle(createRequest(PLUGIN_SETTINGS_GET_CONFIGURATION)
        ).responseBody(), Map.class);

        assertThat(configuration.containsKey("server_base_url"), is(true));
        assertThat(configuration.containsKey("end_point"), is(true));
        assertThat(configuration.containsKey("username"), is(true));
        assertThat(configuration.containsKey("password"), is(true));
        assertThat(configuration.containsKey("oauth_token"), is(true));
        assertThat(configuration.containsKey("review_label"), is(false));
    }

    private GoPluginApiRequest createRequest(final String name) {
        return new GoPluginApiRequest() {
            @Override
            public String extension() {
                return null;
            }

            @Override
            public String extensionVersion() {
                return null;
            }

            @Override
            public String requestName() {
                return name;
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return null;
            }
        };
    }

    @Test
    public void shouldNotifyOnlyWhenCorrectStatus() throws Exception {
        String expectedURL = "url";
        String expectedUsername = "username";
        String expectedRevision = "sha-1";
        String expectedPRId = "1";
        String pipelineName = "pipeline";
        String pipelineCounter = "1";
        String stageName = "stage";
        String stageCounter = "1";
        String result = "failed";

        when(notifyRule.shouldNotify(result)).thenReturn(false);

        Map requestBody = createRequestBodyMap(expectedURL, expectedUsername, expectedRevision, expectedPRId, pipelineName, pipelineCounter, stageName, stageCounter, result);
        plugin.handleStageNotification(createGoPluginAPIRequest(requestBody));

        verify(provider, never()).updateStatus(anyString(), any(PluginSettings.class), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private Map createRequestBodyMap(String url, String username, String revision, String prId, String pipelineName, String pipelineCounter, String stageName, String stageCounter, String stageResult) {
        Map materialRevisionMap = new HashMap();
        Map materialMap = new HashMap();
        materialMap.put("type", "scm");
        materialMap.put("plugin-id", "github.pr");
        Map configurationMap = new HashMap();
        configurationMap.put("url", url);
        configurationMap.put("username", username);
        materialMap.put("scm-configuration", configurationMap);
        materialRevisionMap.put("material", materialMap);

        List modifications = new ArrayList();
        Map modificationMap = new HashMap();
        modificationMap.put("revision", revision);
        Map modificationDataMap = new HashMap();
        modificationDataMap.put("PR_ID", prId);
        modificationMap.put("data", modificationDataMap);
        modifications.add(modificationMap);
        materialRevisionMap.put("modifications", modifications);

        Map pipelineMap = new HashMap();
        List buildCause = new ArrayList();
        buildCause.add(materialRevisionMap);
        pipelineMap.put("build-cause", buildCause);

        Map stageMap = new HashMap();
        stageMap.put("name", stageName);
        stageMap.put("counter", stageCounter);
        stageMap.put("result", stageResult);
        pipelineMap.put("stage", stageMap);

        pipelineMap.put("name", pipelineName);
        pipelineMap.put("counter", pipelineCounter);

        Map requestBody = new HashMap();
        requestBody.put("pipeline", pipelineMap);
        return requestBody;
    }

    private DefaultGoPluginApiRequest createGoPluginAPIRequest(Map requestBody) {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(BuildStatusNotifierPlugin.EXTENSION_NAME, "1.0", BuildStatusNotifierPlugin.REQUEST_STAGE_STATUS);
        request.setRequestBody(JSONUtils.toJSON(requestBody));
        return request;
    }
}
