package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Routes
import okhttp3.Route
import org.json.JSONObject
import javax.servlet.annotation.WebServlet

@WebServlet(urlPatterns = ["/get_route"])
class GetRouteDetailsServlet : AdvancedBaseServlet() {

    companion object {
        private const val KEY_PACKAGE_NAME = "package_name"
        private const val KEY_ROUTE_NAME = "route_name"
    }

    override val isSecureServlet: Boolean
        get() = false

    override val requiredParameters: Array<String>?
        get() = arrayOf(
                KEY_PACKAGE_NAME,
                KEY_ROUTE_NAME
        )

    override fun doAdvancedPost() {
        val packageName = getStringParameter(KEY_PACKAGE_NAME)!!
        val routeName = getStringParameter(KEY_ROUTE_NAME)!!

        val route = Routes.instance.getRouteFrom(packageName, routeName)
        if (route != null) {
            val joData = JSONObject()
            joData.put(Routes.COLUMN_METHOD, route.method)
            joData.put(Routes.COLUMN_NAME, route.name)
            joData.put(Routes.COLUMN_DESCRIPTION, route.description)
            joData.put(Routes.COLUMN_REQUEST_BODY_TYPE, route.requestBodyType)
            joData.put(Routes.COLUMN_JSON_REQ_BODY, route.jsonReqBody)
            /* joData.put(Routes.CO)
             writer.write()*/
        }
    }

}