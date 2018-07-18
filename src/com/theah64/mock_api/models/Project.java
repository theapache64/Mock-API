package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Project {
    public static final String KEY = "project";
    private final String passHash;
    private final String apiKey;
    private String id;
    private String name;
    private String baseOgApiUrl;
    private String packageName;
    private boolean isAllSmallRoutes;
    private String notificationEmails;
    private String defaultSuccessResponse, defaultErrorResponse, baseResponseStructure;

    public Project(String id, String name, String passHash, String apiKey, String baseOgApiUrl, String packageName, boolean isAllSmallRoutes, String notificationEmails, String defaultSuccessResponse, String defaultErrorResponse, String baseResponseStructure) {
        this.id = id;
        this.name = name;
        this.passHash = passHash;
        this.apiKey = apiKey;
        this.baseOgApiUrl = baseOgApiUrl;
        this.packageName = packageName;
        this.isAllSmallRoutes = isAllSmallRoutes;
        this.notificationEmails = notificationEmails;
        this.defaultSuccessResponse = defaultSuccessResponse;
        this.defaultErrorResponse = defaultErrorResponse;
        this.baseResponseStructure = baseResponseStructure;
    }

    public String getDefaultSuccessResponse() {
        return defaultSuccessResponse;
    }

    public void setDefaultSuccessResponse(String defaultSuccessResponse) {
        this.defaultSuccessResponse = defaultSuccessResponse;
    }

    public String getDefaultErrorResponse() {
        return defaultErrorResponse;
    }

    public void setDefaultErrorResponse(String defaultErrorResponse) {
        this.defaultErrorResponse = defaultErrorResponse;
    }

    public String getBaseResponseStructure() {
        return baseResponseStructure;
    }

    public void setBaseResponseStructure(String baseResponseStructure) {
        this.baseResponseStructure = baseResponseStructure;
    }

    public String getNotificationEmails() {
        return notificationEmails;
    }

    public void setNotificationEmails(String notificationEmails) {
        this.notificationEmails = notificationEmails;
    }

    public boolean isAllSmallRoutes() {
        return isAllSmallRoutes;
    }

    public void setAllSmallRoutes(boolean allSmallRoutes) {
        this.isAllSmallRoutes = allSmallRoutes;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBaseOgApiUrl() {
        return baseOgApiUrl;
    }

    public void setBaseOgApiUrl(String baseOgApiUrl) {
        this.baseOgApiUrl = baseOgApiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassHash() {
        return passHash;
    }
}
