package com.tw.go.plugin.setting;

import java.util.Set;

public class DefaultPluginSettings implements PluginSettings {
    private String serverBaseURL;
    private String endPoint;
    private String username;
    private String password;
    private String oauthToken;
    private Set<String> results;
    private boolean passAtEnd;
    private String goAPIUsername;
    private String goAPIPassword;
    private String goAPIServerHost;

    public DefaultPluginSettings() {
    }

    public DefaultPluginSettings(String serverBaseURL, String endPoint, String username, String password, String oauthToken, Set<String> results, boolean passAtEnd, String goAPIUsername, String goAPIPassword, String goAPIServerHost) {
        this.serverBaseURL = serverBaseURL;
        this.endPoint = endPoint;
        this.username = username;
        this.password = password;
        this.oauthToken = oauthToken;
        this.results = results;
        this.passAtEnd = passAtEnd;
        this.goAPIUsername = goAPIUsername;
        this.goAPIPassword = goAPIPassword;
        this.goAPIServerHost = goAPIServerHost;
    }

    @Override
    public String getServerBaseURL() {
        return serverBaseURL;
    }

    public void setServerBaseURL(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    @Override
    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    @Override
    public Set<String> getResults() {
        return results;
    }

    public void setResults(Set<String> results) {
        this.results = results;
    }

    @Override
    public boolean isPassAtEnd() {
        return passAtEnd;
    }

    public void setPassAtEnd(boolean passAtEnd) {
        this.passAtEnd = passAtEnd;
    }

    @Override
    public String getGoAPIUsername() {
        return goAPIUsername;
    }

    public void setGoAPIUsername(String goAPIUsername) {
        this.goAPIUsername = goAPIUsername;
    }

    @Override
    public String getGoAPIPassword() {
        return goAPIPassword;
    }

    public void setGoAPIPassword(String goAPIPassword) {
        this.goAPIPassword = goAPIPassword;
    }

    @Override
    public String getGoAPIServerHost() {
        return goAPIServerHost;
    }

    public void setGoAPIServerHost(String goAPIServerHost) {
        this.goAPIServerHost = goAPIServerHost;
    }

    public boolean shouldNotify(String status) {
        if (status == null) {
            return true;
        } else {
            return results.contains(status.toUpperCase());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultPluginSettings that = (DefaultPluginSettings) o;

        if (endPoint != null ? !endPoint.equals(that.endPoint) : that.endPoint != null) return false;
        if (oauthToken != null ? !oauthToken.equals(that.oauthToken) : that.oauthToken != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (serverBaseURL != null ? !serverBaseURL.equals(that.serverBaseURL) : that.serverBaseURL != null)
            return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (results != null ? !results.equals(that.results) : that.results != null) return false;
        if (goAPIUsername != null ? !goAPIUsername.equals(that.goAPIUsername) : that.goAPIUsername != null) return false;
        if (goAPIPassword != null ? !goAPIPassword.equals(that.goAPIPassword) : that.goAPIPassword != null) return false;
        if (goAPIServerHost != null ? !goAPIServerHost.equals(that.goAPIServerHost) : that.goAPIServerHost != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverBaseURL != null ? serverBaseURL.hashCode() : 0;
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (oauthToken != null ? oauthToken.hashCode() : 0);
        result = 31 * result + (results != null ? results.hashCode() : 0);
        result = 31 * result + (goAPIUsername != null ? goAPIUsername.hashCode() : 0);
        result = 31 * result + (goAPIPassword != null ? goAPIPassword.hashCode() : 0);
        result = 31 * result + (goAPIServerHost != null ? goAPIServerHost.hashCode() : 0);
        return result;
    }

}
