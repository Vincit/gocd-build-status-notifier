package com.tw.go.plugin.provider.gerrit;

import com.tw.go.plugin.provider.DefaultProvider;
import com.tw.go.plugin.provider.gerrit.response.ResponseParser;
import com.tw.go.plugin.provider.gerrit.response.model.CommitDetails;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.provider.gerrit.GerritConfigurationView.PLUGIN_SETTINGS_REVIEW_LABEL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static com.tw.go.plugin.util.ValidationUtils.getValidationError;

public class GerritProvider extends DefaultProvider {

    public static final String PLUGIN_ID = "gerrit.cs.status";
    public static final String GERRIT_CS_POLLER_PLUGIN_ID = "gerrit.cs";

    public static final int IN_PROGRESS_VALUE = 0;
    public static final int SUCCESS_VALUE = 1;
    public static final int FAILURE_VALUE = -1;

    private final HTTPClient httpClient;

    public GerritProvider() {
        super(new GerritConfigurationView());
        httpClient = new HTTPClient();
    }

    public GerritProvider(HTTPClient httpClient) {
        super(new GerritConfigurationView());
        this.httpClient = httpClient;
    }

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GERRIT_CS_POLLER_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineInstance,
                                     String result, String trackbackURL) throws Exception {
        GerritPluginSettings settings = (GerritPluginSettings) pluginSettings;

        String endPointToUse = settings.getEndPoint();
        String usernameToUse = settings.getUsername();
        String passwordToUse = settings.getPassword();
        String codeReviewLabel = settings.getReviewLabel();

        if (StringUtils.isEmpty(endPointToUse)) {
            endPointToUse = System.getProperty("go.plugin.build.status.gerrit.endpoint");
        }
        if (StringUtils.isEmpty(usernameToUse)) {
            usernameToUse = System.getProperty("go.plugin.build.status.gerrit.username");
        }
        if (StringUtils.isEmpty(passwordToUse)) {
            passwordToUse = System.getProperty("go.plugin.build.status.gerrit.password");
        }
        if (StringUtils.isEmpty(codeReviewLabel)) {
            codeReviewLabel = System.getProperty("go.plugin.build.status.gerrit.codeReviewLabel");
        }

        String commitDetailsURL = String.format("%s/a/changes/?q=commit:%s", endPointToUse, revision);
        String commitDetailsResponse = httpClient.getRequest(commitDetailsURL, AuthenticationType.DIGEST, usernameToUse, passwordToUse);
        CommitDetails commitDetails = new ResponseParser().parseCommitDetails(commitDetailsResponse);

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("message", String.format("%s: %s", pipelineInstance, trackbackURL));
        Map<String, Object> labels = new HashMap<String, Object>();
        request.put("labels", labels);
        labels.put(codeReviewLabel, getCodeReviewValue(result));
        String updateStatusURL = String.format("%s/a/changes/%s/revisions/%s/review", endPointToUse, commitDetails.getId(), revision);
        httpClient.postRequest(updateStatusURL, AuthenticationType.DIGEST, usernameToUse, passwordToUse, JSONUtils.toJSON(request));
    }

    @Override
    public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        if (!fields.containsKey(PLUGIN_SETTINGS_REVIEW_LABEL)) {
            response.add(getValidationError(
                        PLUGIN_SETTINGS_REVIEW_LABEL,
                        "Review field must be set"
            ));
        }

        return response;
    }

    @Override
    public PluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        ResultParser resultParser = new ResultParser(PLUGIN_SETTING_RESULT_PREFIX, CHECKBOX_TRUE_VALUE);
        return new GerritPluginSettings(
                responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_END_POINT),
                responseBodyMap.get(PLUGIN_SETTINGS_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_PASSWORD),
                responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                resultParser.toSet(responseBodyMap),
                responseBodyMap.get(PLUGIN_SETTINGS_REVIEW_LABEL),
                CHECKBOX_TRUE_VALUE.equals(responseBodyMap.get(PLUGIN_SETTINGS_PASS_AT_END)),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_PASSWORD),
                responseBodyMap.get(PLUGIN_SETTINGS_GO_API_SERVER_HOST)
        );
    }

    int getCodeReviewValue(String result) {
        result = result == null ? "" : result;
        int value = IN_PROGRESS_VALUE;
        if (result.equalsIgnoreCase("Passed")) {
            value = SUCCESS_VALUE;
        } else if (result.equalsIgnoreCase("Failed")) {
            value = FAILURE_VALUE;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            value = FAILURE_VALUE;
        }
        return value;
    }
}
