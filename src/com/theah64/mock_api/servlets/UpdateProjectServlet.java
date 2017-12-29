package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Project;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
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


    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Projects.COLUMN_PACKAGE_NAME, Projects.COLUMN_BASE_OG_API_URL};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException, QueryBuilderException {

        final String packageName = getStringParameter(Projects.COLUMN_PACKAGE_NAME);
        final String baseOgAPIUrl = getStringParameter(Projects.COLUMN_BASE_OG_API_URL);

        if (!baseOgAPIUrl.matches(URL_REGEX)) {
            throw new Request.RequestException("Invalid URL passed for base og API URL");
        }


        final Projects projectsTable = Projects.getInstance();
        final String id = getHeaderSecurity().getProjectId();
        final Project project = projectsTable.get(Projects.COLUMN_ID, id);
        final String oldBaseUrl = project.getBaseOgApiUrl();

        //Setting new values
        project.setBaseOgApiUrl(baseOgAPIUrl);
        project.setPackageName(packageName);
        projectsTable.update(project);
        Routes.getInstance().updateBaseOGAPIURL(id, oldBaseUrl, baseOgAPIUrl);

        getWriter().write(new APIResponse("Project updated", null).getResponse());
    }
}
