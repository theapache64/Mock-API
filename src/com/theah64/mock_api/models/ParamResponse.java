package com.theah64.mock_api.models;

public class ParamResponse {
    public static final String EQUALS = "==";
    public static final String NOT_EQUALS = "!=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUALS = ">=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUALS = "<=";
    private final String id, routeId, paramId, paramValue, responseId, relOpt;

    public ParamResponse(String id, String routeId, String paramId, String paramValue, String responseId, String relOpt) {
        this.id = id;
        this.routeId = routeId;
        this.paramId = paramId;
        this.paramValue = paramValue;
        this.responseId = responseId;
        this.relOpt = relOpt;
    }

    public String getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getParamId() {
        return paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public String getResponseId() {
        return responseId;
    }

    public String getRelOpt() {
        return relOpt;
    }
}
