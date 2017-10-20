package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLog {

    private final String routeId, requestBody;
    private final boolean isSuccess;

    public HitLog(String routeId, String requestBody, boolean isSuccess) {
        this.routeId = routeId;
        this.requestBody = requestBody;
        this.isSuccess = isSuccess;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
