<%@ page import="com.theah64.mock_api.utils.ActivityCodeGen" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="java.sql.SQLException" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 17/1/18
  Time: 12:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Form form = new Form(request, new String[]{
            ActivityCodeGen.INSTANCE.getKEY_PROJECT_NAME(),
            ActivityCodeGen.INSTANCE.getKEY_ROUTE_NAME()
    });


    try {
        form.isAllRequiredParamsAvailable();
    } catch (Request.RequestException e) {
        e.printStackTrace();
        StatusResponse.redirect(response, "Error", e.getMessage());
        return;
    }

    final String projectName = form.getString(ActivityCodeGen.INSTANCE.getKEY_PROJECT_NAME());
    final String routeName = form.getString(ActivityCodeGen.INSTANCE.getKEY_ROUTE_NAME());

    final ActivityCodeGen.ActivityCode activityCode;
    try {
        activityCode = ActivityCodeGen.INSTANCE.generate(projectName, routeName);
    } catch (SQLException | Request.RequestException e) {
        e.printStackTrace();
        StatusResponse.redirect(response, "Error", e.getMessage());
        return;
    }
%>
<html>
<head>
    <title><%=routeName + " / " + projectName%>
    </title>
    <%@include file="common_headers.jsp" %>

    <script src="//codemirror.net/addon/edit/matchbrackets.js"></script>
    <link rel="stylesheet" href="//codemirror.net/addon/hint/show-hint.css">
    <script src="//codemirror.net/addon/hint/show-hint.js"></script>
    <script src="//codemirror.net/mode/clike/clike.js"></script>
    <script src="//codemirror.net/addon/display/placeholder.js"></script>
    <style>
        .CodeMirror {
            border: 1px solid #eee;
            height: auto;
        }

        body {
            width: 100%;
            height: 100%;
            overflow: hidden;
            margin: 0
        }

        html {
            width: 100%;
            height: 100%;
            overflow: hidden
        }
    </style>
    <script>
        $(document).ready(function () {

            var javaEditor = CodeMirror.fromTextArea(document.getElementById("jCode"), {
                lineNumbers: true,
                mode: "text/x-java",
                matchBrackets: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                }
            });

        });
    </script>
</head>
<body>

<textarea id="jCode"><%=activityCode.toString()%></textarea>
</body>
</html>
