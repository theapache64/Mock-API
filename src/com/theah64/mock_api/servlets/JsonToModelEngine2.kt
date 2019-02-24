package com.theah64.mock_api.servlets

import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 13/11/17.
 */
@WebServlet("/v1/json_to_model_engine2")
class JsonToModelEngine2 : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = false

    override val requiredParameters: Array<String>?
        @Throws(Request.RequestException::class)
        get() = arrayOf(KEY_MODEL_NAME, KEY_JO_STRING, KEY_IS_RETROFIT_MODEL)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {

    }

    companion object {

        private val KEY_MODEL_NAME = "model_name"
        private val KEY_JO_STRING = "jo_string"
        private val KEY_IS_RETROFIT_MODEL = "is_retrofit_model"
    }
}
