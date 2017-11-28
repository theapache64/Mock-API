package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.JSONS;
import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.models.JSON;
import com.theah64.mock_api.test.CodeGen;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_interface_method"})
public class GetAPIInterfaceMethodServlet extends AdvancedBaseServlet {

    private static final String KEY_PROJECT_NAME = "project_name";
    private static final String KEY_RESPONSE_CLASS = "response_class";

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
        return new String[]{KEY_PROJECT_NAME, KEY_RESPONSE_CLASS, JSONS.COLUMN_ROUTE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {


        final String projectName = getStringParameter(KEY_PROJECT_NAME);
        final String route = getStringParameter(JSONS.COLUMN_ROUTE);
        final String responseClass = getStringParameter(KEY_RESPONSE_CLASS);

        final JSON json = JSONS.getInstance().get(projectName, route);
        if (json != null) {
            getHttpServletResponse().setContentType("text/plaint");

            /**
             * @POST("add_address") Call<BaseAPIResponse<AddAddressResponse>> editAddress(
             @Header(KEY_AUTHORIZATION) String apiKey,
             @Query("address_id") String addressId,
             @Query("name") String name,
             @Query("mobile") String mobile,
             @Query("landmark") String landmark,
             @Query("address") String address,
             @Query("city") String city,
             @Query("postal_code") String postalCode
             );
             */
            StringBuilder codeBuilder = new StringBuilder();

            //@POST("add_address") Call<BaseAPIResponse<AddAddressResponse>> editAddress(
            codeBuilder.append(String.format("@POST(\"%s\") Call<BaseAPIResponse<%s>> %s(", json.getRoute(), responseClass, CodeGen.toCamelCase(json.getRoute())));

            if (json.isSecure()) {
                codeBuilder.append("\n\t\t\t\t\t\t\t\t@Header(KEY_AUTHORIZATION) String apiKey,");
            }

            boolean hasParams = false;
            if (json.getRequiredParams() != null) {
                hasParams = true;
                final String[] reqParams = json.getRequiredParams().split(",");
                for (final String reqParam : reqParams) {
                    codeBuilder.append(String.format("\n\t\t\t\t\t\t\t\t@Query(\"%s\") String %s,", reqParam, CodeGen.toCamelCase(reqParam)));
                }
            }


            if (json.getOptionalParams() != null) {
                hasParams = true;
                final String[] optParams = json.getOptionalParams().split(",");
                for (final String optParam : optParams) {
                    codeBuilder.append(String.format("\n\t\t\t\t\t\t\t\t@Query(\"%s\") String %s,", optParam, CodeGen.toCamelCase(optParam)));
                }
            }

            if (hasParams) {
                //Removing last comma
                codeBuilder = new StringBuilder(codeBuilder.substring(0, codeBuilder.length() - 1));
            }

            codeBuilder.append(");");

            getWriter().write(codeBuilder.toString());

        } else {
            throw new RequestException("Invalid route");
        }


    }
}
