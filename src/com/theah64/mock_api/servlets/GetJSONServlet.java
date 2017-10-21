package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.HitLogs;
import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.models.HitLog;
import com.theah64.mock_api.models.JSON;
import com.theah64.mock_api.utils.HeaderSecurity;
import com.theah64.mock_api.utils.LogBuilder;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/get_json/*"})
public class GetJSONServlet extends AdvancedBaseServlet {

    private JSON json;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {

        try {
            final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, PathInfo.UNLIMITED);
            final String projectName = pathInfo.getPart(1);
            final String route = pathInfo.getPartFrom(2);
            json = JSONS.getInstance().get(projectName, route);
            System.out.println("Required param is " + json.getRequiredParams());
            if (json.getRequiredParams() != null) {
                final String[] reqParams = json.getRequiredParams().split(",");
                for (final String reqParam : reqParams) {
                    System.out.println(reqParam);
                }
                return reqParams;
            }

        } catch (PathInfo.PathInfoException | SQLException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        setHttpServletRequest(req);

        //Building request body
        final LogBuilder logBuilder = new LogBuilder();

        try {

            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                final String name = headers.nextElement();
                logBuilder.append(name, req.getHeader(name));
            }

            logBuilder.append("PathInfo", req.getPathInfo());

            final PathInfo pathInfo = new PathInfo(req.getPathInfo(), 2, PathInfo.UNLIMITED);
            final String projectName = pathInfo.getPart(1);
            final String route = pathInfo.getPartFrom(2);
            json = JSONS.getInstance().get(projectName, route);

            logBuilder.append("Authorization", req.getHeader("Authorization"));
            logBuilder.append("Required params", json.getRequiredParams());
            logBuilder.append("-------------------------", "");

            if (json.getRequiredParams() != null) {
                final String[] reqParams = json.getRequiredParams().split(",");
                for (final String reqParam : reqParams) {
                    logBuilder.append(reqParam, req.getParameter(reqParam));
                }
            }

            //Checking if there's any error
            String errorResponse = null;
            try {
                if (getRequiredParameters() != null) {
                    setRequest(new Request(req, getRequiredParameters()));
                }

                if (json.isSecure()) {

                    //Checking if the apikey present in header
                    String apiKey = req.getHeader(HeaderSecurity.KEY_AUTHORIZATION);

                    if (apiKey == null) {
                        //not present in header, so checking on the request
                        apiKey = getStringParameter(HeaderSecurity.KEY_AUTHORIZATION);
                    }

                    new HeaderSecurity(apiKey);
                }


            } catch (Request.RequestException | HeaderSecurity.AuthorizationException e) {
                e.printStackTrace();
                errorResponse = e.getMessage();
            }

            HitLogs.getInstance().add(new HitLog(json.getId(), logBuilder.build(), errorResponse, null));

        } catch (PathInfo.PathInfoException | SQLException e) {
            e.printStackTrace();
        }


        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {

        if (json.isSecure()) {
            final String authorization = getHttpServletRequest().getHeader(HeaderSecurity.KEY_AUTHORIZATION);
            if (authorization == null) {
                throw new Request.RequestException("Authorization header missing");
            }
        }

        if (json.getDelay() > 0) {
            try {
                System.out.println("Sleep for " + json.getDelay() + "ms");
                Thread.sleep(json.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getWriter().write(new JSONObject(json.getResponse()).toString());
    }
}
