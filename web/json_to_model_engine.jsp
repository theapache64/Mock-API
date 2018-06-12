<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.servlets.JsonToModelEngine" %>
<%@ page import="com.theah64.mock_api.utils.CodeGenJava" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="com.theah64.mock_api.utils.TypescriptInterfaceGenerator" %>
<%@ page import="com.theah64.mock_api.utils.TypescriptInterfaceClass" %>
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
    final String modelName = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response";
    final String joString = form.getString(JsonToModelEngine.KEY_JO_STRING);
    final boolean isRetrofitModel = form.getBoolean(JsonToModelEngine.KEY_IS_RETROFIT_MODEL);
    final String targetLang = form.getString(JsonToModelEngine.KEY_TARGET_LANG);

    final String apiKey = form.getString("Authorization");

    final String packageName = Projects.getInstance().get(Projects.COLUMN_API_KEY, apiKey, Projects.COLUMN_PACKAGE_NAME, false);
    String output = null;
    try {
        output = getOutput(targetLang, packageName, joString, modelName, isRetrofitModel);

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
    <script src="//codemirror.net/mode/javascript/javascript.js"></script>
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
                mode: "<%=targetLang.equals(JsonToModelEngine.LANGUAGE_JAVA) ? "text/x-java" : "text/javascript"%>",
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
<%!
    public static String getOutput(String targetLang, String packageName, String joString, String modelName, boolean isRetrofitModel) throws JSONException {

        switch (targetLang) {
            case JsonToModelEngine.LANGUAGE_JAVA:
                return CodeGenJava.getFinalCode(packageName, joString, modelName, isRetrofitModel);

            case JsonToModelEngine.LANGUAGE_TYPESCRIPT_INTERFACE:
                return TypescriptInterfaceGenerator.getFinalCode(joString, modelName);

            case JsonToModelEngine.LANGUAGE_TYPESCRIPT_CLASS:
                return TypescriptInterfaceClass.getFinalCode(joString, modelName);


            default:
                throw new IllegalArgumentException("Undefined language " + targetLang);
        }
    }
%>