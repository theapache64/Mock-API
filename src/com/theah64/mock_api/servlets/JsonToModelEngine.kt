package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Projects
import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.CodeGenJava
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 13/11/17.
 */
@WebServlet("/v1/json_to_model_engine")
class JsonToModelEngine : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(KEY_ROUTE_NAME, KEY_JO_STRING, KEY_IS_RETROFIT_MODEL)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {

        val routeName = getStringParameter(KEY_ROUTE_NAME)
        val modelName = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response"
        val joString = getStringParameter(KEY_JO_STRING)
        val isRetrofitModel = getBooleanParameter(KEY_IS_RETROFIT_MODEL)

        val packageName = Projects.INSTANCE.get(Projects.COLUMN_ID, headerSecurity!!.projectId, Projects.COLUMN_PACKAGE_NAME, false)
        writer!!.write(APIResponse("Done", "data", CodeGenJava.getFinalCode(packageName, joString, modelName, isRetrofitModel)).response)
    }


    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doPost(req, resp)
    }

    companion object {

        val KEY_ROUTE_NAME = "route_name"
        val KEY_JO_STRING = "jo_string"
        val KEY_TARGET_LANG = "target_lang"
        val KEY_IS_RETROFIT_MODEL = "is_retrofit_model"

        val LANGUAGE_TYPESCRIPT_INTERFACE = "typescript_interface"
        val LANGUAGE_TYPESCRIPT_CLASS = "typescript_class"
        val LANGUAGE_JAVA = "java"
    }


}
