<%@ page import="com.theah64.mock_api.database.ParamResponses" %>
<%@ page import="com.theah64.mock_api.database.Params" %>
<%@ page import="com.theah64.mock_api.database.Responses" %>
<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Param" %>
<%@ page import="com.theah64.mock_api.models.ParamResponse" %>
<%@ page import="com.theah64.mock_api.models.Response" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 3/12/17
  Time: 9:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<html>
<head>
    <title>Param Resp</title>
    <%!
        private static final String KEY_ROUTE_NAME = "route_name";
    %>
    <jsp:include page="common_headers.jsp"/>
    <%

        Form form = new Form(request, new String[]{KEY_ROUTE_NAME});
        Route route = null;
        try {
            if (form.isAllRequiredParamsAvailable()) {
                final String routeName = form.getString(KEY_ROUTE_NAME);
                route = Routes.Companion.getInstance().get(project.getName(), routeName);
            }
        } catch (Request.RequestException | SQLException e) {
            e.printStackTrace();
            StatusResponse.redirect(response, "Error", e.getMessage());
            return;
        }

        if (route == null) {
            StatusResponse.redirect(response, "Error", "Invalid route");
            return;
        }


        ParamResponses paramResponsesDb = ParamResponses.Instance();
        final Form saveParamRespForm = new Form(request);
        if (saveParamRespForm.isSubmitted()) {

            //First delete all current responses
            paramResponsesDb.delete(ParamResponses.COLUMN_ROUTE_ID, route.getId());

            final String[] fParams = request.getParameterValues("params[]");
            final String[] fOps = request.getParameterValues("ops[]");
            final String[] fIValues = request.getParameterValues("ivalues[]");
            final String[] fResponses = request.getParameterValues("responses[]");

            if (fParams != null && fOps != null && fIValues != null && fResponses != null) {

                //Add these values
                for (int i = 0; i < fParams.length; i++) {

                    final String fParam = fParams[i];
                    final String fOp = fOps[i];
                    final String fIValue = fIValues[i];
                    final String fResponse = fResponses[i];

                    try {
                        paramResponsesDb.add(new ParamResponse(
                                null, route.getId(),
                                fParam, fIValue, fResponse, fOp
                        ));
                    } catch (SQLException | QueryBuilderException e) {
                        e.printStackTrace();
                        StatusResponse.redirect(response, "Error", e.getMessage());
                    }

                }

            }


        }


        // Data needed


        List<Param> params = Params.Companion.getInstance().getAll(Params.COLUMN_ROUTE_ID, route.getId());
        List<Response> responses = null;
        List<ParamResponse> paramResponses = null;

        try {
            responses = Responses.Companion.getInstance().getAll(Responses.COLUMN_ROUTE_ID, route.getId());
            paramResponses = ParamResponses.Companion.getInstance().getAll(ParamResponses.COLUMN_ROUTE_ID, route.getId());
        } catch (QueryBuilderException | SQLException e) {
            e.printStackTrace();
            StatusResponse.redirect(response, "Error", e.getMessage());
            return;
        }

        if (params.isEmpty()) {
            StatusResponse.redirect(response, "Error", "No param found");
            return;
        }

        if (responses.isEmpty()) {
            StatusResponse.redirect(response, "Error", "No custom response found");
            return;
        }


    %>


    <script>
        $(document).ready(function () {
            $("a#aAddNewRule").on('click', function () {
                var formRow = $("div#empty_form_row").html();
                $(formRow).insertBefore("input#iSubmitParamResp");
            });


            <%
                 for (ParamResponse paramResponse : paramResponses) {
               %>


            var formRow = $("div#empty_form_row");

            //Param
            $(formRow).find("select.sParams").attr('id', 'sParams<%=paramResponse.getId()%>');
            $(formRow).find("select.sOps").attr('id', 'sOps<%=paramResponse.getId()%>');
            $(formRow).find("input.iValue").attr('id', 'iValue<%=paramResponse.getId()%>');
            $(formRow).find("select.sResps").attr('id', 'sResps<%=paramResponse.getId()%>');
            $(formRow.html()).insertBefore("input#iSubmitParamResp");

            $("select#sParams<%=paramResponse.getId()%>").val('<%=paramResponse.getParamId()%>');
            $("select#sOps<%=paramResponse.getId()%>").val('<%=paramResponse.getRelOpt()%>');
            $("input#iValue<%=paramResponse.getId()%>").val('<%=paramResponse.getParamValue()%>');
            $("select#sResps<%=paramResponse.getId()%>").val('<%=paramResponse.getResponseId()%>');

            //Set form values here

            <%
            }
            %>

            $("#fParamResp").on('click', 'a.delParamResp', function () {
                $(this).parent().parent().remove();
            });

        });
    </script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-10">
            <h2>Param Resp
                <small>(<%=project.getName() + "/" + form.getString(KEY_ROUTE_NAME)%>)
                </small>
            </h2>
        </div>

    </div>

    <a id="aAddNewRule" href="#">Add new rule</a>
    <br>
    <br>


    <div id="empty_form_row" style="display: none">

        <div class="form-group">

            <div class="row">

                <%--Params--%>
                <div class="col-md-3">
                    <select name="params[]" class="sParams form-control" required>
                        <option value="">Select param</option>
                        <%
                            for (final Param param : params) {
                        %>
                        <option value="<%=param.getId()%>"><%=param.getName()%>
                        </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <%--Operators--%>
                <div class="col-md-2">
                    <select name="ops[]" class="sOps form-control" required>
                        <option value="">Select operator</option>
                        <option value="==">==</option>
                        <option value="!=">!=</option>
                        <option value=">"> ></option>
                        <option value="<"> <</option>
                        <option value=">="> >=</option>
                        <option value="<="> <=</option>
                    </select>
                </div>

                <%--Value--%>
                <div class="col-md-3">
                    <input name="ivalues[]" class="form-control iValue" type="text" placeholder="Value" required>
                </div>

                <%--Responses--%>
                <div class="col-md-3">
                    <select name="responses[]" class="sResps form-control" required>
                        <option value="">Select response</option>
                        <%
                            for (final Response respons : responses) {
                        %>
                        <option value="<%=respons.getId()%>"><%=respons.getName()%>
                        </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-1">
                    <a class="delParamResp btn btn-danger"> <b>&times;</b> </a>
                </div>


            </div>

        </div>
    </div>


    <div class="row">
        <div class="col-md-12">

            <form id="fParamResp" method="POST" action="param_resp.jsp?<%=request.getQueryString()%>">
                <input id="iSubmitParamResp" value="SAVE" name="<%=Form.KEY_IS_SUBMITTED%>" type="submit"
                       class="btn btn-primary"/>
            </form>


        </div>
    </div>

</div>

</body>
</html>
