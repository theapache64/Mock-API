package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.CodeGen;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 13/11/17.
 */
@WebServlet(AdvancedBaseServlet.VERSION_CODE + "/json_to_model_engine")
public class JsonToModelEngine extends AdvancedBaseServlet {

    private static final String KEY_MODEL_NAME = "model_name";
    private static final String KEY_JO_STRING = "jo_string";
    private static final String KEY_IS_RETROFIT_MODEL = "is_retrofit_model";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_MODEL_NAME,
                KEY_JO_STRING,
                KEY_IS_RETROFIT_MODEL
        };
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException {

        final String modelName = getStringParameter(KEY_MODEL_NAME);
        final String joString = getStringParameter(KEY_JO_STRING);
        final boolean isRetrofitModel = getBooleanParameter(KEY_IS_RETROFIT_MODEL);

        final String packageName = Projects.getInstance().get(Projects.COLUMN_ID, getHeaderSecurity().getProjectId(), Projects.COLUMN_PACKAGE_NAME, false);
        getWriter().write(new APIResponse("Done", "data", CodeGen.getFinalCode(packageName, joString, modelName, isRetrofitModel)).getResponse());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }


}
