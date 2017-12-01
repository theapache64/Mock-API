package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 1/12/17.
 */
public class Response {

    private final String id, name, routeId, response;

    public Response(String id, String name, String routeId, String response) {
        this.id = id;
        this.name = name;
        this.routeId = routeId;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getResponse() {
        return response;
    }
}
