<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Param" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.webengine.utils.WebEngineConfig" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 7/12/17
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<html>
<head>
    <title>API Documentation - <%=project.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>
    <style>
        body {
            background-color: #FFF;
        }

        p {
            font-size: 16px;
        }
    </style>
</head>
<body>
<div class="container">

    <h1><%=project.getName()%>
    </h1>

    <hr>

    <h2>API Documentation</h2>
    <p>This is a lightweight web service, (REST interface), which provides an easy way to access
        <b>`<%=project.getName()%>`</b> data. The
        API works through simple commands, so there should not be a problem coding some nice applications.
    </p>

    <br>

    <h2>API Endpoints</h2>
    <p>All the API endpoints return the same data structure as below
    </p>

    <%--Structure--%>
    <div class="row">
        <div class="col-md-8">
            <table class="table table-bordered ">
                <thead>
                <tr>
                    <th>Returned Key</th>
                    <th>Description</th>
                    <th>Example</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>error</td>
                    <td>The returned status for the API call, can be either 'true' or 'false'</td>
                    <td>true</td>
                </tr>
                <tr>
                    <td>message</td>
                    <td>Either the error message or the successful message</td>
                    <td>OK</td>
                </tr>
                <tr>
                    <td>data</td>
                    <td>If 'error' is returned as 'false' the API query results will be inside 'data'</td>
                    <td>data</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>


    <%
        final List<Route> routeList = Routes.getInstance().getAllDetailed(project.getId());
        for (final Route route : routeList) {
    %>

    <h2>/<%=route.getName()%>
    </h2>
    <br>
    <p>HTTP POST</p>

    <%--End point--%>
    <div class="row">
        <div class="col-md-9">
            <table class="table table-bordered ">
                <thead>
                <tr>
                    <th>End Point</th>
                    <th>Type</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>
                        <a href="<%=route.getExternalApiUrl()+"/"+route.getName()%>"><%=route.getExternalApiUrl() + "/" + route.getName()%>
                        </a></td>
                    <td>Actual URL</td>
                </tr>
            </table>
        </div>
    </div>

    <%
        if (!route.getParams().isEmpty()) {

    %>
    <%--Parameters--%>
    <div class="row">
        <div class="col-md-10">
            <table class="table table-bordered ">
                <thead>
                <tr>
                    <th>Parameter</th>
                    <th>Required</th>
                    <th>Type</th>
                    <th>Default value</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (final Param param : route.getParams()) {

                %>
                <tr>
                    <td><code><%=param.getName()%>
                    </code></td>
                    <td><%=param.isRequired()%>
                    </td>
                    <td><%=param.getDataType()%>
                    </td>
                    <td><%=param.getDefaultValue()%>
                    </td>
                    <td><%=param.getDescription()%>
                    </td>
                </tr>
                <%

                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
    <%

        }
    %>

    <%--Examples--%>
    <p>Test URLs</p>
    <div class="row">
        <div class="col-md-10">
            <table class="table table-bordered ">
                <thead>
                <tr>
                    <th>URL</th>
                    <th>Type</th>
                </tr>
                </thead>
                <tbody>


                <tr>
                    <td>
                        <a target="_blank" href="<%=route.getExternalApiUrl()%>"><%=route.getExternalApiUrl()%>
                        </a></td>
                    <td>Actual URL</td>
                </tr>

                <tr>
                    <td>
                        <a target="_blank"
                           href="<%=route.getExternalApiUrl()+"?"+route.getDummyRequiredParams()%>"><%=route.getExternalApiUrl()%>
                        </a></td>
                    <td>Actual URL (with required params.)</td>
                </tr>


                <tr>
                    <td>
                        <a target="_blank"
                           href="<%=WebEngineConfig.getBaseURL()+"/get_json/"+project.getName()+"/"+route.getName()+"?"+route.getDummyRequiredParams()%>">
                            <%=WebEngineConfig.getBaseURL() + "/get_json/" + project.getName() + "/" + route.getName()%>
                        </a></td>
                    <td>Mock URL (with required params.)</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>


    <%

        }
    %>

</div>
</body>
</html>
