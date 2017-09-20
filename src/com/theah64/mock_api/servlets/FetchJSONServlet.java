package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.models.JSON;
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
        final String route = pathInfo.getPartFrom(2);
        final JSON json = JSONS.getInstance().get(projectName, route);

        final JSONObject joJson = new JSONObject();
        joJson.put(JSONS.COLUMN_RESPONSE, json.getResponse());
        joJson.put(JSONS.COLUMN_REQUIRED_PARAMS, json.getRequiredParams());
        joJson.put(JSONS.COLUMN_OPTIONAL_PARAMS, json.getOptionalParams());
        joJson.put(JSONS.COLUMN_EXTERNAL_API_URL, json.getExternalApiUrl());
        joJson.put(JSONS.COLUMN_IS_SECURE, json.isSecure());
        joJson.put(JSONS.COLUMN_DELAY, json.getDelay());
        joJson.put(JSONS.COLUMN_DESCRIPTION, json.getDescription());
        joJson.put("last_modified", TimeUtils.millisToLongDHMS(json.getUpdatedInMillis()) + " ago");
        joJson.put("last_modified_date", getIndianDate(json.getUpdatedInMillis()));

        getWriter().write(new APIResponse("Response loaded", joJson).getResponse());
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private String getIndianDate(long updatedInMillis) {
        LocalDateTime ldt = LocalDateTime.parse(DATE_FORMAT.format(new Date(updatedInMillis)), DateTimeFormatter.ofPattern(DATE_FORMAT.toPattern()));
        return DATE_FORMAT.format(Date.from(ldt.atZone(ZoneId.of("Asia/Kolkata")).toInstant()));
    }
}
