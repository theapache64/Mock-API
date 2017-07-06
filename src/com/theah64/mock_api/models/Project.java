package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Project {
    public static final String KEY = "project";
    private String id;
    private final String name;
    private final String passHash;
    private final String apiKey;
    private String baseOgApiUrl;

    public Project(String id, String name, String passHash, String apiKey, String baseOgApiUrl) {
        this.id = id;
        this.name = name;
        this.passHash = passHash;
        this.apiKey = apiKey;
        this.baseOgApiUrl = baseOgApiUrl;
    }

    public String getBaseOgApiUrl() {
        return baseOgApiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBaseOgApiUrl(String baseOgApiUrl) {
        this.baseOgApiUrl = baseOgApiUrl;
    }
}
