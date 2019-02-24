package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Images
import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.CommonUtils
import com.theah64.mock_api.utils.TinifyUtils
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.RandomString
import com.theah64.webengine.utils.Request
import org.json.JSONException
import org.json.JSONObject

import javax.servlet.annotation.WebServlet
import java.io.File
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 12/12/17.
 */
@WebServlet(urlPatterns = ["/v1/add_to_project_images"])
class AddToProjectImagesServlet : AdvancedBaseServlet() {


    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf(Images.COLUMN_THUMB_URL, Images.COLUMN_IMAGE_URL, KEY_IS_COMPRESS)

    @Throws(
            IOException::class,
            JSONException::class,
            SQLException::class,
            Request.RequestException::class,
            PathInfo.PathInfoException::class,
            QueryBuilderException::class
    )
    override fun doAdvancedPost() {
        val thumbUrl = getStringParameter(Images.COLUMN_THUMB_URL)!!
        val imageUrl = getStringParameter(Images.COLUMN_IMAGE_URL)!!
        val isCompress = getBooleanParameter(KEY_IS_COMPRESS)
        val dataDir = CommonUtils.getDataDir()
        val imageFile = File(dataDir.absolutePath + File.separator + RandomString.get(10))

        val id = TinifyUtils.manage(headerSecurity!!.projectId, imageUrl, thumbUrl, imageFile.absolutePath, isCompress)

        val joImage = JSONObject()
        joImage.put(Images.COLUMN_IMAGE_URL, imageUrl)
        joImage.put(Images.COLUMN_THUMB_URL, thumbUrl)
        joImage.put(Images.COLUMN_ID, id)

        writer!!.write(APIResponse("OK", joImage).response)
    }

    companion object {

        private val KEY_IS_COMPRESS = "is_compress"
    }
}
