package com.theah64.mock_api.models;

import com.sun.istack.internal.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Route {
    private String id;
    private final String projectId;
    private final String name;
    private final String defaultResponse;
    private final String description;
    private final String externalApiUrl;
    private boolean isSecure;
    private final long delay;
    private final long updatedInMillis;
    private final List<Param> requiredParams;
    private final List<Param> optionalParams;

    public Route(String id, String projectId, String route, String response, String description, String externalApiUrl, List<Param> requiredParams, List<Param> optionalParams, boolean isSecure, long delay, long updatedInMillis) throws JSONException {
        this.id = id;
        this.projectId = projectId;
        this.name = route;
        this.defaultResponse = response != null ? new JSONObject(response).toString() : null;
        this.description = description;
        this.externalApiUrl = externalApiUrl;
        this.requiredParams = requiredParams;
        this.optionalParams = optionalParams;
        this.isSecure = isSecure;
        this.delay = delay;
        this.updatedInMillis = updatedInMillis;
    }

    @NotNull
    public List<Param> getRequiredParams() {
        return requiredParams;
    }

    @NotNull
    public List<Param> getOptionalParams() {
        return optionalParams;
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


    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }

    public void setId(String id) {
        this.id = id;
    }
}
