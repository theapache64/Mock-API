package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 15/1/18.
 */
public class Preference {
    
    private final String defaultSuccessResponse, defaultErrorResponse, baseResponseStructure;

    public Preference(String defaultSuccessResponse, String defaultErrorResponse, String baseResponseStructure) {
        this.defaultSuccessResponse = defaultSuccessResponse;
        this.defaultErrorResponse = defaultErrorResponse;
        this.baseResponseStructure = baseResponseStructure;
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
}
