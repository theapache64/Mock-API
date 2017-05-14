package com.theah64.mock_api.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 14/5/17.
 */
public class JSON {
    private final String projectId, route, response;

    public JSON(String projectId, String route, String response) throws JSONException {
        this.projectId = projectId;
        this.route = route;
        this.response = new JSONObject(response).toString();
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
