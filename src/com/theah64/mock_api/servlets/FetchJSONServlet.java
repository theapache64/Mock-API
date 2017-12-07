package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Params;
import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Response;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.TimeUtils;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/fetch_json/*"})
public class FetchJSONServlet extends AdvancedBaseServlet {


    private static final String KEY_DUMMY_PARAMS = "dummy_params";
    private static final String KEY_LAST_MODIFIED = "last_modified";
    private static final String KEY_LAST_MODIFIED_DATE = "last_modified_date";
    private static final String KEY_RESPONSES = "responses";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }


    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException, QueryBuilderException {

        final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, PathInfo.UNLIMITED);
        final String projectName = pathInfo.getPart(1);
        final String routeName = pathInfo.getPartFrom(2);
        final Route route = Routes.getInstance().get(projectName, routeName);


        final JSONObject joJson = new JSONObject();

        final JSONArray jaResponses = new JSONArray();
        final List<Response> responses = Responses.getInstance().getAll(Responses.COLUMN_ROUTE_ID, route.getId());
        for (final Response response : responses) {
            final JSONObject joResponse = new JSONObject();
            joResponse.put(Responses.COLUMN_ID, response.getId());
            joResponse.put(Responses.COLUMN_NAME, response.getName());
            jaResponses.put(joResponse);
        }

        joJson.put(KEY_RESPONSES, jaResponses);

        final JSONArray jaParams = new JSONArray();

        //Adding required params
        for (final Param param : route.getParams()) {
            final JSONObject joParam = new JSONObject();
            joParam.put(Params.COLUMN_ID, param.getId());
            joParam.put(Params.COLUMN_NAME, param.getName());
            joParam.put(Params.COLUMN_DATA_TYPE, param.getDataType());
            joParam.put(Params.COLUMN_DEFAULT_VALUE, param.getDefaultValue());
            joParam.put(Params.COLUMN_DESCRIPTION, param.getDescription());
            joParam.put(Params.COLUMN_IS_REQUIRED, param.isRequired());
            jaParams.put(joParam);
        }


        joJson.put(Routes.KEY_PARAMS, jaParams);
        joJson.put(Routes.COLUMN_EXTERNAL_API_URL, route.getExternalApiUrl());
        joJson.put(Routes.COLUMN_IS_SECURE, route.isSecure());
        joJson.put(Routes.COLUMN_DELAY, route.getDelay());
        joJson.put(Routes.COLUMN_DESCRIPTION, route.getDescription());

        joJson.put(KEY_DUMMY_PARAMS, getDummyParams(route.filterRequiredParams()));
        joJson.put(KEY_LAST_MODIFIED, TimeUtils.millisToLongDHMS(route.getUpdatedInMillis()) + " ago");
        joJson.put(KEY_LAST_MODIFIED_DATE, getIndianDate(route.getUpdatedInMillis()));

        getWriter().write(new APIResponse("Response loaded", joJson).getResponse());
    }


    public static String getDummyParams(String[] requiredParams) {
        final StringBuilder dummyParamBuilder = new StringBuilder();
        for (final String param : requiredParams) {
            dummyParamBuilder.append(param).append("=sampleParam&");
        }
        return dummyParamBuilder.toString();
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private String getIndianDate(long updatedInMillis) {
        LocalDateTime ldt = LocalDateTime.parse(DATE_FORMAT.format(new Date(updatedInMillis)), DateTimeFormatter.ofPattern(DATE_FORMAT.toPattern()));
        return DATE_FORMAT.format(Date.from(ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant()));
    }
}
