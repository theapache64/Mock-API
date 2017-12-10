package com.theah64.mock_api.models;

public class TinifyKey {

    private final String id, key, email, usage;

    public TinifyKey(String id, String key, String email, String usage) {
        this.id = id;
        this.key = key;
        this.email = email;
        this.usage = usage;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getKey() {
        return key;
    }

    public String getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "TinifyKey{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", email='" + email + '\'' +
                ", usage='" + usage + '\'' +
                '}';
    }
}
