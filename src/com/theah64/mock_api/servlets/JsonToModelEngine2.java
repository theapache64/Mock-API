package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 13/11/17.
 */
@WebServlet(AdvancedBaseServlet.VERSION_CODE + "/json_to_model_engine2")
public class JsonToModelEngine2 extends AdvancedBaseServlet {

    private static final String KEY_MODEL_NAME = "model_name";
    private static final String KEY_JO_STRING = "jo_string";
    private static final String KEY_IS_RETROFIT_MODEL = "is_retrofit_model";

    @Override
    protected boolean isSecureServlet() {
        return false;
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
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {

    }
}
