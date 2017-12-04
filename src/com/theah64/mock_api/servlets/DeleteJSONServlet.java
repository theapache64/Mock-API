package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/delete_json"})
public class DeleteJSONServlet extends AdvancedBaseServlet {
    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Routes.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException {
        final String jsonId = getStringParameter(Routes.COLUMN_ID);
        Routes.getInstance().delete(Routes.COLUMN_ID, jsonId, Routes.COLUMN_PROJECT_ID, getHeaderSecurity().getProjectId());
        getWriter().write(new APIResponse("Route deleted", null).getResponse());
    }
}
