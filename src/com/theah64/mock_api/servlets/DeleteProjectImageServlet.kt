package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Images
import com.theah64.mock_api.utils.APIResponse
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.File
import java.io.IOException
import java.sql.SQLException

@WebServlet(urlPatterns = ["/v1/delete_image"])
class DeleteProjectImageServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        @Throws(Request.RequestException::class)
        get() = arrayOf(Images.COLUMN_ID)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class, QueryBuilderException::class)
    override fun doAdvancedPost() {
        val imageId = getStringParameter(Images.COLUMN_ID)!!
        val projectId = headerSecurity!!.projectId

        val filePath = Images.instance.get(Images.COLUMN_ID, imageId, Images.COLUMN_PROJECT_ID, projectId, Images.COLUMN_FILE_PATH)
        if (filePath != null) {
            val file = File(filePath)

            file.delete()

            //Delete from db also
            Images.instance.delete(Images.COLUMN_ID, imageId, Images.COLUMN_PROJECT_ID, projectId)
            writer!!.write(APIResponse("Image deleted", null).response)

        } else {
            throw Request.RequestException("Invalid request")
        }
    }
}
