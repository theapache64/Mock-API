package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLog {

    private final String routeId, requestBody, errorResponse;


    public HitLog(String routeId, String requestBody, String errorResponse) {
        this.routeId = routeId;
        this.requestBody = requestBody;
        this.errorResponse = errorResponse;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getErrorResponse() {
        return errorResponse;
    }
}
