package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/save_json"})
public class SaveJSONServlet extends AdvancedBaseServlet {

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESPONSE_ID = "response_id";

    public static final String KEY_REQ_DATA_TYPES = "req_data_types[]";
    public static final String KEY_REQ_DEFAULT_VALUES = "req_default_values[]";
    public static final String KEY_REQ_DESCRIPTIONS = "req_descriptions[]";

    private static final String KEY_OPT_DATA_TYPES = "opt_data_types[]";
    private static final String KEY_OPT_DEFAULT_VALUES = "opt_default_values[]";
    private static final String KEY_OPT_DESCRIPTIONS = "opt_descriptions[]";
    public static final String KEY_REQUIRED_PARAMS = "required_params[]";
    public static final String KEY_OPTIONAL_PARAMS = "optional_params[]";

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
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {

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

        final List<Param> requiredParams = new ArrayList<>();
        final List<Param> optionalParams = new ArrayList<>();

        final String reqParamNames[] = getStringParameterArray(KEY_REQUIRED_PARAMS);
        System.out.println("OK:" + Arrays.toString(reqParamNames));
        final String reqParamDataTypes[] = getStringParameterArray(KEY_REQ_DATA_TYPES);
        final String reqParamDefaultValues[] = getStringParameterArray(KEY_REQ_DEFAULT_VALUES);
        final String reqParamDescriptions[] = getStringParameterArray(KEY_REQ_DESCRIPTIONS);

        if (reqParamNames != null) {

            for (int i = 0; i < reqParamNames.length; i++) {

                final String reqParamName = reqParamNames[i].replaceAll("\\s+", "_");
                final String reqParamDataType = reqParamDataTypes[i];
                final String reqParamDefaultValue = reqParamDefaultValues[i];
                final String reqParamDescription = reqParamDescriptions[i];

                requiredParams.add(new Param(null, reqParamName, routeId, reqParamDataType, reqParamDefaultValue, reqParamDescription, true));
            }
        }

        final String optParamNames[] = getStringParameterArray(Routes.KEY_OPTIONAL_PARAMS);
        final String optParamDataTypes[] = getStringParameterArray(KEY_OPT_DATA_TYPES);
        final String optParamDefaultValues[] = getStringParameterArray(KEY_OPT_DEFAULT_VALUES);
        final String optParamDescriptions[] = getStringParameterArray(KEY_OPT_DESCRIPTIONS);

        if (optParamDataTypes != null) {

            for (int i = 0; i < optParamNames.length; i++) {

                final String optParamName = optParamNames[i].replaceAll("\\s+", "_");
                final String optParamDataType = optParamDataTypes[i];
                final String optParamDefaultValue = optParamDefaultValues[i];
                final String optParamDescription = optParamDescriptions[i];

                requiredParams.add(new Param(null, optParamName, routeId, optParamDataType, optParamDefaultValue, optParamDescription, false));
            }
        }

        final String description = getStringParameter(Routes.COLUMN_DESCRIPTION);
        final boolean isSecure = getBooleanParameter(Routes.COLUMN_IS_SECURE);
        final long delay = getLongParameter(Routes.COLUMN_DELAY);
        final String externalApiUrl = getStringParameter(Routes.COLUMN_EXTERNAL_API_URL);

        if (externalApiUrl != null && !externalApiUrl.matches(URL_REGEX)) {
            throw new Request.RequestException("Invalid external api url :" + externalApiUrl);
        }


        final Route route = new Route(null, projectId, routeName, defaultResponse, description, externalApiUrl, requiredParams, optionalParams, isSecure, delay, -1);

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
