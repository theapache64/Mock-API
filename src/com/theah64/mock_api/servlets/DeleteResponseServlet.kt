package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Responses
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
@WebServlet(urlPatterns = ["/v1/delete_response"])
class DeleteResponseServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(Responses.COLUMN_ID)

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class, QueryBuilderException::class)
    override fun doAdvancedPost() {
        val respId = getStringParameter(Responses.COLUMN_ID)!!
        Responses.instance.delete(Responses.COLUMN_ID, respId)
        writer!!.write(APIResponse("Response deleted", null).response)
    }
}
