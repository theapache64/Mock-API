package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.models.Project;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
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
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {
        final String column = getStringParameter(KEY_COLUMN);
        final String value = getStringParameter(KEY_VALUE);

        if (column.equals(Projects.COLUMN_BASE_OG_API_URL) && !value.matches(URL_REGEX)) {
            throw new RequestException("Invalid URL");
        }

        final Projects projectsTable = Projects.getInstance();
        projectsTable.update(Projects.COLUMN_ID, getHeaderSecurity().getProjectId(), column, value);

        if (column.equals(Projects.COLUMN_BASE_OG_API_URL)) {
            //update project url
            final Project theProject = (Project) getHttpServletRequest().getSession().getAttribute(Project.KEY);

            //updating all old instance of string with new route
            JSONS.getInstance().updateBaseOGAPIURL(getHeaderSecurity().getProjectId(), theProject.getBaseOgApiUrl(), value);

            theProject.setBaseOgApiUrl(value);
        }

        getWriter().write(new APIResponse("Project updated", null).getResponse());


    }
}
