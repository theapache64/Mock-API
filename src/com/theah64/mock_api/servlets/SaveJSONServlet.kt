package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Projects
import com.theah64.mock_api.database.Responses
import com.theah64.mock_api.database.RouteUpdates
import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Param
import com.theah64.mock_api.models.Project
import com.theah64.mock_api.models.Route
import com.theah64.mock_api.models.RouteUpdate
import com.theah64.mock_api.servlets.FetchJSONServlet.Companion.KEY_DUMMY_PARAMS
import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.DiffUtils
import com.theah64.mock_api.utils.MailHelper
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.exceptions.MailException
import com.theah64.webengine.utils.CommonUtils
import com.theah64.webengine.utils.RandomString
import com.theah64.webengine.utils.Request
import com.theah64.webengine.utils.WebEngineConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.sql.SQLException
import java.util.*
import javax.servlet.annotation.WebServlet

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = ["/v1/save_json"])
class SaveJSONServlet : AdvancedBaseServlet() {


    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(KEY_RESPONSE_ID, KEY_NOTIFY_OTHERS, KEY_RESPONSE)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, QueryBuilderException::class)
    override fun doAdvancedPost() {

        val routeName = getStringParameter(Routes.COLUMN_NAME)!!
        val projectId = headerSecurity!!.projectId

        var routeId = Routes.instance.get(Routes.COLUMN_NAME, routeName, Routes.COLUMN_PROJECT_ID, projectId, Routes.COLUMN_ID)

        val responseId = getStringParameter(KEY_RESPONSE_ID)!!
        val response = getStringParameter(KEY_RESPONSE)!!
        val method = getStringParameter(Routes.COLUMN_METHOD)!!


        val requestBodyType = getStringParameter(Routes.COLUMN_REQUEST_BODY_TYPE)!!
        val jsonReqBody = if (requestBodyType == Project.REQUEST_BODY_TYPE_JSON) getStringParameter(Routes.COLUMN_JSON_REQ_BODY) else null

        //Validation
        if (
                CommonUtils.isJSONValid(response, "Invalid response JSON : ")

        ) {

            if (requestBodyType == Project.REQUEST_BODY_TYPE_JSON
                    && jsonReqBody != null
                    && jsonReqBody.isNotEmpty()
                    && CommonUtils.isJSONValid(jsonReqBody, "Invalid request JSON : ")) {
                println("Valid json request")
            }

            var defaultResponse: String? = null
            if (responseId == Routes.COLUMN_DEFAULT_RESPONSE) {
                defaultResponse = response
            } else {
                Responses.instance.update(Responses.COLUMN_ID, responseId, Responses.COLUMN_RESPONSE, response)
            }


            val params = ArrayList<Param>()

            val notifyOthers = getBooleanParameter(KEY_NOTIFY_OTHERS)

            if (requestBodyType.equals(Project.REQUEST_BODY_TYPE_FORM)) {


                val paramNames = getStringParameterArray(KEY_PARAMS)
                val paramDataTypes = getStringParameterArray(KEY_DATA_TYPES)
                val paramDefaultValues = getStringParameterArray(KEY_DEFAULT_VALUES)
                val paramDescriptions = getStringParameterArray(KEY_DESCRIPTIONS)
                val paramIsRequired = getStringParameterArray(KEY_IS_REQUIRED)


                // adding params
                for (i in paramNames.indices) {

                    val paramName = paramNames[i].replace("\\s+".toRegex(), "_")
                    val paramDataType = paramDataTypes[i]
                    val paramDefaultValue = paramDefaultValues[i]
                    val paramDescription = paramDescriptions[i]

                    val isRequired = paramIsRequired[i] == "true"


                    if (!paramName.trim { it <= ' ' }.isEmpty()) {
                        params.add(Param(null, paramName, routeId, paramDataType, paramDefaultValue, paramDescription, isRequired))
                    }
                }
            }


            val description = getStringParameter(Routes.COLUMN_DESCRIPTION)!!
            val isSecure = getBooleanParameter(Routes.COLUMN_IS_SECURE)
            val delay = getLongParameter(Routes.COLUMN_DELAY)
            val externalApiUrl = getStringParameter(Routes.COLUMN_EXTERNAL_API_URL)

            /* if (externalApiUrl != null && !externalApiUrl.matches(URL_REGEX.toRegex())) {
                 throw Request.RequestException("Invalid external api url :$externalApiUrl")
             }*/


            val route = Route(null, projectId, routeName, requestBodyType, jsonReqBody, defaultResponse!!, description, externalApiUrl!!, method, params, isSecure, delay, -1)

            val joResp = JSONObject()
            joResp.put(KEY_DUMMY_PARAMS, route.dummyRequiredParams)

            val project = Projects.instance.get(Projects.COLUMN_ID, projectId)!!

            val subject: String
            val message: String
            val externalLink: String


            val updateKey = RandomString.get(50)

            if (routeId == null) {

                //Route doesn't exist
                routeId = Routes.instance.addv3(route)
                route.id = routeId

                joResp.put(Routes.COLUMN_ID, routeId)

                subject = "Route established - " + project.name + " / " + route.name
                message = "Route Added"
                externalLink = String.format(WebEngineConfig.getBaseURL() + "/index.jsp?api_key=%s&route=%s&response_id=default_response", project.apiKey, route.name)

                writer!!.write(APIResponse("Route established ", joResp).response)
            } else {
                //Update the existing route
                route.id = routeId

                //Checking if the route had previous history
                try {

                    val isHistoryExists = RouteUpdates.instance[RouteUpdates.COLUMN_ROUTE_ID, routeId] != null

                    if (!isHistoryExists) {

                        //Add first history for further comparisons
                        val hRoute = Routes.instance.get(project.name, routeName)!!
                        RouteUpdates.instance.add(RouteUpdate(null,
                                RandomString.get(50),
                                hRoute.id!!,
                                hRoute.method!!,
                                toReadableString(hRoute),
                                if (hRoute.delay > 0) hRoute.delay.toString() else "-1",
                                hRoute.description!!,
                                hRoute.defaultResponse!!,
                                -1
                        ))
                    }

                } catch (e: QueryBuilderException) {
                    e.printStackTrace()
                }

                Routes.instance.update(route)

                subject = "Route updated - " + project.name + " / " + route.name
                message = "Route Updated"
                externalLink = String.format(
                        WebEngineConfig.getBaseURL() + "/route_update.jsp?key=%s&project_name=%s&route_name=%s",
                        updateKey, project.name, route.name
                )


                writer!!.write(APIResponse("Route updated", joResp).response)
            }


            val lastRouteUpdate = RouteUpdates.instance.getLast(RouteUpdates.COLUMN_ROUTE_ID, route.id!!)

            val newRouteUpdate = RouteUpdate(
                    null,
                    updateKey,
                    route.id!!,
                    route.method!!,
                    toReadableString(route),
                    route.delay.toString(),
                    route.description!!,
                    route.defaultResponse!!,
                    -1
            )

            val diffViews = DiffUtils.getDiffViews(lastRouteUpdate, newRouteUpdate)

            if (diffViews.isNotEmpty()) {

                println("has difference")

                try {
                    RouteUpdates.instance.add(newRouteUpdate)
                } catch (e: QueryBuilderException) {
                    e.printStackTrace()
                    throw Request.RequestException(e.message)
                }


                //About update
                if (notifyOthers && project.notificationEmails != null && !project.notificationEmails!!.trim({ it <= ' ' }).isEmpty()) {

                    Thread {


                        try {
                            MailHelper.sendMail(
                                    project.notificationEmails,
                                    subject,
                                    getMailContent(message, project.name, route.name, externalLink),
                                    "MockAPI"
                            )
                        } catch (e: MailException) {
                            e.printStackTrace()
                        }


                    }.start()

                }
            } else {
                println("No difference found")
            }
        }

    }

    private fun toReadableString(route: Route): String {
        //Adding update
        val routeParams = StringBuilder()
        for (param in route.params!!) {
            routeParams.append(param.name).append(" ").append(param.dataType).append("\n")
        }
        return routeParams.toString()
    }

    companion object {

        const val KEY_DATA_TYPES = "data_types[]"
        const val KEY_DEFAULT_VALUES = "default_values[]"
        const val KEY_DESCRIPTIONS = "descriptions[]"
        const val KEY_IS_REQUIRED = "is_required[]"
        const val KEY_PARAMS = "params[]"
        private val KEY_RESPONSE = "response"
        private val KEY_RESPONSE_ID = "response_id"
        private val KEY_NOTIFY_OTHERS = "notify_others"
        private val MAIL_CONTENT = "<html style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <head style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <link href=\"https://fonts.googleapis.com/css?family=Roboto:400,500,700\" rel=\"stylesheet\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <style style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > * { margin: 0 auto; padding: 0; } p, a { font-family: 'Roboto', sans-serif; } body { background: #171717; } div#body { text-align: center; color: white; padding: 50px; height: 158px; } a { text-decoration: none; } a#link { background: #6ac045; border-radius: 3px; -webkit-border-radius: 3px; -moz-border-radius: 3px; color: white !important; padding: 8px 34px; font-weight: 800; } div#header { background-color: #1d1d1d; width: 100%; height: 80px; border-bottom: 1px solid #2f2f2f; } div#footer { background-color: #1d1d1d; width: 100%; text-align: center; border-top: 1px solid #2f2f2f; position: absolute; bottom: 0px; } p#credits { padding: 10px; color: #565656; } p#credits a { color: #565656; } p.title { color: #ffffff; font-size: 26px; padding: 26px; } p.title2 { color: #ffffff; font-size: 26px; padding: 26px; font-weight: 600; } span.sub_title { font-size: 15px; } </style> </head> <body style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#171717;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;\" > <div id=\"header\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#1d1d1d;width:100%;height:80px;border-bottom-width:1px;border-bottom-style:solid;border-bottom-color:#2f2f2f;\" > <p class=\"title\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-family:'Roboto', sans-serif;color:#ffffff;font-size:26px;padding-top:26px;padding-bottom:26px;padding-right:26px;padding-left:26px;\" >MockAPI <span id=\"sub_title\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" >- PROJECT_NAME</span></p></div> <div id=\"body\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:50px;padding-bottom:50px;padding-right:50px;padding-left:50px;text-align:center;color:white;height:158px;\" > <p class=\"title2\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-family:'Roboto', sans-serif;color:#ffffff;font-size:26px;padding-top:26px;padding-bottom:26px;padding-right:26px;padding-left:26px;font-weight:600;\" >MESSAGE_HEADING</p> <br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <p id=\"verify_instruction\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-size:18px;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;font-family:'Roboto', sans-serif;\" > /ROUTE_NAME </p><br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" ><br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <a id=\"link\" href=\"EXTERNAL_LINK\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:8px;padding-bottom:8px;padding-right:34px;padding-left:34px;font-family:'Roboto', sans-serif;text-decoration:none;background-color:#6ac045;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;border-radius:3px;-webkit-border-radius:3px;-moz-border-radius:3px;font-weight:800;color:white !important;\" >Watch!</a> </div> <div id=\"footer\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#1d1d1d;width:100%;text-align:center;border-top-width:1px;border-top-style:solid;border-top-color:#2f2f2f;position:absolute;bottom:0px;\" > <p id=\"credits\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:10px;padding-bottom:10px;padding-right:10px;padding-left:10px;font-family:'Roboto', sans-serif;color:#565656;\" ><a target=\"_blank\" href=\"https://github.com/theapache64/Mock-API\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;font-family:'Roboto', sans-serif;text-decoration:none;color:#565656;\" > A Github Project</a></p></div> </body> </html>"

        private fun getMailContent(messageHeading: String, projectName: String?, routeName: String, externalLink: String): String {

            return MAIL_CONTENT
                    .replace("PROJECT_NAME", projectName!!)
                    .replace("MESSAGE_HEADING", messageHeading)
                    .replace("ROUTE_NAME", routeName)
                    .replace("EXTERNAL_LINK", externalLink)
        }
    }
}
