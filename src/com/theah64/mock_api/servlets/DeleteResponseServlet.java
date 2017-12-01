package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
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
        return new String[0];
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException, QueryBuilderException {

    }
}
