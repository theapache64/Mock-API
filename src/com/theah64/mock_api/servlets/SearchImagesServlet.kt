package com.theah64.mock_api.servlets

import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.sql.SQLException

/**
 * Created by theapache64 on 12/12/17.
 */
@WebServlet(urlPatterns = ["/v1/search_images"])
class SearchImagesServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = false

    override val requiredParameters: Array<String>?
        @Throws(Request.RequestException::class)
        get() = arrayOf(KEY_KEYWORD)

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class, QueryBuilderException::class)
    override fun doAdvancedPost() {
        val keyword = getStringParameter(KEY_KEYWORD)
        val url = String.format("http://theapache64.com/gpix/v1/search?keyword=%s&limit=100&Authorization=GoZNYVeK9O", URLEncoder.encode(keyword, "UTF-8"))
        writer!!.write(getResponseFromURL(url))
    }

    companion object {

        private val KEY_KEYWORD = "keyword"

        @Throws(IOException::class)
        private fun getResponseFromURL(url: String): String {

            val theURL = URL(url)
            val urlCon = theURL.openConnection() as HttpURLConnection
            val br = BufferedReader(InputStreamReader(if (urlCon.responseCode == 200) urlCon.inputStream else urlCon.errorStream))


            val sb = StringBuilder()
            br.use { reader ->
                reader.lineSequence().forEach { line ->
                    sb.append(line)
                }
            }

            return sb.toString()
        }
    }
}
