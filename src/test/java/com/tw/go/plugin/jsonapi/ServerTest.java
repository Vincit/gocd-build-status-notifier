package com.tw.go.plugin.jsonapi;

import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.HttpConnectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ServerTest {

    private static final String HOST_NAME = "http://exmaple.org";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String BASIC_AUTH_TOKEN = "Basic dXNlcjpwYXNz";

    @Mock
    private HttpConnectionUtil httpConnectionUtil;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetPipelineInstance_WithoutCredentials() throws IOException {
        Server server = new Server(getSettingsWithoutCredentials(), httpConnectionUtil);

        URL expectedUrl = new URL(HOST_NAME + "/go/api/pipelines/pipeline-name/instance/12");
        HttpURLConnection connection = mockConnection(expectedUrl, "pipeline-name");

        Pipeline pipelineInstance = server.getPipelineInstance("pipeline-name", "12");

        verify(connection).connect();
        verify(connection, never()).setRequestProperty(anyString(), anyString());

        assertThat(pipelineInstance, notNullValue());
        assertThat(pipelineInstance.name, is("pipeline-name"));
    }

    @Test
    public void shouldGetPipelineInstance_WithCredentials() throws IOException {
        Server server = new Server(getSettingsWithCredentials(), httpConnectionUtil);

        URL expectedUrl = new URL(HOST_NAME + "/go/api/pipelines/pipeline-name/instance/12");
        HttpURLConnection connection = mockConnection(expectedUrl, "pipeline-name");

        Pipeline pipelineInstance = server.getPipelineInstance("pipeline-name", "12");

        verify(connection).connect();

        verify(connection).setRequestProperty("Authorization", BASIC_AUTH_TOKEN);

        assertThat(pipelineInstance, notNullValue());
        assertThat(pipelineInstance.name, is("pipeline-name"));
    }

    private HttpURLConnection mockConnection(URL expectedUrl, String pipelineName) throws IOException {
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(httpConnectionUtil.getConnection(eq(expectedUrl))).thenReturn(connection);

        Object response = new Object();
        when(connection.getContent()).thenReturn(response);

        Pipeline pipeline = new Pipeline();
        pipeline.name = pipelineName;
        when(httpConnectionUtil.responseToType(same(response), eq(Pipeline.class))).thenReturn(pipeline);

        return connection;
    }

    private PluginSettings getSettingsWithoutCredentials() {
        DefaultPluginSettings settings = new DefaultPluginSettings();
        settings.setGoAPIServerHost(HOST_NAME);

        return settings;
    }

    private PluginSettings getSettingsWithCredentials() {
        DefaultPluginSettings settings = new DefaultPluginSettings();
        settings.setGoAPIServerHost(HOST_NAME);
        settings.setGoAPIUsername(USERNAME);
        settings.setGoAPIPassword(PASSWORD);

        return settings;
    }

}