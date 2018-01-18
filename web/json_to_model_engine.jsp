<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.servlets.JsonToModelEngine" %>
<%@ page import="com.theah64.mock_api.utils.CodeGen" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="org.json.JSONException" %>
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
            "Authorization",
            JsonToModelEngine.KEY_ROUTE_NAME,
            JsonToModelEngine.KEY_JO_STRING,
            JsonToModelEngine.KEY_IS_RETROFIT_MODEL
    });



    try {

        if (!form.isSubmitted()) {
            throw new Request.RequestException("Bad Request");
        }

        form.isAllRequiredParamsAvailable();

    } catch (Request.RequestException e) {
        e.printStackTrace();
        StatusResponse.redirect(response, "Error", e.getMessage());
        return;
    }

    final String routeName = form.getString(JsonToModelEngine.KEY_ROUTE_NAME);
    final String modelName = CodeGen.getFirstCharUppercase(CodeGen.toCamelCase(routeName)) + "Response";
    final String joString = form.getString(JsonToModelEngine.KEY_JO_STRING);
    final boolean isRetrofitModel = form.getBoolean(JsonToModelEngine.KEY_IS_RETROFIT_MODEL);

    final String apiKey = form.getString("Authorization");
    System.out.println(apiKey);
    final String packageName = Projects.getInstance().get(Projects.COLUMN_API_KEY, apiKey, Projects.COLUMN_PACKAGE_NAME, false);
    String output = null;
    try {
        output = CodeGen.getFinalCode(packageName, joString, modelName, isRetrofitModel);
    } catch (JSONException e) {
        e.printStackTrace();
        StatusResponse.redirect(response, "Error", e.getMessage());
    }

%>
<html>
<head>
    <title><%=modelName%>
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
<textarea id="jCode"><%=output%></textarea>
</body>
</html>
