package org.ies.fenix.client.api;

public class SessionManager {

    private String token;
    private Integer clientId;
    private String username;

    public void saveSession(String token, Integer clientId, String username) {
        this.token = token;
        this.clientId = clientId;
        this.username = username;
    }

    public void clearSession() {
        this.token = null;
        this.clientId = null;
        this.username = null;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public String getToken() {
        return token;
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthorizationHeader() {
        return "Bearer " + token;
    }
}