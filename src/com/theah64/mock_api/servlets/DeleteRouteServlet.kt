package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Projects
import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.MailHelper
import com.theah64.webengine.exceptions.MailException
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = ["/v1/delete_route"])
class DeleteRouteServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(Routes.COLUMN_ID)

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class)
    override fun doAdvancedPost() {
        val routeId = getStringParameter(Routes.COLUMN_ID)!!
        val route = Routes.instance.getRouteBy(Routes.COLUMN_ID, routeId)
        val project = Projects.instance[Projects.COLUMN_ID, headerSecurity!!.projectId]!!
        Routes.instance.delete(Routes.COLUMN_ID, routeId, Routes.COLUMN_PROJECT_ID, headerSecurity!!.projectId)
        Thread {
            if (project.notificationEmails != null) {
                try {
                    val subMes = "Route deleted: " + project.name + " / " + route.name
                    MailHelper.sendMail(project.notificationEmails, subMes, subMes, "MockAPI")
                } catch (e: MailException) {
                    e.printStackTrace()
                }

            }
        }.start()
        writer!!.write(APIResponse("Route deleted", null).response)
    }
}
