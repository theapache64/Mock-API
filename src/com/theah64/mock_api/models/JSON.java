package com.theah64.mock_api.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 14/5/17.
 */
public class JSON {
    private final String id,projectId, route, response;

    public JSON(String id, String projectId, String route, String response) throws JSONException {
        this.id = id;
        this.projectId = projectId;
        this.route = route;
        this.response = response != null ? new JSONObject(response).toString() : null;
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
