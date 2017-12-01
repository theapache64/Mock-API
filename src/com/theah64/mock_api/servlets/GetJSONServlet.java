package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.HeaderSecurity;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/get_json/*"})
public class GetJSONServlet extends AdvancedBaseServlet {

    private Route route;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {

        try {
            final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, PathInfo.UNLIMITED);
            final String projectName = pathInfo.getPart(1);
            final String routeName = pathInfo.getPartFrom(2);
            route = Routes.getInstance().get(projectName, routeName);
            System.out.println("Required param is " + route.getRequiredParams());
            if (route.getRequiredParams() != null) {
                final String[] reqParams = route.getRequiredParams().split(",");
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
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {

        if (route.isSecure()) {
            final String authorization = getHttpServletRequest().getHeader(HeaderSecurity.KEY_AUTHORIZATION);
            if (authorization == null) {
                throw new Request.RequestException("Authorization header missing");
            }
        }

        if (route.getDelay() > 0) {
            try {
                System.out.println("Sleep for " + route.getDelay() + "ms");
                Thread.sleep(route.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String jsonResp = route.getDefaultResponse();

        if (route.getRequiredParams() != null) {
            final String[] reqParams = route.getRequiredParams().split(",");
            for (final String reqParam : reqParams) {
                final String value = getStringParameter(reqParam);
                jsonResp = jsonResp.replace("{" + reqParam + "}", value);
            }
        }

        if (route.getOptionalParams() != null) {
            final String[] optParams = route.getOptionalParams().split(",");
            for (final String optParam : optParams) {
                final String value = getStringParameter(optParam);
                if (value != null && !value.trim().isEmpty()) {
                    jsonResp = jsonResp.replace("{" + optParam + "}", value);
                }
            }
        }

        getWriter().write(new JSONObject(jsonResp).toString());
    }
}
