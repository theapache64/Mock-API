package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.CodeGenJava;
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
            final String ROUTE_NAME = routeName.toUpperCase();
            codeBuilder.append(String.format("\nconst %s = '%s';\n", ROUTE_NAME, ROUTE_NAME));

            //Reducer
            codeBuilder.append(String.format("\nexport default (state : Object, action : Object) => ResponseManager.manage(%s, state, action);\n\n", ROUTE_NAME));


            //Action
            codeBuilder.append(String.format("\nexport const %s = (", SlashCutter.cut(CodeGenJava.toCamelCase(route.getName()))));

            //Looping through params


            final StringBuilder descriptionBuilder = new StringBuilder();

            if (route.getDescription() != null && !route.getDescription().trim().isEmpty()) {
                descriptionBuilder.append("* ").append(route.getDescription()).append("\n\n");
            }


            final String returnClassName = CodeGenJava.getFromFirstCapCharacter(SlashCutter.cut(responseClass));
            /*codeBuilder
                    .append(String.format("%s\n@%s(\"%s\")\nCall<BaseAPIResponse<%s>> %s(", (route.getMethod().equals("POST") && !route.getParams().isEmpty()) ? "@FormUrlEncoded" : "", route.getMethod(), route.getName(),
                    returnClassName, SlashCutter.cut(CodeGenJava.toCamelCase(route.getName()))));
*/
            if (route.isSecure()) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,");
            }

            boolean hasFileParam = false;

            final StringBuilder requestBodyBuilder = new StringBuilder();
            if (!route.getParams().isEmpty()) {

                final List<Param> params = route.getParams();
                for (final Param param : params) {

                    final String camelCaseParamName = CodeGenJava.toCamelCase(param.getName());
                    descriptionBuilder.append("* @param ").append(camelCaseParamName).append(" <p>").append(param.getDescription()).append("</p>\n");

                    if (param.getDataType().equals(Param.DATA_TYPE_FILE)) {
                        hasFileParam = true;

                        codeBuilder.append(String.format("\n\t%s %s,", MULTIPART_KEY, camelCaseParamName));
                    } else {
                        //Param
                        //codeBuilder.append(String.format("\n\t@%s(\"%s\") %s %s %s,", route.getMethod().equals("POST") && codeBuilder.indexOf(MULTIPART_KEY) == -1 ? "Field" : "Query", param.getName(), param.isRequired() ? "@NonNull" : "@Nullable", getPrimitive(param.getDataType()), CodeGenJava.toCamelCase(param.getName())));
                        codeBuilder.append(String.format("\n\t%s: %s, ", CodeGenJava.toCamelCase(param.getName()), getPrimitive(param.getDataType())));
                        requestBodyBuilder.append(String.format("\n\t   %s: %s,", param.getName(), camelCaseParamName));
                    }
                }
            }

            codeBuilder.append(String.format("%s): AxiosRequestType => AxiosRequest.build(", route.getParams().isEmpty() ? "" :"\n"))
                    .append(String.format("\n\t%s,", ROUTE_NAME))
                    .append(String.format("\n\t'%s',", route.getMethod()))
                    .append(String.format("\n\t'/%s',\n", route.getName()));

            if(!requestBodyBuilder.toString().isEmpty()){
                codeBuilder.append(String.format("\n\t{%s\n\t}\n", requestBodyBuilder.toString()));
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
            // codeBuilder.insert(0, descriptionBuilder.toString());

            //Adding flow
            codeBuilder.insert(0, "// @flow\n\nimport ResponseManager from '../../../utils/ResponseManager';\n" +
                    "import type {AxiosRequestType} from '../../../types/AxiosRequestType';\n" +
                    "import AxiosRequest from '../../../types/AxiosRequestType';\n\n");

            getWriter().write(codeBuilder.toString());

        } else {
            throw new Request.RequestException("Invalid route");
        }


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
