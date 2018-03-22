package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.CodeGen;
import com.theah64.mock_api.utils.SlashCutter;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_interface_method"})
public class GetAPIInterfaceMethodServlet extends AdvancedBaseServlet {

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
        final String responseClass = CodeGen.getFirstCharUppercase(CodeGen.toCamelCase(routeName)) + "Response";

        final Route route = Routes.getInstance().get(projectName, routeName);

        if (route != null) {

            getHttpServletResponse().setContentType("text/plain");

            StringBuilder codeBuilder = new StringBuilder();
            final StringBuilder descriptionBuilder = new StringBuilder();

            if (route.getDescription() != null && !route.getDescription().trim().isEmpty()) {
                descriptionBuilder.append("* ").append(route.getDescription()).append("\n\n");
            }


            final String returnClassName = CodeGen.getFromFirstCapCharacter(SlashCutter.cut(responseClass));
            codeBuilder.append(String.format("%s\n@%s(\"%s\")\nCall<BaseAPIResponse<%s>> %s(", (route.getMethod().equals("POST") && !route.getParams().isEmpty()) ? "@FormUrlEncoded" : "", route.getMethod(), route.getName(),
                    returnClassName, SlashCutter.cut(CodeGen.toCamelCase(route.getName()))));

            if (route.isSecure()) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,");
            }

            boolean hasFileParam = false;

            if (!route.getParams().isEmpty()) {

                final List<Param> params = route.getParams();
                for (final Param param : params) {

                    final String camelCaseParamName = CodeGen.toCamelCase(param.getName());
                    descriptionBuilder.append("* @param ").append(camelCaseParamName).append(" <p>").append(param.getDescription()).append("</p>\n");

                    if (param.getDataType().equals(Param.DATA_TYPE_FILE)) {
                        hasFileParam = true;

                        codeBuilder.append(String.format("\n\t%s %s,", MULTIPART_KEY, camelCaseParamName));
                    } else {
                        codeBuilder.append(String.format("\n\t@%s(\"%s\") %s %s %s,", route.getMethod().equals("POST") && codeBuilder.indexOf(MULTIPART_KEY) == -1 ? "Field" : "Query", param.getName(), param.isRequired() ? "@NonNull" : "@Nullable", getPrimitive(param.getDataType()), CodeGen.toCamelCase(param.getName())));
                    }
                }
            }

            if (!route.getParams().isEmpty() || route.isSecure()) {
                //Removing last comma
                codeBuilder = new StringBuilder(codeBuilder.substring(0, codeBuilder.length() - 1));
            }

            descriptionBuilder.append("* @return ").append(returnClassName);

            if (hasFileParam) {
                final int index = codeBuilder.indexOf("@" + route.getMethod());
                codeBuilder.insert(index, "@Multipart\n");
            }

            codeBuilder.append(");");

            //Adding multiline comment
            descriptionBuilder.insert(0, "/**\n");
            descriptionBuilder.append("\n*/\n");

            //description
            codeBuilder.insert(0, descriptionBuilder.toString());

            getWriter().write(codeBuilder.toString());

        } else {
            throw new Request.RequestException("Invalid route");
        }


    }

    private String getPrimitive(String dataType) {

        if (dataType.equals("Integer")) {
            return "int";
        }

        if (!dataType.equals("String")) {
            return dataType.toLowerCase();
        }
        return dataType;
    }


}
