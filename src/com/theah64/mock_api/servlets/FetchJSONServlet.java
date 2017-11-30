package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.theah64.mock_api.utils.TimeUtils;
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

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/fetch_json/*"})
public class FetchJSONServlet extends AdvancedBaseServlet {


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
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException {

        final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, PathInfo.UNLIMITED);
        final String projectName = pathInfo.getPart(1);
        final String routeName = pathInfo.getPartFrom(2);
        final Route route = Routes.getInstance().get(projectName, routeName);

        final JSONObject joJson = new JSONObject();
        joJson.put(Routes.COLUMN_DEFAULT_RESPONSE, route.getDefaultResponse());
        joJson.put(Routes.COLUMN_REQUIRED_PARAMS, route.getRequiredParams());
        joJson.put(Routes.COLUMN_OPTIONAL_PARAMS, route.getOptionalParams());
        joJson.put(Routes.COLUMN_EXTERNAL_API_URL, route.getExternalApiUrl());
        joJson.put(Routes.COLUMN_IS_SECURE, route.isSecure());
        joJson.put(Routes.COLUMN_DELAY, route.getDelay());
        joJson.put(Routes.COLUMN_DESCRIPTION, route.getDescription());
        joJson.put("dummy_params", getDummyParams(route.getRequiredParams()));
        joJson.put("last_modified", TimeUtils.millisToLongDHMS(route.getUpdatedInMillis()) + " ago");
        joJson.put("last_modified_date", getIndianDate(route.getUpdatedInMillis()));

        getWriter().write(new APIResponse("Response loaded", joJson).getResponse());
    }

    public static String getDummyParams(String requiredParams) {
        if (requiredParams != null && !requiredParams.isEmpty()) {
            return requiredParams.replaceAll(",", "=sampleParam&") + "=sampleParam";
        }
        return "";
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private String getIndianDate(long updatedInMillis) {
        LocalDateTime ldt = LocalDateTime.parse(DATE_FORMAT.format(new Date(updatedInMillis)), DateTimeFormatter.ofPattern(DATE_FORMAT.toPattern()));
        return DATE_FORMAT.format(Date.from(ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant()));
    }
}
