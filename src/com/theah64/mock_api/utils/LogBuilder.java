package com.theah64.mock_api.utils;

/**
 * Created by theapache64 on 14/10/17.
 */
public class LogBuilder {
    private final StringBuilder logBuilder;

    public LogBuilder() {
        logBuilder = new StringBuilder();
    }


    public void append(String key, String value) {
        logBuilder.append("<b>").append(key).append("</b>: ").append(value).append("<br>");
    }

    public String build() {
        return logBuilder.toString();
    }
}
