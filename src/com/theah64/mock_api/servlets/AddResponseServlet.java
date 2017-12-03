package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.models.Response;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 1/12/17.
 */
@WebServlet(urlPatterns = AdvancedBaseServlet.VERSION_CODE + "/add_response")
public class AddResponseServlet extends AdvancedBaseServlet {

    private static final String KEY_ROUTE = "route";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Responses.COLUMN_NAME, Routes.COLUMN_PROJECT_ID, KEY_ROUTE, Responses.COLUMN_RESPONSE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException, QueryBuilderException {


        final String name = getStringParameter(Responses.COLUMN_NAME);
        final String routeName = getStringParameter(KEY_ROUTE);
        final String response = getStringParameter(Responses.COLUMN_RESPONSE);
        final String projectId = getStringParameter(Routes.COLUMN_PROJECT_ID);
        final String routeId = Routes.getInstance().get(Routes.COLUMN_PROJECT_ID, projectId, Routes.COLUMN_NAME, routeName, Routes.COLUMN_ID);
        final String respId = Responses.getInstance().addv3(new Response(null, name, routeId, response));
        getWriter().write(new APIResponse("Response added", "id", respId).getResponse());
    }
}