package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Param
import com.theah64.mock_api.utils.CodeGenJava
import com.theah64.mock_api.utils.SlashCutter
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException
import java.io.IOException
import java.sql.SQLException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = ["/v1/get_api_interface_method"])
class GetAPIInterfaceMethodServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = false

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        doPost(req, resp)
    }

    override val requiredParameters: Array<String>?
        get() = arrayOf(KEY_PROJECT_NAME, Routes.COLUMN_NAME)

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {


        val projectName = getStringParameter(KEY_PROJECT_NAME)!!
        val routeName = getStringParameter(Routes.COLUMN_NAME)!!
        val responseClass = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response"

        val route = Routes.INSTANCE.get(projectName, routeName)

        if (route != null) {

            httpServletResponse!!.contentType = "text/plain"

            var codeBuilder = StringBuilder()
            val descriptionBuilder = StringBuilder()

            if (route.description != null && !route.description.trim({ it <= ' ' }).isEmpty()) {
                descriptionBuilder.append("* ").append(route.description).append("\n\n")
            }


            val returnClassName = CodeGenJava.getFromFirstCapCharacter(SlashCutter.cut(responseClass))
            codeBuilder.append(String.format("%s\n@%s(\"%s\")\nCall<BaseAPIResponse<%s>> %s(", if (route.method == "POST" && !route.params!!.isEmpty()) "@FormUrlEncoded" else "", route!!.method, route!!.name,
                    returnClassName, SlashCutter.cut(CodeGenJava.toCamelCase(route!!.name))))

            if (route.isSecure) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,")
            }

            var hasFileParam = false

            if (!route.params!!.isEmpty()) {

                val params = route.params
                for (param in params!!) {

                    val camelCaseParamName = CodeGenJava.toCamelCase(param.name)
                    descriptionBuilder.append("* @param ").append(camelCaseParamName).append(" <p>").append(param.description).append("</p>\n")

                    if (param.dataType == Param.DATA_TYPE_FILE) {
                        hasFileParam = true

                        codeBuilder.append(String.format("\n\t%s %s,", MULTIPART_KEY, camelCaseParamName))
                    } else {
                        codeBuilder.append(String.format("\n\t@%s(\"%s\") %s %s %s,", if (route!!.method == "POST" && codeBuilder.indexOf(MULTIPART_KEY) == -1) "Field" else "Query", param.name, if (param.isRequired) "@NonNull" else "@Nullable", getPrimitive(param.dataType), CodeGenJava.toCamelCase(param.name)))
                    }
                }
            }

            if (!route.params.isEmpty() || route.isSecure) {
                //Removing last comma
                codeBuilder = StringBuilder(codeBuilder.substring(0, codeBuilder.length - 1))
            }

            descriptionBuilder.append("* @return ").append(returnClassName)

            if (hasFileParam) {
                val index = codeBuilder.indexOf("@" + route!!.method)
                codeBuilder.insert(index, "@Multipart\n")
            }

            codeBuilder.append(");")

            //Adding multiline comment
            descriptionBuilder.insert(0, "/**\n")
            descriptionBuilder.append("\n*/\n")

            //description
            codeBuilder.insert(0, descriptionBuilder.toString())

            writer!!.write(codeBuilder.toString())

        } else {
            throw Request.RequestException("Invalid route")
        }


    }

    private fun getPrimitive(dataType: String): String {

        if (dataType == "Integer") {
            return "int"
        }

        return if (dataType != "String") {
            dataType.toLowerCase()
        } else dataType
    }

    companion object {

        private val KEY_PROJECT_NAME = "project_name"
        private val MULTIPART_KEY = "@Part MultipartBody.Part"
    }


}
