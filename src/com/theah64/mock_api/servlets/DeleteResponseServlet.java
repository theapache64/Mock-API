package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 1/12/17.
 */
@WebServlet(urlPatterns = AdvancedBaseServlet.VERSION_CODE + "/delete_response")
public class DeleteResponseServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Responses.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException {
        final String respId = getStringParameter(Responses.COLUMN_ID);
        Responses.getInstance().delete(Responses.COLUMN_ID, respId);
        getWriter().write(new APIResponse("Response deleted", null).getResponse());
    }
}
