package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Project {
    public static final String KEY = "project";
    private String id;
    private final String name;
    private final String passHash;

    public Project(String id, String name, String passHash) {
        this.id = id;
        this.name = name;
        this.passHash = passHash;
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
}
