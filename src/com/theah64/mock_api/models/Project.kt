package com.theah64.mock_api.models

/**
 * Created by theapache64 on 14/5/17.
 */
class Project(
        var id: String?,
        var name: String,
        val passHash: String,
        val apiKey: String,
        val requestBodyType: String,
        var baseOgApiUrl: String,
        var packageName: String,
        var isAllSmallRoutes: Boolean,
        var notificationEmails: String?,
        var defaultSuccessResponse: String,
        var defaultErrorResponse: String,
        var baseResponseStructure: String) {
    companion object {
        const val KEY = "project"
        const val REQUEST_BODY_TYPE_FORM = "JSON"
    }
}
