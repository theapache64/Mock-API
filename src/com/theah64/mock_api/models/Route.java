package com.theah64.mock_api.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Route {

    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";
    private static final String METHOD_DELETE = "DELETE";

    private String id;
    private final String projectId;
    private final String name;
    private final String defaultResponse;
    private final String description;
    private final String externalApiUrl;
    private final String method;
    private boolean isSecure;
    private final long delay;
    private final long updatedInMillis;
    private final List<Param> params;

    public Route(String id, String projectId, String route, String response, String description, String externalApiUrl, String method, List<Param> params, boolean isSecure, long delay, long updatedInMillis) throws JSONException {
        this.id = id;
        this.projectId = projectId;
        this.name = route;
        this.defaultResponse = response != null ? new JSONObject(response).toString() : null;
        this.description = description;
        this.externalApiUrl = externalApiUrl;
        this.method = method;
        this.params = params;
        this.isSecure = isSecure;
        this.delay = delay;
        this.updatedInMillis = updatedInMillis;
    }

    public List<Param> getParams() {
        return params;
    }

    public long getUpdatedInMillis() {
        return updatedInMillis;
    }

    public String getExternalApiUrl() {
        return externalApiUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public long getDelay() {
        return delay;
    }


    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] filterRequiredParams() {
        final List<String> reqParams = new ArrayList<>();
        for (final Param param : getParams()) {
            if (param.isRequired()) {
                reqParams.add(param.getName());
            }
        }
        return reqParams.toArray(new String[]{});
    }


    public String getMethod() {
        return method;
    }

    public String getDummyRequiredParams() {
        final StringBuilder dummyParamBuilder = new StringBuilder();
        for (final String param : filterRequiredParams()) {
            dummyParamBuilder.append(param).append("=sampleParam&");
        }
        return dummyParamBuilder.toString();
    }

    public String getBootstrapLabelForMethod() {
        switch (method) {

            case METHOD_POST:
                return "label-success";

            default:
                return "label-default";
        }
    }
}
