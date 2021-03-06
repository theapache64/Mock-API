package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.*
import com.theah64.mock_api.lab.Main
import com.theah64.mock_api.models.ParamResponse
import com.theah64.mock_api.models.Project
import com.theah64.mock_api.models.Route
import com.theah64.mock_api.utils.DynamicResponseGenerator
import com.theah64.mock_api.utils.GoogleSheetUtils
import com.theah64.mock_api.utils.HeaderSecurity
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.CommonUtils
import com.theah64.mock_api.utils.ParamFilter
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.lang.Exception
import java.sql.SQLException

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = ["/get_json/*"])
@MultipartConfig
class GetJSONServlet : AdvancedBaseServlet() {

    private var route: Route? = null

    override val isSecureServlet: Boolean
        get() = false

    override fun isJsonBody(): Boolean {
        initRoute()

        val isJsonBody = route!!.requestBodyType == Project.REQUEST_BODY_TYPE_JSON
        return isJsonBody
    }

    private fun initRoute() {
        println("Route init")
        val pathInfo = PathInfo(httpServletRequest!!.pathInfo, 2, PathInfo.UNLIMITED)
        val projectName = pathInfo.getPart(1)
        val routeName = pathInfo.getPartFrom(2)
        println("Route name is $routeName")

        try {
            this.route = Routes.instance.get(projectName!!, routeName!!)
            println("Route is $route")

        } catch (e: SQLException) {
            val errMsg = e.message
            if (errMsg?.startsWith("No response found for") == true) {
                println("Default route failed check for slash...")
                if (routeName.indexOf("/") != -1) {

                    // has slashes
                    val slashIndex = routeName.lastIndexOf("/")
                    val routeWithOutLast = routeName.substring(0, slashIndex)

                    this.route = Routes.instance.getRouteLike(projectName!!, "$routeWithOutLast/")

                    println("WITH OUT LAST : $routeWithOutLast")

                } else {
                    throw  e
                }
            } else {
                throw  e
            }
        }


    }

    override val requiredParameters: Array<String>?
        get() = try {

            println("HEHE")
            initRoute()

            // required params only if the request body is FORM
            if (route!!.requestBodyType == Project.REQUEST_BODY_TYPE_FORM) {
                // form
                route!!.filterRequiredParams()
            } else {
                // json
                val isSkipParam = httpServletRequest!!.getParameter("is_skip_param") == "true"

                if (isSkipParam) {
                    arrayOf()
                } else {
                    val jsonBody = route!!.jsonReqBody
                    if (jsonBody != null) {
                        ParamFilter.filterRequiredParams(jsonBody).toTypedArray()
                    } else {
                        // no req params
                        arrayOf()
                    }
                }

            }


        } catch (e: PathInfo.PathInfoException) {
            e.printStackTrace()
            throw Request.RequestException(e.message)
        } catch (e: SQLException) {
            e.printStackTrace()
            throw Request.RequestException(e.message)
        }

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doPost(req, resp)
    }


    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class)
    override fun doAdvancedPost() {


        if (!getBooleanParameter("is_skip_auth") && route!!.isSecure) {
            val authorization = httpServletRequest!!.getHeader(HeaderSecurity.KEY_AUTHORIZATION)
                    ?: throw Request.RequestException("Authorization header missing")
        }


        if (route!!.delay > 0) {
            try {

                Thread.sleep(route!!.delay * 1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        //Getting param response
        try {
            val paramResponses = ParamResponses.instance.getAll(ParamResponses.COLUMN_ROUTE_ID, route!!.id!!)
            var jsonResp: String? = null
            if (!paramResponses.isEmpty()) {
                //Has custom resp
                for (paramResponse in paramResponses) {

                    val pVal = getStringParameter(Params.instance.get(Params.COLUMN_ID, paramResponse.paramId, Params.COLUMN_NAME, false)!!)

                    if (pVal != null) {

                        val op = paramResponse.relOpt
                        val resp = Responses.instance.get(Responses.COLUMN_ID, paramResponse.responseId, Responses.COLUMN_RESPONSE, false)

                        if (op == ParamResponse.EQUALS && pVal == paramResponse.paramValue || op == ParamResponse.NOT_EQUALS && pVal != paramResponse.paramValue) {
                            jsonResp = resp
                        } else {
                            try {
                                //Convert to number
                                val pNum = java.lang.Double.parseDouble(paramResponse.paramValue)
                                val inputNum = java.lang.Double.parseDouble(pVal)

                                if (op == ParamResponse.GREATER_THAN && inputNum > pNum ||
                                        op == ParamResponse.GREATER_THAN_OR_EQUALS && inputNum >= pNum ||
                                        op == ParamResponse.LESS_THAN && inputNum < pNum ||
                                        op == ParamResponse.LESS_THAN_OR_EQUALS && inputNum <= pNum) {
                                    jsonResp = resp
                                }
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }

                        }

                        if (jsonResp != null) {
                            break
                        }
                    }
                }
            }

            if (jsonResp == null) {
                jsonResp = route!!.defaultResponse
            }

            //Input throw back
            if (route!!.requestBodyType == Project.REQUEST_BODY_TYPE_FORM) {
                // form
                if (route!!.params != null) {
                    val reqParams = route!!.params!!
                    for (params in reqParams) {
                        val value = getStringParameter(params.name)
                        if (value != null && !value.trim { it <= ' ' }.isEmpty()) {
                            jsonResp = jsonResp!!.replace("{" + params.name + "}", value)
                        }
                    }
                }
            } else {
                // json
                val joReqBody = if (route!!.jsonReqBody == null || route!!.jsonReqBody!!.isEmpty()) "{}" else route!!.jsonReqBody!!
                val reqParams = ParamFilter.filterAllParams(joReqBody)
                for (key in reqParams) {
                    if (request!!.joRequestBody.has(key)) {
                        val valueX = request!!.joRequestBody.get(key)
                        if (valueX is Int || valueX is String) {
                            val value = valueX.toString()
                            if (value.trim { it <= ' ' }.isNotEmpty()) {
                                jsonResp = jsonResp!!.replace("{$key}", value)
                            }
                        }
                    }
                }
            }

            jsonResp = DynamicResponseGenerator.generate(jsonResp)
            jsonResp = Main.ConditionedResponse.generate(jsonResp!!)
            jsonResp = GoogleSheetUtils.generate(jsonResp)

            //Validation
            if (CommonUtils.isJSONValid(jsonResp, "Invalid JSON Response")) {
                httpServletResponse!!.addHeader("Content-Length", jsonResp.length.toString())
                httpServletResponse!!.status = route!!.statusCode
                writer!!.write(jsonResp)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw Request.RequestException(e.message)
        }

    }


}
