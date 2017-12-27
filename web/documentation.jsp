<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Param" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.webengine.utils.DarKnight" %>
<%@ page import="com.theah64.webengine.utils.WebEngineConfig" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 7/12/17
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

    final String apiKey = DarKnight.getDecrypted(request.getParameter(Projects.COLUMN_API_KEY));

    Project project = null;

    if (apiKey != null) {
        project = Projects.getInstance().get(Projects.COLUMN_API_KEY, apiKey);
    }

    if (project == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    final List<Route> routeList = Routes.getInstance().getAllDetailed(project.getId());
%>
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

        .label-sm {
            font-size: 45%;
            position: relative;
            top: -4px;
        }

        .CodeMirror-fullscreen {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            height: auto;
            z-index: 9;
        }

        .CodeMirror {
            border: 1px solid silver;
        }

        .CodeMirror-empty.CodeMirror-focused {
            outline: none;
        }

        .CodeMirror pre.CodeMirror-placeholder {
            color: #999;
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
        API works through simple commands, so there should not be a problem coding some nice applications. This API
        contains total <b><%=routeList.size()%>
        </b> route(s)</b>
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
        int i = 0;
        for (final Route route : routeList) {
            i++;
    %>

    <h3>
        <small><%=i%>
            .
        </small>
        /<%=route.getName()%>
        <span class="label label-sm <%=route.getBootstrapLabelForMethod()%>"><%=route.getMethod()%></span>
    </h3>

    <br>

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

    <br>
    <%
        if (route.getDescription() != null && !route.getDescription().trim().isEmpty()) {
    %>
    <h4><b>Description</b></h4>
    <p><%=route.getDescription()%>
    </p>
    <br>
    <%
        }
    %>

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


    <h4><b>Sample Response</b></h4>

    <%--Sample output--%>


    <div class="row">
        <div class="col-md-10">
            <textarea class="default_response" name="response"
                      placeholder="Response" title="JSON"><%=route.getDefaultResponse()%></textarea>
        </div>
    </div>

    <br>
    <br>

    <%--Examples--%>
    <h4><b>Test URLs</b></h4>
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
<script>
    $(document).ready(function () {

        $('textarea.default_response').each(function (index, elem) {

            var editor = CodeMirror.fromTextArea(elem, {
                lineNumbers: true,
                mode: "application/json",
                matchBrackets: true,
                foldGutter: true,
                extraKeys: {
                    "Ctrl-Q": function (cm) {
                        cm.foldCode(cm.getCursor());
                    },
                    "F11": function (cm) {
                        isAlertResult = !cm.getOption("fullScreen");
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                },
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });
            editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));

        });


    });
</script>
</body>
</html>
