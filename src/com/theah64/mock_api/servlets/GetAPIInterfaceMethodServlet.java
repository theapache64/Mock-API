package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.CodeGen;
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
        return new String[]{KEY_PROJECT_NAME, KEY_RESPONSE_CLASS, Routes.COLUMN_NAME};
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException {


        final String projectName = getStringParameter(KEY_PROJECT_NAME);
        final String routeName = getStringParameter(Routes.COLUMN_NAME);
        final String responseClass = getStringParameter(KEY_RESPONSE_CLASS);

        final Route route = Routes.getInstance().get(projectName, routeName);
        if (route != null) {
            getHttpServletResponse().setContentType("text/plain");

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
            codeBuilder.append(String.format("@POST(\"%s\")\nCall<BaseAPIResponse<%s>> %s(", route.getName(), responseClass, CodeGen.toCamelCase(route.getName())));

            if (route.isSecure()) {
                codeBuilder.append("\n@Header(KEY_AUTHORIZATION) String apiKey,");
            }

            if (!route.getParams().isEmpty()) {

                final List<Param> params = route.getParams();
                for (final Param param : params) {
                    codeBuilder.append(String.format("\n@Query(\"%s\") String %s,", param, CodeGen.toCamelCase(param.getName())));
                }

                codeBuilder = new StringBuilder(codeBuilder.substring(0, codeBuilder.length() - 1));
            }


            codeBuilder.append(");");

            getWriter().write(codeBuilder.toString());

        } else {
            throw new Request.RequestException("Invalid route");
        }


    }
}
