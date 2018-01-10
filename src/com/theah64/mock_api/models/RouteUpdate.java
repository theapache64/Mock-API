package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 10/1/18.
 */
public class RouteUpdate {

    private final String id, key, routeId, method, params, delay, description, defaultResponse;

    public RouteUpdate(String id, String key, String routeId, String method, String params, String delay, String description, String defaultResponse) {
        this.id = id;
        this.key = key;
        this.routeId = routeId;
        this.method = method;
        this.params = params;
        this.delay = delay;
        this.description = description;
        this.defaultResponse = defaultResponse;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getMethod() {
        return method;
    }

    public String getParams() {
        return params;
    }

    public String getDelay() {
        return delay;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }
}
