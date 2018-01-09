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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return new String[]{
                Projects.COLUMN_PACKAGE_NAME,
                Projects.COLUMN_BASE_OG_API_URL,
                Projects.COLUMN_IS_ALL_SMALL_ROUTES,
                Projects.COLUMN_NOTIFICATION_EMAILS
        };

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException, QueryBuilderException {

        final String packageName = getStringParameter(Projects.COLUMN_PACKAGE_NAME);
        final String baseOgAPIUrl = getStringParameter(Projects.COLUMN_BASE_OG_API_URL);
        final boolean isAllSmallRoutes = getBooleanParameter(Projects.COLUMN_IS_ALL_SMALL_ROUTES);
        final String emailNotifications = getStringParameter(Projects.COLUMN_NOTIFICATION_EMAILS);

        if (!baseOgAPIUrl.matches(URL_REGEX)) {
            throw new Request.RequestException("Invalid URL passed for base og API URL");
        }

        final String[] emails = emailNotifications.split(",");
        StringBuilder emailBuilder = new StringBuilder();

        for (final String email : emails) {
            if (isValidEmail(email)) {
                if (emailBuilder.indexOf(email) == -1) {
                    emailBuilder
                            .append(email)
                            .append(",");
                }
            } else {
                throw new Request.RequestException("Invalid email :" + email);
            }
        }

        if (emailBuilder.length() == 0) {
            emailBuilder = null;
        }


        final Projects projectsTable = Projects.getInstance();
        final String id = getHeaderSecurity().getProjectId();
        final Project project = projectsTable.get(Projects.COLUMN_ID, id);
        final String oldBaseUrl = project.getBaseOgApiUrl();

        //Setting new values
        project.setBaseOgApiUrl(baseOgAPIUrl);
        project.setPackageName(packageName);
        project.setAllSmallRoutes(isAllSmallRoutes);
        project.setNotificationEmails(emailBuilder != null ? emailBuilder.substring(0, emailBuilder.length() - 1) : null);


        projectsTable.update(project);
        Routes.getInstance().updateBaseOGAPIURL(id, oldBaseUrl, baseOgAPIUrl);

        getWriter().write(new APIResponse(
                "Project updated",
                null).getResponse()
        );

    }
}
