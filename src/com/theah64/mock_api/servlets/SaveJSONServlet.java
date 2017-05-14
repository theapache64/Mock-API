package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.models.JSON;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/save_json"})
public class SaveJSONServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{JSONS.COLUMN_ROUTE, JSONS.COLUMN_RESPONSE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {


        final String route = getStringParameter(JSONS.COLUMN_ROUTE);
        final String projectId = getHeaderSecurity().getProjectId();

        final boolean isRouteExist = JSONS.getInstance().isExist(JSONS.COLUMN_ROUTE, route, JSONS.COLUMN_PROJECT_ID, projectId);

        final String response = getStringParameter(JSONS.COLUMN_RESPONSE);
        if (!isRouteExist) {
            //Route doesn't exist
            final String jsonId = JSONS.getInstance().addv3(new JSON(null, projectId, route, response));
            getWriter().write(new APIResponse("Route established ", JSONS.COLUMN_ID, jsonId).getResponse());
        } else {
            //Update the existing route
            JSONS.getInstance().update(JSONS.COLUMN_PROJECT_ID, projectId, JSONS.COLUMN_ROUTE, route, JSONS.COLUMN_RESPONSE, new JSONObject(response).toString());
            getWriter().write(new APIResponse("Route updated", null).getResponse());
        }
    }
}
