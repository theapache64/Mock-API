package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 15/1/18.
 */
public class Preference {

    private final String defaultSuccessResponse, defaultErrorResponse, baseResponseStructure;
    private final boolean isOnline;

    public Preference(String defaultSuccessResponse, String defaultErrorResponse, String baseResponseStructure, boolean isOnline) {
        this.defaultSuccessResponse = defaultSuccessResponse;
        this.defaultErrorResponse = defaultErrorResponse;
        this.baseResponseStructure = baseResponseStructure;
        this.isOnline = isOnline;
    }

    public String getDefaultSuccessResponse() {
        return defaultSuccessResponse;
    }

    public String getDefaultErrorResponse() {
        return defaultErrorResponse;
    }

    public String getBaseResponseStructure() {
        return baseResponseStructure;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
