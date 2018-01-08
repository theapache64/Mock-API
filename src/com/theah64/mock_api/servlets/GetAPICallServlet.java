package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.CodeGen;
import com.theah64.mock_api.utils.Inflector;
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

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_call"})
public class GetAPICallServlet extends AdvancedBaseServlet {

    private static final String KEY_PROJECT_NAME = "project_name";

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
        return new String[]{KEY_PROJECT_NAME, Routes.COLUMN_NAME};
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

            //Basic
            codeBuilder.append(String.format("RetrofitClient.getClient().create(APIInterface.class).%s(", CodeGen.toCamelCase(route.getName())));

            //Building param map
            for (Param param : route.getParams()) {
                final String paramName = param.getName();
                codeBuilder.append("\n\t");
                String fCharUp = CodeGen.getFirstCharUppercase(CodeGen.toCamelCase(paramName));
                if (paramName.equals("api_key")) {
                    codeBuilder.append("App.getCompany().getApiKey(),");
                } else if (paramName.endsWith("_id")) {
                    //Spinner
                    fCharUp = Inflector.getInstance().pluralize(fCharUp.substring(0, fCharUp.length() - 2));
                    codeBuilder.append("gs").append(fCharUp).append(".getCustomSpinner().getItemSelected().getId(),");
                } else if (paramName.startsWith("is_")) {
                    codeBuilder.append("cb").append(fCharUp).append(".isChecked(),");
                } else {
                    //ValidTextInputLayout
                    codeBuilder.append("vtil").append(fCharUp).append(".getString(),");
                }
            }

            if (!route.getParams().isEmpty()) {
                //removing last comma
                final String x = codeBuilder.toString().substring(0, codeBuilder.length() - 1);
                codeBuilder = new StringBuilder(x);
            }

            codeBuilder.append(String.format("\n).enqueue(new CustomRetrofitCallback<BaseAPIResponse<%s>, %s>(this, R.string.Loading){", responseClass, responseClass));
            codeBuilder.append("\n\t").append("@Override");
            codeBuilder.append("\n\t").append(String.format("protected void onSuccess(%s data) {", responseClass));
            codeBuilder.append("\n\t}");
            codeBuilder.append("\n});");
            /*
            ).enqueue(new CustomRetrofitCallback<BaseAPIResponse<AddCustomerResponse>, AddCustomerResponse>(this, R.string.Adding_customer) {
                    @Override
                    protected void onSuccess(AddCustomerResponse data) {
                        vtilCustomerCode.addSuggestion(data.getCustomer());
                        getResponse().getCustomers().add(0, data.getCustomer());
                        Toast.makeText(CustomerMasterActivity.this, R.string.Added_new_customer, Toast.LENGTH_SHORT).show();
                        refreshPage();
                    }
                });
             */

            getWriter().write(codeBuilder.toString());
        } else {
            throw new Request.RequestException("Invalid route");
        }


    }

    private String getPrimitive(String dataType) {
        if (!dataType.equals("String")) {
            return dataType.toLowerCase();
        }
        return dataType;
    }


}
