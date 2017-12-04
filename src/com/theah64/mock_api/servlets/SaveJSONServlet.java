package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.APIResponse;
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

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESPONSE_ID = "response_id";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{
                KEY_RESPONSE_ID,
                KEY_RESPONSE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException {

        final String routeName = getStringParameter(Routes.COLUMN_NAME);
        final String projectId = getHeaderSecurity().getProjectId();

        final String routeId = Routes.getInstance().get(Routes.COLUMN_NAME, routeName, Routes.COLUMN_PROJECT_ID, projectId, Routes.COLUMN_ID);

        final String responseId = getStringParameter(KEY_RESPONSE_ID);
        final String response = getStringParameter(KEY_RESPONSE);
        String defaultResponse = null;
        if (responseId.equals(Routes.COLUMN_DEFAULT_RESPONSE)) {
            defaultResponse = response;
        } else {
            Responses.getInstance().update(Responses.COLUMN_ID, responseId, Responses.COLUMN_RESPONSE, response);
        }

        String requiredParams = getStringParameter(Routes.COLUMN_REQUIRED_PARAMS);
        String optionalParams = getStringParameter(Routes.COLUMN_OPTIONAL_PARAMS);
        final String description = getStringParameter(Routes.COLUMN_DESCRIPTION);
        final boolean isSecure = getBooleanParameter(Routes.COLUMN_IS_SECURE);
        final long delay = getLongParameter(Routes.COLUMN_DELAY);
        final String externalApiUrl = getStringParameter(Routes.COLUMN_EXTERNAL_API_URL);

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

        final Route route = new Route(null, projectId, routeName, defaultResponse, requiredParams, optionalParams, description, externalApiUrl, isSecure, delay, -1);

        if (routeId == null) {
            //Route doesn't exist
            final String jsonId = Routes.getInstance().addv3(route);
            getWriter().write(new APIResponse("Route established ", Routes.COLUMN_ID, jsonId).getResponse());
        } else {
            //Update the existing route
            route.setId(routeId);
            Routes.getInstance().update(route);
            getWriter().write(new APIResponse("Route updated", new JSONObject()).getResponse());
        }
    }
}
