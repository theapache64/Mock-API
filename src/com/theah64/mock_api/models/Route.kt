package com.theah64.mock_api.models

import com.theah64.mock_api.utils.DynamicResponseGenerator

import java.util.ArrayList

/**
 * Created by theapache64 on 14/5/17.
 */
data class Route(
        var id: String?,
        val projectId: String?,
        val name: String,
        val requestBodyType: String,
        val jsonReqBody: String?,
        val defaultResponse: String?,
        val description: String?,
        val externalApiUrl: String?,
        val method: String?,
        val params: List<Param>?,
        val isSecure: Boolean,
        val delay: Long,
        val updatedInMillis: Long,
        val statusCode: Int
) {

    val dummyRequiredParams: String
        get() {
            val dummyParamBuilder = StringBuilder()
            for (param in filterRequiredParams()) {
                dummyParamBuilder.append(param).append("=").append(DynamicResponseGenerator.getLoremIpsum().getWords(1)).append("&")
            }
            dummyParamBuilder.append("is_skip_auth=true")

            println("Json req body is $jsonReqBody")

            if (jsonReqBody != null) {
                dummyParamBuilder.append("&is_skip_param=true")
            }

            return dummyParamBuilder.toString()
        }

    val bootstrapLabelForMethod: String
        get() {
            when (method) {

                METHOD_POST -> return "label-success"

                METHOD_GET -> return "label-primary"

                METHOD_DELETE -> return "label-danger"

                METHOD_PATCH -> return "label-warning"

                else -> return "label-default"
            }
        }

    fun filterRequiredParams(): Array<String> {
        val reqParams = ArrayList<String>()
        if (params != null) {
            for (param in params) {
                if (param.isRequired) {
                    reqParams.add(param.name)
                }
            }
        }
        return reqParams.toTypedArray()
    }

    companion object {

        private val METHOD_POST = "POST"
        private val METHOD_GET = "GET"
        private val METHOD_PUT = "PUT"
        private val METHOD_PATCH = "PATCH"
        private val METHOD_DELETE = "DELETE"
    }
}
