package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Param
import com.theah64.mock_api.models.Route
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
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = ["/v1/gen_redux_duck"])
class GenReduxDuckServlet : AdvancedBaseServlet() {

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

            val codeBuilder = StringBuilder()


            //Adding main constant
            val ROUTE_NAME = getFormattedRouteNameWithCaps(routeName)
            codeBuilder.append("\n// Keyword")
            codeBuilder.append(String.format("\nconst %s = '%s';\n", ROUTE_NAME, ROUTE_NAME))

            // Response class name
            val responseClassName = getResponseClassNameFromRoute(routeName)

            //Reducer
            val reducerName = getReducerNameRouteName(routeName)
            codeBuilder.append(String.format("\nexport const %s = \n (state : NetworkResponse<%s>, action : BaseAction) => \n ResponseManager.manage(%s, state, action);\n\n", reducerName, responseClassName, ROUTE_NAME))


            // Params
            codeBuilder.append("// Params\nexport class Params {\n  constructor(\n")

            for (param in route.params!!) {
                codeBuilder.append("    public readonly ").append(param.name)
                        .append(String.format("%s: ", if (param.isRequired) "" else "?")).append(getPrimitive(param.dataType))
                        .append(",\n")
            }

            codeBuilder.append("  ) { }\n}")


            codeBuilder.append("\n\n")

            //Action
            val actionName = getActionNameFromRouteName(routeName)
            codeBuilder.append(String.format("// Action \nexport const %s = (", actionName))

            //Looping through params
            if (route.isSecure) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,")
            }

            val hasFileParam = false


            if (!route.params.isEmpty()) {
                codeBuilder.append("\n\tparams : Params")
            }

            codeBuilder.append(String.format("%s): AxiosRequest => new AxiosRequest(", if (route.params.isEmpty()) "" else "\n"))
                    .append(String.format("\n\t%s,", ROUTE_NAME))
                    .append(String.format("\n\t'%s',", route.method))
                    .append(String.format("\n\t'/%s',\n", route.name))

            if (!route.params.isEmpty()) {
                codeBuilder.append("\tparams\n")
            }



            if (hasFileParam) {
                val index = codeBuilder.indexOf("@" + route.method)
                codeBuilder.insert(index, "@Multipart\n")
            }

            codeBuilder.append(");\n")

            writer!!.write(codeBuilder.toString())

        } else {
            throw Request.RequestException("Invalid route")
        }


    }

    private fun getFormattedRouteNameWithCaps(routeName: String): String {
        return routeName
                .replace("([a-zA-Z][a-z]*)([A-Z])".toRegex(), "$1_$2")
                .toUpperCase()
                .replace("[\\W+]".toRegex(), "_")
    }

    private fun getPrimitive(dataType: String): String {

        if (dataType == "Integer") {
            return "number"
        }

        return if (dataType != "string") {
            dataType.toLowerCase()
        } else dataType
    }

    companion object {

        private val KEY_PROJECT_NAME = "project_name"
        private const val MULTIPART_KEY = "@Part MultipartBody.Part"

        private fun getActionNameFromRouteName(routeName: String): String {
            var routeName = routeName
            routeName = routeName.replace("[^\\w]+".toRegex(), "_")
            return CodeGenJava.toCamelCase(routeName)
        }

        private fun getResponseClassNameFromRoute(routeName: String): String {
            var routeName = routeName
            routeName = routeName.replace("[^\\w]+".toRegex(), "_")
            return CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response"
        }

        private fun getReducerNameRouteName(routeName: String): String {
            var routeName = routeName
            routeName = routeName.replace("[^\\w]+".toRegex(), "_")
            return CodeGenJava.toCamelCase(routeName) + "Reducer"
        }
    }


}
