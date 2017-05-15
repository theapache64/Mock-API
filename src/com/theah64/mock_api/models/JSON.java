package com.theah64.mock_api.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 14/5/17.
 */
public class JSON {
    private final String id, projectId, route, response, requiredParams, optionalParams;

    public JSON(String id, String projectId, String route, String response, String requiredParams, String optionalParams) throws JSONException {
        this.id = id;
        this.projectId = projectId;
        this.route = route;
        this.response = response != null ? new JSONObject(response).toString() : null;
        this.requiredParams = requiredParams;
        this.optionalParams = optionalParams;
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
