package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.RouteUpdates;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.*;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.DiffUtils;
import com.theah64.mock_api.utils.MailHelper;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.exceptions.MailException;
import com.theah64.webengine.utils.CommonUtils;
import com.theah64.webengine.utils.RandomString;
import com.theah64.webengine.utils.Request;
import com.theah64.webengine.utils.WebEngineConfig;
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

    public static final String KEY_DATA_TYPES = "data_types[]";
    public static final String KEY_DEFAULT_VALUES = "default_values[]";
    public static final String KEY_DESCRIPTIONS = "descriptions[]";
    public static final String KEY_IS_REQUIRED = "is_required[]";
    public static final String KEY_PARAMS = "params[]";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESPONSE_ID = "response_id";
    private static final String KEY_NOTIFY_OTHERS = "notify_others";
    private static final String MAIL_CONTENT = "<html style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <head style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <link href=\"https://fonts.googleapis.com/css?family=Roboto:400,500,700\" rel=\"stylesheet\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <style style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > * { margin: 0 auto; padding: 0; } p, a { font-family: 'Roboto', sans-serif; } body { background: #171717; } div#body { text-align: center; color: white; padding: 50px; height: 158px; } a { text-decoration: none; } a#link { background: #6ac045; border-radius: 3px; -webkit-border-radius: 3px; -moz-border-radius: 3px; color: white !important; padding: 8px 34px; font-weight: 800; } div#header { background-color: #1d1d1d; width: 100%; height: 80px; border-bottom: 1px solid #2f2f2f; } div#footer { background-color: #1d1d1d; width: 100%; text-align: center; border-top: 1px solid #2f2f2f; position: absolute; bottom: 0px; } p#credits { padding: 10px; color: #565656; } p#credits a { color: #565656; } p.title { color: #ffffff; font-size: 26px; padding: 26px; } p.title2 { color: #ffffff; font-size: 26px; padding: 26px; font-weight: 600; } span.sub_title { font-size: 15px; } </style> </head> <body style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#171717;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;\" > <div id=\"header\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#1d1d1d;width:100%;height:80px;border-bottom-width:1px;border-bottom-style:solid;border-bottom-color:#2f2f2f;\" > <p class=\"title\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-family:'Roboto', sans-serif;color:#ffffff;font-size:26px;padding-top:26px;padding-bottom:26px;padding-right:26px;padding-left:26px;\" >MockAPI <span id=\"sub_title\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" >- PROJECT_NAME</span></p></div> <div id=\"body\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:50px;padding-bottom:50px;padding-right:50px;padding-left:50px;text-align:center;color:white;height:158px;\" > <p class=\"title2\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-family:'Roboto', sans-serif;color:#ffffff;font-size:26px;padding-top:26px;padding-bottom:26px;padding-right:26px;padding-left:26px;font-weight:600;\" >MESSAGE_HEADING</p> <br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <p id=\"verify_instruction\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;font-size:18px;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;font-family:'Roboto', sans-serif;\" > /ROUTE_NAME </p><br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" ><br style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" > <a id=\"link\" href=\"EXTERNAL_LINK\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:8px;padding-bottom:8px;padding-right:34px;padding-left:34px;font-family:'Roboto', sans-serif;text-decoration:none;background-color:#6ac045;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;border-radius:3px;-webkit-border-radius:3px;-moz-border-radius:3px;font-weight:800;color:white !important;\" >Watch!</a> </div> <div id=\"footer\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;background-color:#1d1d1d;width:100%;text-align:center;border-top-width:1px;border-top-style:solid;border-top-color:#2f2f2f;position:absolute;bottom:0px;\" > <p id=\"credits\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:10px;padding-bottom:10px;padding-right:10px;padding-left:10px;font-family:'Roboto', sans-serif;color:#565656;\" ><a target=\"_blank\" href=\"https://github.com/theapache64/Mock-API\" style=\"margin-top:0;margin-bottom:0;margin-right:auto;margin-left:auto;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;font-family:'Roboto', sans-serif;text-decoration:none;color:#565656;\" > A Github Project</a></p></div> </body> </html>";

    private static String getMailContent(String messageHeading, String projectName, String routeName, String externalLink) {
        return MAIL_CONTENT
                .replace("PROJECT_NAME", projectName)
                .replace("MESSAGE_HEADING", messageHeading)
                .replace("ROUTE_NAME", routeName)
                .replace("EXTERNAL_LINK", externalLink);
    }

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{
                KEY_RESPONSE_ID,
                KEY_NOTIFY_OTHERS,
                KEY_RESPONSE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, QueryBuilderException {

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
            final boolean notifyOthers = getBooleanParameter(KEY_NOTIFY_OTHERS);


            if (paramNames != null) {


                for (int i = 0; i < paramNames.length; i++) {

                    final String paramName = paramNames[i].replaceAll("\\s+", "_");
                    final String paramDataType = paramDataTypes[i];
                    final String paramDefaultValue = paramDefaultValues[i];
                    final String paramDescription = paramDescriptions[i];

                    boolean isRequired = paramIsRequired[i].equals("true");


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

            String subject, message, externalLink;


            final String updateKey = RandomString.get(50);

            if (routeId == null) {

                //Route doesn't exist
                routeId = Routes.getInstance().addv3(route);
                route.setId(routeId);

                joResp.put(Routes.COLUMN_ID, routeId);

                subject = "Route established - " + project.getName() + " / " + route.getName();
                message = "Route Added";
                externalLink = String.format(WebEngineConfig.getBaseURL() + "/index.jsp?api_key=%s&route=%s&response_id=default_response", project.getApiKey(), route.getName());

                getWriter().write(new APIResponse("Route established ", joResp).getResponse());
            } else {
                //Update the existing route
                route.setId(routeId);

                //Checking if the route had previous history
                try {

                    final boolean isHistoryExists = RouteUpdates.getInstance().get(RouteUpdates.COLUMN_ROUTE_ID, routeId) != null;

                    if (!isHistoryExists) {

                        //Add first history for further comparisons
                        final Route hRoute = Routes.getInstance().get(project.getName(), routeName);
                        RouteUpdates.getInstance().add(new RouteUpdate(
                                null,
                                RandomString.get(50),
                                hRoute.getId(),
                                hRoute.getMethod(),
                                toReadableString(hRoute),
                                hRoute.getDelay() > 0 ? String.valueOf(hRoute.getDelay()) : "-1",
                                hRoute.getDescription(),
                                hRoute.getDefaultResponse(),
                                -1
                        ));
                    }

                } catch (QueryBuilderException e) {
                    e.printStackTrace();
                }

                Routes.getInstance().update(route);

                subject = "Route updated - " + project.getName() + " / " + route.getName();
                message = "Route Updated";
                externalLink = String.format(
                        WebEngineConfig.getBaseURL() + "/route_update.jsp?key=%s&project_name=%s&route_name=%s",
                        updateKey, project.getName(), route.getName()
                );


                getWriter().write(new APIResponse("Route updated", joResp).getResponse());
            }


            final RouteUpdate lastRouteUpdate = RouteUpdates.getInstance().getLast(RouteUpdates.COLUMN_ROUTE_ID, route.getId());

            final RouteUpdate newRouteUpdate = new RouteUpdate(null, updateKey, route.getId(), route.getMethod(), toReadableString(route),
                    String.valueOf(route.getDelay()),
                    route.getDescription(), route.getDefaultResponse(),
                    -1);

            final List<DiffView> diffViews = DiffUtils.getDiffViews(lastRouteUpdate, newRouteUpdate);

            if (!diffViews.isEmpty()) {

                System.out.println("has difference");

                try {
                    RouteUpdates.getInstance().add(newRouteUpdate);
                } catch (QueryBuilderException e) {
                    e.printStackTrace();
                    throw new Request.RequestException(e.getMessage());
                }


                //About update
                if (notifyOthers && project.getNotificationEmails() != null && !project.getNotificationEmails().trim().isEmpty()) {

                    new Thread(() -> {


                        try {
                            MailHelper.sendMail(
                                    project.getNotificationEmails(),
                                    subject,
                                    getMailContent(message, project.getName(), route.getName(), externalLink),
                                    "MockAPI"
                            );
                        } catch (MailException e) {
                            e.printStackTrace();
                        }

                    }).start();

                }
            } else {
                System.out.println("No difference found");
            }
        }

    }

    private String toReadableString(final Route route) {
        //Adding update
        final StringBuilder routeParams = new StringBuilder();
        for (Param param : route.getParams()) {
            routeParams.append(param.getName()).append(" ").append(param.getDataType()).append("\n");
        }
        return routeParams.toString();
    }
}
