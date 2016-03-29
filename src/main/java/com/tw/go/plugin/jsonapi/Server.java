package com.tw.go.plugin.jsonapi;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.HttpConnectionUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static com.tw.go.plugin.util.StringUtils.isEmpty;

/**
 * Actual methods for contacting the remote server.
 */
public class Server {
    private Logger LOG = Logger.getLoggerFor(Server.class);

    // Contains authentication credentials, etc.
    private PluginSettings settings;
    private HttpConnectionUtil httpConnectionUtil;

    /**
     * Construct a new server object, using credentials from PluginSettings.
     */
    public Server(PluginSettings settings) {
        this.settings = settings;
        httpConnectionUtil = new HttpConnectionUtil();
    }

    Server(PluginSettings settings, HttpConnectionUtil httpConnectionUtil) {
        this.settings = settings;
        this.httpConnectionUtil = httpConnectionUtil;
    }

    public <T> T getResourceAs(URL url, Class<T> type)
            throws IOException {
        URL normalizedUrl;
        try {
            normalizedUrl = url.toURI().normalize().toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        LOG.info("Fetching " + normalizedUrl.toString());

        HttpURLConnection request = httpConnectionUtil.getConnection(normalizedUrl);

        final String login = settings.getGoAPIUsername();
        final String password = settings.getGoAPIPassword();
        // Add in our HTTP authorization credentials if we have them.
        if (!isEmpty(login) && !isEmpty(password)) {
            String userpass = login + ":" + password;
            String basicAuth = "Basic "
                    + DatatypeConverter.printBase64Binary(userpass.getBytes());
            request.setRequestProperty("Authorization", basicAuth);
        }

        request.connect();

        return httpConnectionUtil.responseToType(request.getContent(), type);
    }


    /**
     * Get a specific instance of a pipeline.
     */
    public Pipeline getPipelineInstance(String pipelineName, String pipelineCounter)
            throws MalformedURLException, IOException {
        final String apiHost = settings.getGoAPIServerHost();
        URL url = new URL(String.format("%s/go/api/pipelines/%s/instance/%s",
                apiHost, pipelineName, pipelineCounter));

        return getResourceAs(url, Pipeline.class);
    }

}
