<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.RequestException" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.mock_api.models.*" %>
<%@ page import="com.theah64.mock_api.database.*" %><%--
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
                route = Routes.getInstance().get(project.getName(), routeName);
            }
        } catch (RequestException | SQLException e) {
            e.printStackTrace();
            StatusResponse.redirect(response, "Error", e.getMessage());
            return;
        }

        if (route == null) {
            StatusResponse.redirect(response, "Error", "Invalid route");
            return;
        }

//        Data needed

        List<Param> params = Params.getInstance().getAll(Params.COLUMN_ROUTE_ID, route.getId());
        List<Response> responses = null;
        List<ParamResponse> paramResponses = null;
        try {
            responses = Responses.getInstance().getAll(Responses.COLUMN_ROUTE_ID, route.getId());
            paramResponses = ParamResponses.getInstance().getAll(ParamResponses.COLUMN_ROUTE_ID, route.getId());
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
                $("form#fParamResp").prepend(formRow);
            });

            $("a.delParamResp").on('click', function () {
                $(this).parent().parent().remove();
            });

            <%
                 for (ParamResponse paramResponse : paramResponses) {
                %>
            $("select#sOpt<%=paramResponse.getId()%>").val('<%=paramResponse.getRelOpt()%>');
            <%
            }
            %>


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
                    <select class="form-control">
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
                    <select class="form-control">
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
                    <input class="form-control" type="text" placeholder="Value">
                </div>

                <%--Responses--%>
                <div class="col-md-3">
                    <select class="form-control">
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

            <form id="fParamResp">


                <%
                    for (ParamResponse paramResponse : paramResponses) {
                %>
                <div class="form-group">

                    <div class="row">

                        <%--Params--%>
                        <div class="col-md-3">
                            <select id="sParam<%=paramResponse.getId()%>" class="form-control">
                                <option value="">Select param</option>
                                <%
                                    for (final Param param : params) {
                                %>
                                <option value="<%=param.getId()%>" <%=paramResponse.getParamId().equals(param.getId()) ? "selected" : ""%> ><%=param.getName()%>
                                </option>
                                <%
                                    }
                                %>
                            </select>
                        </div>

                        <%--Operators--%>
                        <div class="col-md-2">
                            <select id="sOpt<%=paramResponse.getId()%>" class="form-control">
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
                            <input class="form-control" type="text" placeholder="Value">
                        </div>

                        <%--Responses--%>
                        <div class="col-md-3">
                            <select id="sResp<%=paramResponse.getId()%>" class="form-control">
                                <option value="">Select response</option>
                                <%
                                    for (final Response respons : responses) {
                                %>
                                <option value="<%=respons.getId()%>" <%=paramResponse.getResponseId().equals(respons.getId()) ? "selected" : ""%> ><%=respons.getName()%>
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

                <%
                    }
                %>

                <button type="submit" class="btn btn-primary">SAVE</button>
            </form>

        </div>
    </div>

</div>

</body>
</html>
