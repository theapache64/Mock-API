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

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Param param = (Param) object;
            if (this.name.equals(param.getName())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
