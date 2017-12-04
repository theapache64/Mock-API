package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.ParamResponses;
import com.theah64.mock_api.database.Params;
import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.ParamResponse;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.HeaderSecurity;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/get_json/*"})
public class GetJSONServlet extends AdvancedBaseServlet {

    private Route route;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {

        try {
            final PathInfo pathInfo = new PathInfo(getHttpServletRequest().getPathInfo(), 2, PathInfo.UNLIMITED);
            final String projectName = pathInfo.getPart(1);
            final String routeName = pathInfo.getPartFrom(2);
            route = Routes.getInstance().get(projectName, routeName);
            System.out.println("Required param is " + route.getRequiredParams());
            if (route.getRequiredParams() != null) {
                final String[] reqParams = route.getRequiredParams().split(",");
                for (final String reqParam : reqParams) {
                    System.out.println(reqParam);
                }
                return reqParams;
            }

        } catch (PathInfo.PathInfoException | SQLException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException {

        if (route.isSecure()) {
            final String authorization = getHttpServletRequest().getHeader(HeaderSecurity.KEY_AUTHORIZATION);
            if (authorization == null) {
                throw new Request.RequestException("Authorization header missing");
            }
        }

        if (route.getDelay() > 0) {
            try {
                System.out.println("Sleep for " + route.getDelay() + "ms");
                Thread.sleep(route.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Getting param response
        try {
            final List<ParamResponse> paramResponses = ParamResponses.getInstance().getAll(ParamResponses.COLUMN_ROUTE_ID, route.getId());
            String jsonResp = null;
            if (!paramResponses.isEmpty()) {
                //Has custom resp
                for (final ParamResponse paramResponse : paramResponses) {

                    final String pVal = getStringParameter(Params.getInstance().get(Params.COLUMN_ID, paramResponse.getParamId(), Params.COLUMN_NAME, false));

                    if (pVal != null) {

                        final String op = paramResponse.getRelOpt();
                        final String resp = Responses.getInstance().get(Responses.COLUMN_ID, paramResponse.getResponseId(), Responses.COLUMN_RESPONSE, false);

                        if (
                                (op.equals(ParamResponse.EQUALS) && pVal.equals(paramResponse.getParamValue())) ||
                                        (op.equals(ParamResponse.NOT_EQUALS) && !pVal.equals(paramResponse.getParamValue()))
                                ) {
                            jsonResp = resp;
                        } else {
                            try {
                                //Convert to number
                                final double pNum = Double.parseDouble(paramResponse.getParamValue());
                                final double inputNum = Double.parseDouble(pVal);

                                if (
                                        (op.equals(ParamResponse.GREATER_THAN) && inputNum > pNum) ||
                                                (op.equals(ParamResponse.GREATER_THAN_OR_EQUALS) && inputNum >= pNum) ||
                                                (op.equals(ParamResponse.LESS_THAN) && inputNum < pNum) ||
                                                (op.equals(ParamResponse.LESS_THAN_OR_EQUALS) && inputNum <= pNum)

                                        ) {
                                    jsonResp = resp;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        if (jsonResp != null) {
                            break;
                        }
                    }
                }
            }

            if (jsonResp == null) {
                jsonResp = route.getDefaultResponse();
            }


            //Input throw back
            if (route.getRequiredParams() != null) {
                final String[] reqParams = route.getRequiredParams().split(",");
                for (final String reqParam : reqParams) {
                    final String value = getStringParameter(reqParam);
                    jsonResp = jsonResp.replace("{" + reqParam + "}", value);
                }
            }

            if (route.getOptionalParams() != null) {
                final String[] optParams = route.getOptionalParams().split(",");
                for (final String optParam : optParams) {
                    final String value = getStringParameter(optParam);
                    if (value != null && !value.trim().isEmpty()) {
                        jsonResp = jsonResp.replace("{" + optParam + "}", value);
                    }
                }
            }

            //First name throw back
            

            getWriter().write(new JSONObject(jsonResp).toString());

        } catch (QueryBuilderException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }
    }
}
