package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.RouteUpdates;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Project;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.models.RouteUpdate;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.MailHelper;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.exceptions.MailException;
import com.theah64.webengine.utils.CommonUtils;
import com.theah64.webengine.utils.RandomString;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.theah64.mock_api.servlets.FetchJSONServlet.KEY_DUMMY_PARAMS;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/save_json"})
public class SaveJSONServlet extends AdvancedBaseServlet {

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESPONSE_ID = "response_id";

    public static final String KEY_DATA_TYPES = "data_types[]";
    public static final String KEY_DEFAULT_VALUES = "default_values[]";
    public static final String KEY_DESCRIPTIONS = "descriptions[]";
    public static final String KEY_IS_REQUIRED = "is_required[]";

    public static final String KEY_PARAMS = "params[]";

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

        String routeId = Routes.getInstance().get(Routes.COLUMN_NAME, routeName, Routes.COLUMN_PROJECT_ID, projectId, Routes.COLUMN_ID);

        final String responseId = getStringParameter(KEY_RESPONSE_ID);
        final String response = getStringParameter(KEY_RESPONSE);
        final String method = getStringParameter(Routes.COLUMN_METHOD);

        //Validation
        if (CommonUtils.isJSONValid(response)) {

            String defaultResponse = null;
            if (responseId.equals(Routes.COLUMN_DEFAULT_RESPONSE)) {
                defaultResponse = response;
            } else {
                Responses.getInstance().update(Responses.COLUMN_ID, responseId, Responses.COLUMN_RESPONSE, response);
            }

            final List<Param> params = new ArrayList<>();

            final String paramNames[] = getStringParameterArray(KEY_PARAMS);
            final String paramDataTypes[] = getStringParameterArray(KEY_DATA_TYPES);
            final String paramDefaultValues[] = getStringParameterArray(KEY_DEFAULT_VALUES);
            final String paramDescriptions[] = getStringParameterArray(KEY_DESCRIPTIONS);
            final String paramIsRequired[] = getStringParameterArray(KEY_IS_REQUIRED);


            if (paramNames != null) {


                for (int i = 0; i < paramNames.length; i++) {

                    final String paramName = paramNames[i].replaceAll("\\s+", "_");
                    final String paramDataType = paramDataTypes[i];
                    final String paramDefaultValue = paramDefaultValues[i];
                    final String paramDescription = paramDescriptions[i];

                    boolean isRequired = paramIsRequired[i].equals("true");
                    System.out.println(paramName + ":" + paramIsRequired[i]);

                    if (!paramName.trim().isEmpty()) {
                        params.add(new Param(null, paramName, routeId, paramDataType, paramDefaultValue, paramDescription, isRequired));
                    }
                }
            }


            final String description = getStringParameter(Routes.COLUMN_DESCRIPTION);
            final boolean isSecure = getBooleanParameter(Routes.COLUMN_IS_SECURE);
            final long delay = getLongParameter(Routes.COLUMN_DELAY);
            final String externalApiUrl = getStringParameter(Routes.COLUMN_EXTERNAL_API_URL);

            if (externalApiUrl != null && !externalApiUrl.matches(URL_REGEX)) {
                throw new Request.RequestException("Invalid external api url :" + externalApiUrl);
            }


            final Route route = new Route(null, projectId, routeName, defaultResponse, description, externalApiUrl, method, params, isSecure, delay, -1);

            final JSONObject joResp = new JSONObject();
            joResp.put(KEY_DUMMY_PARAMS, route.getDummyRequiredParams());

            Project project = Projects.getInstance().get(Projects.COLUMN_ID, projectId);

            String subject, message;

            if (routeId == null) {

                //Route doesn't exist
                routeId = Routes.getInstance().addv3(route);
                route.setId(routeId);

                joResp.put(Routes.COLUMN_ID, routeId);

                subject = "Route established - " + project.getName();
                message = "Route established : " + route.getName();

                getWriter().write(new APIResponse("Route established ", joResp).getResponse());
            } else {
                //Update the existing route
                route.setId(routeId);
                Routes.getInstance().update(route);

                subject = "Route updated - " + project.getName();
                message = "Route updated : " + route.getName();


                getWriter().write(new APIResponse("Route updated", joResp).getResponse());
            }


            final String updateKey = RandomString.get(50);

            final StringBuilder routeParams = new StringBuilder();
            for (Param param : route.getParams()) {
                routeParams.append(param.getName()).append(" ").append(param.getDataType()).append("\n");
            }

            try {
                RouteUpdates.getInstance().add(new RouteUpdate(null, updateKey, route.getId(), route.getMethod(), routeParams.toString(),
                        route.getDelay() > 0 ? String.valueOf(route.getDelay()) : null,
                        route.getDescription(), route.getDefaultResponse()
                ));
            } catch (QueryBuilderException e) {
                e.printStackTrace();
                throw new Request.RequestException(e.getMessage());
            }

            //Route established
            if (project.getNotificationEmails() != null && !project.getNotificationEmails().trim().isEmpty()) {

                new Thread(() -> {

                    try {
                        MailHelper.sendMail(
                                project.getNotificationEmails(),
                                subject,
                                message,
                                "MockAPI"
                        );
                    } catch (MailException e) {
                        e.printStackTrace();
                    }

                }).start();

            }
        }

    }


}
