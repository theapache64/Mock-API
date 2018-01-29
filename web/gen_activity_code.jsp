<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.servlets.JsonToModelEngine" %>
<%@ page import="com.theah64.mock_api.utils.CodeGen" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="com.theah64.mock_api.utils.ActivityCodeGen" %>
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
            ActivityCodeGen.KEY_API_KEY,
            ActivityCodeGen.KEY_ROUTE_ID
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

    final String apiKey = form.getString(ActivityCodeGen.KEY_API_KEY);
    final String routeId = form.getString(ActivityCodeGen.KEY_ROUTE_ID);

    final ActivityCodeGen.ActivityCode activityCode = ActivityCodeGen.generate(apiKey, routeId);
%>
<html>
<head>
    <title><%=activityCode.getRouteName() +" / "+ activityCode.getProjectName()%>
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
<%--

    private static final String KEY_CODE = "code";
    private static final String KEY_FROM_DATE = "from_date";
    private static final String KEY_TO_DATE = "to_date";
    private static final String KEY_TYPE = "type";

    @Override
    protected Call<BaseAPIResponse<GeneralLedgerReportResponse>> getCall(APIInterface apiInterface) {
        return apiInterface.getGeneralLedgerReport(
                App.getCompany().getApiKey(),
                getString(KEY_CODE),
                getString(KEY_FROM_DATE),
                getString(KEY_TO_DATE),
                getString(KEY_TYPE)
        );
    }


    public static void start(Context context, String pageTitle, String code, String fromDate, String toDate, String type, String[] ghostKeys) {
        final Intent i = new Intent(context, GeneralLedgerReportResultActivity.class);
        i.putExtra(KEY_GHOST_KEYS, ghostKeys);
        i.putExtra(KEY_CODE, code);
        i.putExtra(KEY_FROM_DATE, fromDate);
        i.putExtra(KEY_TO_DATE, toDate);
        i.putExtra(KEY_TYPE, type);
        context.startActivity(i);
    }

--%>
<textarea id="jCode"><%=output%></textarea>
</body>
</html>
