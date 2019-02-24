package com.theah64.mock_api.models

class ParamResponse(
        val id: String?,
        val routeId: String,
        val paramId: String,
        val paramValue: String,
        val responseId: String,
        val relOpt: String
) {
    companion object {
        val EQUALS = "=="
        val NOT_EQUALS = "!="
        val GREATER_THAN = ">"
        val GREATER_THAN_OR_EQUALS = ">="
        val LESS_THAN = "<"
        val LESS_THAN_OR_EQUALS = "<="
    }
}
