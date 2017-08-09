package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 6/7/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/update_project"})
public class UpdateProjectServlet extends AdvancedBaseServlet {

    private static final String KEY_COLUMN = "column";
    private static final String KEY_VALUE = "value";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{KEY_COLUMN, KEY_VALUE};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {
        final String column = getStringParameter(KEY_COLUMN);
        final String value = getStringParameter(KEY_VALUE);

        if (column.equals(Projects.COLUMN_BASE_OG_API_URL) && !value.matches(URL_REGEX)) {
            throw new RequestException("Invalid URL");
        }

        System.out.println("Updating project");

        final Projects projectsTable = Projects.getInstance();
        final String id = getHeaderSecurity().getProjectId();
        projectsTable.update(Projects.COLUMN_ID, id, column, value);

        if (column.equals(Projects.COLUMN_BASE_OG_API_URL)) {

            System.out.println("Updating og base api url");

            //update project url
            final String oldBaseUrl = projectsTable.get(Projects.COLUMN_ID, id, Projects.COLUMN_BASE_OG_API_URL, true);

            //updating all old instance of string with new route
            JSONS.getInstance().updateBaseOGAPIURL(id, oldBaseUrl, value);
        }

        getWriter().write(new APIResponse("Project updated", null).getResponse());


    }
}
