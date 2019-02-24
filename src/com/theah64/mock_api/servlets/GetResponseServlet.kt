package com.theah64.mock_api.servlets


import com.theah64.mock_api.database.Responses
import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.utils.APIResponse
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException
import org.json.JSONObject

import javax.servlet.annotation.WebServlet
import java.io.IOException
import java.sql.SQLException

@WebServlet(urlPatterns = ["/v1/get_response"])
class GetResponseServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(Responses.COLUMN_ID, KEY_ROUTE_NAME, KEY_PROJECT_NAME)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class, QueryBuilderException::class)
    override fun doAdvancedPost() {

        val responseId = getStringParameter(Responses.COLUMN_ID)!!
        val resp: String?

        if (responseId == Routes.COLUMN_DEFAULT_RESPONSE) {
            val routeName = getStringParameter(KEY_ROUTE_NAME)!!
            val projectName = getStringParameter(KEY_PROJECT_NAME)!!
            resp = Routes.INSTANCE.get(projectName, routeName)!!.defaultResponse
        } else {
            resp = Responses.INSTANCE.get(
                    Responses.COLUMN_ID,
                    responseId,
                    Responses.COLUMN_RESPONSE,
                    false
            )
        }


        if (resp != null) {

            val joData = JSONObject()
            joData.put(Responses.COLUMN_ID, responseId)
            joData.put(Responses.COLUMN_RESPONSE, resp)

            writer!!.write(APIResponse("OK", joData).response)
        } else {
            writer!!.write(APIResponse("Invalid response id").response)
        }

    }

    companion object {

        private val KEY_ROUTE_NAME = "route_name"
        private val KEY_PROJECT_NAME = "project_name"
    }
}
