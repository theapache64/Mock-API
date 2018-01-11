package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Project;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.MailHelper;
import com.theah64.webengine.exceptions.MailException;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/delete_route"})
public class DeleteRouteServlet extends AdvancedBaseServlet {
    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Routes.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException {
        final String routeId = getStringParameter(Routes.COLUMN_ID);
        final Route route = Routes.getInstance().getRouteBy(Routes.COLUMN_ID, routeId);
        final Project project = Projects.getInstance().get(Projects.COLUMN_ID, getHeaderSecurity().getProjectId());
        Routes.getInstance().delete(Routes.COLUMN_ID, routeId, Routes.COLUMN_PROJECT_ID, getHeaderSecurity().getProjectId());
        new Thread(() -> {
            if (project.getNotificationEmails() != null) {
                try {
                    final String subMes = "Route deleted: " + project.getName() + " / " + route.getName();
                    MailHelper.sendMail(project.getNotificationEmails(), subMes, subMes, "MockAPI");
                } catch (MailException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        getWriter().write(new APIResponse("Route deleted", null).getResponse());
    }
}
