package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_response"})
public class GetResponseServlet extends AdvancedBaseServlet {

    private static final String KEY_ROUTE_NAME = "route_name";
    private static final String KEY_PROJECT_NAME = "project_name";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Responses.COLUMN_ID, KEY_ROUTE_NAME, KEY_PROJECT_NAME};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException, QueryBuilderException {

        final String responseId = getStringParameter(Responses.COLUMN_ID);
        String resp;


        if (responseId.equals(Routes.COLUMN_DEFAULT_RESPONSE)) {
            final String routeName = getStringParameter(KEY_ROUTE_NAME);
            final String projectName = getStringParameter(KEY_PROJECT_NAME);
            resp = Routes.getInstance().get(projectName, routeName).getDefaultResponse();
        } else {
            resp = Responses.getInstance().get(
                    Responses.COLUMN_ID,
                    responseId,
                    Responses.COLUMN_RESPONSE,
                    false
            );
        }


        if (resp != null) {

            final JSONObject joData = new JSONObject();
            joData.put(Responses.COLUMN_ID, responseId);
            joData.put(Responses.COLUMN_RESPONSE, resp);

            getWriter().write(new APIResponse("OK", joData).getResponse());
        } else {
            getWriter().write(new APIResponse("Invalid response id").getResponse());
        }

    }
}
