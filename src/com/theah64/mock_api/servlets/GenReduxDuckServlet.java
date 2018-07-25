package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
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
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/gen_redux_duck"})
public class GenReduxDuckServlet extends AdvancedBaseServlet {

    private static final String KEY_PROJECT_NAME = "project_name";
    private static final String MULTIPART_KEY = "@Part MultipartBody.Part";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_PROJECT_NAME,
                Routes.COLUMN_NAME
        };
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException {


        final String projectName = getStringParameter(KEY_PROJECT_NAME);
        final String routeName = getStringParameter(Routes.COLUMN_NAME);
        final String responseClass = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response";

        final Route route = Routes.getInstance().get(projectName, routeName);

        if (route != null) {

            getHttpServletResponse().setContentType("text/plain");

            StringBuilder codeBuilder = new StringBuilder();


            //Adding main constant
            final String ROUTE_NAME = getFormattedRouteNameWithCaps(routeName);
            codeBuilder.append("\n// Keyword");
            codeBuilder.append(String.format("\nconst %s = '%s';\n", ROUTE_NAME, ROUTE_NAME));

            // Response class name
            final String responseClassName = getResponseClassNameFromRoute(routeName);

            //Reducer
            final String reducerName = getReducerNameRouteName(routeName);
            codeBuilder.append(String.format("\nexport const %s = \n (state : NetworkResponse<%s>, action : BaseAction) => \n ResponseManager.manage(%s, state, action);\n\n", reducerName, responseClassName, ROUTE_NAME));


            // Params
            codeBuilder.append("// Params\nexport class Params {\n  constructor(\n");

            for (final Param param : route.getParams()) {
                codeBuilder.append("    public readonly ").append(param.getName())
                        .append(String.format("%s: ", param.isRequired() ? "" : "?")).append(getPrimitive(param.getDataType()))
                        .append(",\n");
            }

            codeBuilder.append("  ) { }\n}");


            codeBuilder.append("\n\n");

            //Action
            final String actionName = getActionNameFromRouteName(routeName);
            codeBuilder.append(String.format("// Action \nexport const %s = (", actionName));

            //Looping through params
            if (route.isSecure()) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,");
            }

            boolean hasFileParam = false;


            if (!route.getParams().isEmpty()) {
                codeBuilder.append("\n\tparams : Params");
            }

            codeBuilder.append(String.format("%s): AxiosRequest => new AxiosRequest(", route.getParams().isEmpty() ? "" : "\n"))
                    .append(String.format("\n\t%s,", ROUTE_NAME))
                    .append(String.format("\n\t'%s',", route.getMethod()))
                    .append(String.format("\n\t'/%s',\n", route.getName()));

            if (!route.getParams().isEmpty()) {
                codeBuilder.append("\tparams\n");
            }



            if (hasFileParam) {
                final int index = codeBuilder.indexOf("@" + route.getMethod());
                codeBuilder.insert(index, "@Multipart\n");
            }

            codeBuilder.append(");\n");

            getWriter().write(codeBuilder.toString());

        } else {
            throw new Request.RequestException("Invalid route");
        }


    }

    private static String getActionNameFromRouteName(String routeName) {
        routeName = routeName.replaceAll("[^\\w]+", "_");
        return CodeGenJava.toCamelCase(routeName);
    }

    private static String getResponseClassNameFromRoute(String routeName) {
        routeName = routeName.replaceAll("[^\\w]+", "_");
        return CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response";
    }

    private static String getReducerNameRouteName(String routeName) {
        routeName = routeName.replaceAll("[^\\w]+", "_");
        return CodeGenJava.toCamelCase(routeName) + "Reducer";
    }

    private String getFormattedRouteNameWithCaps(String routeName) {
        return routeName
                .replaceAll("([a-zA-Z][a-z]*)([A-Z])", "$1_$2")
                .toUpperCase()
                .replaceAll("[\\W+]", "_");
    }

    private String getPrimitive(String dataType) {

        if (dataType.equals("Integer")) {
            return "number";
        }

        if (!dataType.equals("string")) {
            return dataType.toLowerCase();
        }
        return dataType;
    }


}
