package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.utils.APIResponse
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 21/9/17.
 */
@WebServlet(urlPatterns = ["/v1/search"])
class SearchServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doPost(req, resp)
    }

    override val requiredParameters: Array<String>?
        get() = arrayOf(KEY_COLUMN, KEY_VALUE)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {


        val projectId = headerSecurity!!.projectId
        val column = getStringParameter(KEY_COLUMN)!!
        val value = getStringParameter(KEY_VALUE)!!
        val routes = Routes.instance.getLike(Routes.COLUMN_PROJECT_ID, projectId, column, value, Routes.COLUMN_NAME)
        if (routes != null) {
            val jaRoutes = JSONArray()
            for (route in routes) {
                jaRoutes.put(route)
            }
            val joData = JSONObject()
            joData.put("routes", jaRoutes)
            writer!!.write(APIResponse("${routes.size} route(s) found", joData).response)
        } else {
            throw Request.RequestException("No match found")
        }


    }

    companion object {

        private val KEY_COLUMN = "column"
        private val KEY_VALUE = "value"
    }
}
