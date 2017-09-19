package com.theah64.mock_api.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 14/5/17.
 */
public class JSON {
    private final String id, projectId, route, response, requiredParams, optionalParams, description, externalApiUrl;
    private boolean isSecure;
    private final long delay;
    private final long updatedInMillis;

    public JSON(String id, String projectId, String route, String response, String requiredParams, String optionalParams, String description, String externalApiUrl, boolean isSecure, long delay, long updatedInMillis) throws JSONException {
        this.id = id;
        this.projectId = projectId;
        this.route = route;
        this.response = response != null ? new JSONObject(response).toString() : null;
        this.requiredParams = requiredParams;
        this.optionalParams = optionalParams;
        this.description = description;
        this.externalApiUrl = externalApiUrl;
        this.isSecure = isSecure;
        this.delay = delay;
        this.updatedInMillis = updatedInMillis;
    }

    public long getUpdatedInMillis() {
        return updatedInMillis;
    }

    public String getExternalApiUrl() {
        return externalApiUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public long getDelay() {
        return delay;
    }

    public String getRequiredParams() {
        return requiredParams;
    }

    public String getOptionalParams() {
        return optionalParams;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRoute() {
        return route;
    }

    public String getResponse() {
        return response;
    }
}
