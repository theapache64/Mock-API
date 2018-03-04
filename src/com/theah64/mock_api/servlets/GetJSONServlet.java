package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.ParamResponses;
import com.theah64.mock_api.database.Params;
import com.theah64.mock_api.database.Responses;
import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.ParamResponse;
import com.theah64.mock_api.models.Route;
import com.theah64.mock_api.utils.DynamicResponseGenerator;
import com.theah64.mock_api.utils.HeaderSecurity;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.CommonUtils;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 14/5/17.
 */
@WebServlet(urlPatterns = {"/get_json/*"})
public class GetJSONServlet extends AdvancedBaseServlet {

    private Route route;

    private static final String CONDITIONED_PATTERN = "\\{(.+)\\s*(==|===|!=|>|<|>=|<=)\\s*(.+)\\s*\\?\\s*(.+)\\:\\s*(.+)\\}";


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
            return route.filterRequiredParams();

        } catch (PathInfo.PathInfoException | SQLException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }
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

                Thread.sleep(route.getDelay() * 1000);
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
            if (route.getParams() != null) {
                final List<Param> reqParams = route.getParams();
                for (final Param params : reqParams) {
                    final String value = getStringParameter(params.getName());
                    if (value != null && !value.trim().isEmpty()) {
                        jsonResp = jsonResp.replace("{" + params.getName() + "}", value);
                    }
                }
            }


            jsonResp = DynamicResponseGenerator.generate(jsonResp);

            //Checking if conditioned response
            final Pattern pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(jsonResp);

            if (matcher.find()) {
                do {

                    //{10>3 ? young : old}
                    final String val1 = matcher.group(1);
                    final String operator = matcher.group(2);
                    final String val2 = matcher.group(3);
                    final String trueVal = matcher.group(4);
                    final String falseVal = matcher.group(5);



                    /*final String param = matcher.group(1);
                    String newRandomRegEx = null;
                    try {
                        final int count = Integer.parseInt(param);
                        newRandomRegEx = randomRegEx.replace("(\\d+)", count + "");

                    } catch (NumberFormatException e) {
                        e.printStackTrace();

                        //Param is a string
                        newRandomRegEx = randomRegEx.replace("(.+)", param);
                        System.out.println("New random regex : " + newRandomRegEx);
                    }


                    //Regex matching
                    final String[] jsonRespArr = jsonResp.split(
                            newRandomRegEx
                    );

                    final StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < jsonRespArr.length; i++) {

                        String data = randomResponse.getValue(param);
                        data = data.replace("\n", "\\n");
                        sb.append(jsonRespArr[i]);

                        if (i < (jsonRespArr.length - 1)) {
                            sb.append(data);
                        }

                    }*//*

                    jsonResp = sb.toString();*/


                } while (matcher.find());
            }

            //Validation
            if (CommonUtils.isJSONValid(jsonResp)) {
                getWriter().write(jsonResp);
            }

        } catch (QueryBuilderException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }
    }


}
