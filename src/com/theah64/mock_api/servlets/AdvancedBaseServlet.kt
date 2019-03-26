package com.theah64.mock_api.servlets

import com.theah64.mock_api.utils.APIResponse
import com.theah64.mock_api.utils.HeaderSecurity
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException
import java.io.IOException
import java.io.PrintWriter
import java.sql.SQLException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by shifar on 16/9/16.
 */
abstract class AdvancedBaseServlet : HttpServlet() {

    var request: Request? = null
    private var hs: HeaderSecurity? = null
    var writer: PrintWriter? = null
        private set
    internal var httpServletRequest: HttpServletRequest? = null
        set
    var httpServletResponse: HttpServletResponse? = null
        private set

    private val contentType: String
        get() = CONTENT_TYPE_JSON

    protected abstract val isSecureServlet: Boolean

    protected abstract val requiredParameters: Array<String>?

    internal val headerSecurity: HeaderSecurity?
        get() {
            if (!isSecureServlet) {
                throw IllegalArgumentException("It's not a secure servlet")
            }
            return hs
        }

    open fun isJsonBody(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = contentType


        this.httpServletRequest = req
        this.httpServletResponse = resp

        writer = resp.writer

        try {

            if (requiredParameters != null) {
                val isJsonBody = isJsonBody()
                request = Request(req, requiredParameters, isJsonBody)
            }


            if (isSecureServlet) {

                //Checking if the apikey present in header
                var apiKey: String? = req.getHeader(HeaderSecurity.KEY_AUTHORIZATION)

                if (apiKey == null) {
                    //not present in header, so checking on the request
                    apiKey = getStringParameter(HeaderSecurity.KEY_AUTHORIZATION)
                }

                hs = HeaderSecurity(apiKey)
            }


            doAdvancedPost()

        } catch (e: Throwable) {
            e.printStackTrace()
            writer!!.write(APIResponse(e.message).toString())
        }

    }

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class, QueryBuilderException::class)
    protected abstract fun doAdvancedPost()

    internal fun getStringParameter(key: String): String? {
        return request?.getStringParameter(key)
    }

    fun getIntParameter(key: String, defValue: Int): Int {

        val value = getStringParameter(key)

        if (value != null) {
            try {
                return Integer.parseInt(value)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        }

        return defValue
    }

    internal fun getBooleanParameter(key: String): Boolean {
        return request?.getBooleanParameter(key) ?: false
    }

    fun has(key: String): Boolean {
        return request?.has(key) ?: false
    }

    internal fun getLongParameter(key: String): Long {
        return request?.getLongParameter(key) ?: -1
    }

    fun getStringParameterArray(key: String): Array<String> {
        return request?.getStringParameters(key) ?: arrayOf()
    }


    inner class ServletException(message: String) : Exception(message)

    companion object {

        const val URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"

        private val CONTENT_TYPE_JSON = "application/json"
        private val ERROR_GET_NOT_SUPPORTED = "GET method not supported"
        private val ERROR_POST_NOT_SUPPORTED = "POST method not supported"

        @Throws(IOException::class)
        internal fun setGETMethodNotSupported(response: HttpServletResponse) {
            notSupported(ERROR_GET_NOT_SUPPORTED, response)
        }

        @Throws(IOException::class)
        protected fun POSTMethodNotSupported(response: HttpServletResponse) {
            notSupported(ERROR_POST_NOT_SUPPORTED, response)
        }

        @Throws(IOException::class)
        private fun notSupported(methodErrorMessage: String, response: HttpServletResponse) {
            response.contentType = CONTENT_TYPE_JSON
            val out = response.writer

            //GET Method not supported
            out.write(APIResponse(methodErrorMessage).response)
        }
    }
}
