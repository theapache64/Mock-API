package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_response"})
public class GetResponseServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Responses.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException, QueryBuilderException {

        final String responseId = getStringParameter(Responses.COLUMN_ID);
        String resp = null;

        if (responseId.equals("default_response")) {

        } else {
            resp = Responses.getInstance().get(
                    Responses.COLUMN_ID,
                    responseId,
                    Responses.COLUMN_RESPONSE,
                    true
            );
        }
        
        if (resp != null) {

            getWriter().write(new APIResponse("OK", Responses.COLUMN_RESPONSE, resp).getResponse());
        } else {
            getWriter().write(new APIResponse("Invalid response id").getResponse());
        }

    }
}
