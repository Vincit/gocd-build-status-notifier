package com.tw.go.plugin.util;

import com.tw.go.plugin.jsonapi.Server;
import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.setting.PluginSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        final NotifyResolver notifyResolver = new NotifyResolver(settings, true);
        assertThat(notifyResolver.shouldNotify(PASSED_STATE), is(true));
    }

    @Test
    public void shouldNotify_LastStage_Passed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, true);

        assertThat(notifyResolver.shouldNotify(PASSED_STATE), is(true));
    }

    @Test
    public void shouldNotify_NotLastStage_Passed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, false);

        assertThat(notifyResolver.shouldNotify(PASSED_STATE), is(false));
    }

    @Test
    public void shouldNotify_NotLastStage_Passed_NotifyAlways() throws Exception {
        final PluginSettings settings = getSettingsNotifyAlways();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, false);

        assertThat(notifyResolver.shouldNotify(PASSED_STATE), is(true));
    }

    @Test
    public void shouldNotify_NotLastStage_Failed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, false);

        assertThat(notifyResolver.shouldNotify(FAILED_STATE), is(true));
    }

    @Test
    public void shouldNotify_LastStage_Failed() throws Exception {
        final PluginSettings settings = getSettingsNotifyPassedAtEnd();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, true);

        assertThat(notifyResolver.shouldNotify(FAILED_STATE), is(true));
    }

    @Test
    public void shouldNotify_NotificationTypeTurnedOff() throws Exception {
        final PluginSettings settings = getSettingsNotifyOnlyFailed();
        final NotifyResolver notifyResolver = new NotifyResolver(settings, true);

        assertThat(notifyResolver.shouldNotify(PASSED_STATE), is(false));
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