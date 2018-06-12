package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Projects;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.CodeGenJava;
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

    public static final String KEY_ROUTE_NAME = "route_name";
    public static final String KEY_JO_STRING = "jo_string";
    public static final String KEY_TARGET_LANG = "target_lang";
    public static final String KEY_IS_RETROFIT_MODEL = "is_retrofit_model";

    public static final String LANGUAGE_TYPESCRIPT_INTERFACE = "typescript_interface";
    public static final String LANGUAGE_TYPESCRIPT_CLASS = "typescript_class";
    public static final String LANGUAGE_JAVA = "java";

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_ROUTE_NAME,
                KEY_JO_STRING,
                KEY_IS_RETROFIT_MODEL
        };
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, PathInfo.PathInfoException {

        final String routeName = getStringParameter(KEY_ROUTE_NAME);
        final String modelName = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response";
        final String joString = getStringParameter(KEY_JO_STRING);
        final boolean isRetrofitModel = getBooleanParameter(KEY_IS_RETROFIT_MODEL);

        final String packageName = Projects.getInstance().get(Projects.COLUMN_ID, getHeaderSecurity().getProjectId(), Projects.COLUMN_PACKAGE_NAME, false);
        getWriter().write(new APIResponse("Done", "data", CodeGenJava.getFinalCode(packageName, joString, modelName, isRetrofitModel)).getResponse());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }


}
