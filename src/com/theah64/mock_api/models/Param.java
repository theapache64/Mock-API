package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 30/11/17.
 */
public class Param {
    private final String id, name, routeId, type;

    public Param(String id, String name, String routeId, String type) {
        this.id = id;
        this.name = name;
        this.routeId = routeId;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
