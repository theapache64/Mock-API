package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLog {

    private final String routeId, requestBody, errorResponse,createdAt;


    public HitLog(String routeId, String requestBody, String errorResponse, String createdAt) {
        this.routeId = routeId;
        this.requestBody = requestBody;
        this.errorResponse = errorResponse;
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
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
