package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
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

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[0];
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {

        try {
            final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, 2);
            final String projectName = pathInfo.getPart(1);
            final String route = pathInfo.getPart(2);
            final String response = JSONS.getInstance().getResponse(projectName, route);
            getWriter().write(new JSONObject(response).toString());

        } catch (PathInfo.PathInfoException e) {
            e.printStackTrace();
            throw new Request.RequestException("Either project name or route missing");
        }
    }
}
