package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Responses
import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Response
import com.theah64.mock_api.utils.APIResponse
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 1/12/17.
 */
@WebServlet(urlPatterns = ["/v1/add_response"])
class AddResponseServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(Responses.COLUMN_NAME, Routes.COLUMN_PROJECT_ID, KEY_ROUTE, Responses.COLUMN_RESPONSE)

    @Throws(
            Request.RequestException::class,
            IOException::class,
            JSONException::class,
            SQLException::class,
            Request.RequestException::class,
            PathInfo.PathInfoException::class,
            QueryBuilderException::class
    )
    override fun doAdvancedPost() {

        val name = getStringParameter(Responses.COLUMN_NAME)!!
        val routeName = getStringParameter(KEY_ROUTE)!!
        val projectId = getStringParameter(Routes.COLUMN_PROJECT_ID)!!
        val routeId = Routes.INSTANCE.get(Routes.COLUMN_PROJECT_ID, projectId, Routes.COLUMN_NAME, routeName, Routes.COLUMN_ID)!!

        //Checking duplicate responseName
        val isExist = Responses.INSTANCE.isExist(Responses.COLUMN_NAME, name, Responses.COLUMN_ROUTE_ID, routeId)

        if (!isExist) {
            val response = getStringParameter(Responses.COLUMN_RESPONSE)!!
            val respId = Responses.INSTANCE.addv3(Response(null, name, routeId, response))
            writer!!.write(APIResponse("Response added", "id", respId).response)
        } else {
            writer!!.write(APIResponse("ERROR: Duplicate response -$name").response)
        }
    }

    companion object {

        private val KEY_ROUTE = "route"
    }
}
