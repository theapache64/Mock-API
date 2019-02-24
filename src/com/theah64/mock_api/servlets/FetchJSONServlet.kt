package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Params
import com.theah64.mock_api.database.Responses
import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.TimeUtils
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = ["/fetch_json/*"])
class FetchJSONServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = null

    @Throws(
            Request.RequestException::class,
            IOException::class,
            JSONException::class,
            SQLException::class,
            PathInfo.PathInfoException::class,
            QueryBuilderException::class
    )
    override fun doAdvancedPost() {

        val pathInfo = PathInfo(httpServletRequest!!.pathInfo, 2, PathInfo.UNLIMITED)
        val projectName = pathInfo.getPart(1)
        val routeName = pathInfo.getPartFrom(2)
        val route = Routes.INSTANCE.get(projectName, routeName)!!


        val joJson = JSONObject()

        val jaResponses = JSONArray()
        val responses = Responses.INSTANCE.getAll(Responses.COLUMN_ROUTE_ID, route.id!!)
        for (response in responses) {
            val joResponse = JSONObject()
            joResponse.put(Responses.COLUMN_ID, response.id)
            joResponse.put(Responses.COLUMN_NAME, response.name)
            jaResponses.put(joResponse)
        }

        joJson.put(KEY_RESPONSES, jaResponses)

        val jaParams = JSONArray()

        //Adding required params
        for (param in route.params!!) {
            val joParam = JSONObject()
            joParam.put(Params.COLUMN_ID, param.id)
            joParam.put(Params.COLUMN_NAME, param.name)
            joParam.put(Params.COLUMN_DATA_TYPE, param.dataType)
            joParam.put(Params.COLUMN_DEFAULT_VALUE, param.defaultValue)
            joParam.put(Params.COLUMN_DESCRIPTION, param.description)
            joParam.put(Params.COLUMN_IS_REQUIRED, param.isRequired)
            jaParams.put(joParam)
        }

        joJson.put(Routes.KEY_PARAMS, jaParams)
        joJson.put(Routes.COLUMN_EXTERNAL_API_URL, route.externalApiUrl)
        joJson.put(Routes.COLUMN_IS_SECURE, route.isSecure)
        joJson.put(Routes.COLUMN_DELAY, route.delay)
        joJson.put(Routes.COLUMN_DESCRIPTION, route.description)
        joJson.put(Routes.COLUMN_METHOD, route.method)

        joJson.put(KEY_DUMMY_PARAMS, route.dummyRequiredParams)
        joJson.put(KEY_LAST_MODIFIED, TimeUtils.millisToLongDHMS(route.updatedInMillis) + " ago")
        joJson.put(KEY_LAST_MODIFIED_DATE, getIndianDate(route.updatedInMillis))

        writer!!.write(APIResponse("Response loaded", joJson).response)
    }

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doPost(req, resp)
    }

    private fun getIndianDate(updatedInMillis: Long): String {
        val ldt = LocalDateTime.parse(DATE_FORMAT.format(Date(updatedInMillis)), DateTimeFormatter.ofPattern(DATE_FORMAT.toPattern()))
        return DATE_FORMAT.format(Date.from(ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant()))
    }

    companion object {


        val KEY_DUMMY_PARAMS = "dummy_params"
        private val KEY_LAST_MODIFIED = "last_modified"
        private val KEY_LAST_MODIFIED_DATE = "last_modified_date"
        private val KEY_RESPONSES = "responses"
        private val DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
    }
}
