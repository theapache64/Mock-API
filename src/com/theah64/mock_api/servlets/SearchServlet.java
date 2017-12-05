package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by theapache64 on 21/9/17.
 */
@WebServlet(urlPatterns = AdvancedBaseServlet.VERSION_CODE + "/search")
public class SearchServlet extends AdvancedBaseServlet {

    private static final String KEY_COLUMN = "column";
    private static final String KEY_VALUE = "value";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{KEY_COLUMN, KEY_VALUE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException {


        final String projectId = getHeaderSecurity().getProjectId();
        final String column = getStringParameter(KEY_COLUMN);
        final String value = getStringParameter(KEY_VALUE);
        final List<String> routes = Routes.getInstance().getLike(Routes.COLUMN_PROJECT_ID, projectId, column, value, Routes.COLUMN_NAME);
        if (routes != null) {
            final JSONArray jaRoutes = new JSONArray();
            for (final String route : routes) {
                jaRoutes.put(route);
            }
            final JSONObject joData = new JSONObject();
            joData.put("routes", jaRoutes);
            getWriter().write(new APIResponse(routes.size() + " route(s) found", joData).getResponse());
        } else {
            throw new Request.RequestException("No match found");
        }


    }
}
