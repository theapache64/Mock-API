package com.theah64.mock_api.servlets;

import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.HeaderSecurity;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by shifar on 16/9/16.
 */
public abstract class AdvancedBaseServlet extends HttpServlet {

    protected static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";


    public static final String VERSION_CODE = "/v1";
    protected static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ERROR_GET_NOT_SUPPORTED = "GET method not supported";
    private static final String ERROR_POST_NOT_SUPPORTED = "POST method not supported";
    private Request request;
    private HeaderSecurity hs;
    private PrintWriter out;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    protected static void setGETMethodNotSupported(HttpServletResponse response) throws IOException {
        notSupported(ERROR_GET_NOT_SUPPORTED, response);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    protected static void POSTMethodNotSupported(HttpServletResponse response) throws IOException {
        notSupported(ERROR_POST_NOT_SUPPORTED, response);
    }

    private static void notSupported(String methodErrorMessage, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        final PrintWriter out = response.getWriter();

        //GET Method not supported
        out.write(new APIResponse(methodErrorMessage).getResponse());
    }

    public PrintWriter getWriter() {
        return out;
    }


    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(getContentType());


        this.httpServletRequest = req;
        this.httpServletResponse = resp;

        out = resp.getWriter();

        try {

            if (getRequiredParameters() != null) {
                request = new Request(req, getRequiredParameters());
            }


            if (isSecureServlet()) {

                //Checking if the apikey present in header
                String apiKey = req.getHeader(HeaderSecurity.KEY_AUTHORIZATION);

                if (apiKey == null) {
                    //not present in header, so checking on the request
                    apiKey = getStringParameter(HeaderSecurity.KEY_AUTHORIZATION);
                }

                hs = new HeaderSecurity(apiKey);
            }


            doAdvancedPost();

        } catch (Throwable e) {
            e.printStackTrace();
            out.write(new APIResponse(e.getMessage()).toString());
        }
    }

    private String getContentType() {
        return CONTENT_TYPE_JSON;
    }

    protected abstract boolean isSecureServlet();

    protected abstract String[] getRequiredParameters() throws Request.RequestException;

    protected abstract void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException;

    HeaderSecurity getHeaderSecurity() {
        if (!isSecureServlet()) {
            throw new IllegalArgumentException("It's not a secure servlet");
        }
        return hs;
    }

    String getStringParameter(String key) {
        return request.getStringParameter(key);
    }

    public int getIntParameter(String key, int defValue) {

        final String value = getStringParameter(key);

        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return defValue;
    }

    boolean getBooleanParameter(String key) {
        return request.getBooleanParameter(key);
    }

    public boolean has(String key) {
        return request.has(key);
    }

    HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    long getLongParameter(String key) {
        return request.getLongParameter(key);
    }

    public String[] getStringParameterArray(String key) {
        return request.getStringParameters(key);
    }


    public class ServletException extends Exception {
        public ServletException(String message) {
            super(message);
        }
    }
}
