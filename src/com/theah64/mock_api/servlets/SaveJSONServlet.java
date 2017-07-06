package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.models.JSON;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;

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
        return new String[]{
                JSONS.COLUMN_ROUTE,
                JSONS.COLUMN_RESPONSE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException {

        final String route = getStringParameter(JSONS.COLUMN_ROUTE);
        final String projectId = getHeaderSecurity().getProjectId();

        final boolean isRouteExist = JSONS.getInstance().isExist(JSONS.COLUMN_ROUTE, route, JSONS.COLUMN_PROJECT_ID, projectId);

        final String response = getStringParameter(JSONS.COLUMN_RESPONSE);
        String requiredParams = getStringParameter(JSONS.COLUMN_REQUIRED_PARAMS);
        String optionalParams = getStringParameter(JSONS.COLUMN_OPTIONAL_PARAMS);
        final String description = getStringParameter(JSONS.COLUMN_DESCRIPTION);
        final boolean isSecure = getBooleanParameter(JSONS.COLUMN_IS_SECURE);
        final long delay = getLongParameter(JSONS.COLUMN_DELAY);
        final String externalApiUrl = getStringParameter(JSONS.COLUMN_EXTERNAL_API_URL);

        if (externalApiUrl != null && !externalApiUrl.matches(URL_REGEX)) {
            throw new RequestException("Invalid external api url :" + externalApiUrl);
        }

        if (requiredParams.trim().isEmpty()) {
            requiredParams = null;
        } else {
            requiredParams = requiredParams.replaceAll("\\s+", "_");
        }

        if (optionalParams.trim().isEmpty()) {
            optionalParams = null;
        } else {
            optionalParams = optionalParams.replaceAll("\\s+", "_");
        }

        final JSON json = new JSON(null, projectId, route, response, requiredParams, optionalParams, description, externalApiUrl, isSecure, delay);

        if (!isRouteExist) {
            //Route doesn't exist
            final String jsonId = JSONS.getInstance().addv3(json);
            getWriter().write(new APIResponse("Route established ", JSONS.COLUMN_ID, jsonId).getResponse());
        } else {
            //Update the existing route
            JSONS.getInstance().update(json);
            getWriter().write(new APIResponse("Route updated", null).getResponse());
        }
    }
}
