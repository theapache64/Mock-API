<%@ page import="com.theah64.mock_api.database.HitLogs" %>
<%@ page import="com.theah64.mock_api.models.HitLog" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 14/10/17
  Time: 4:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<html>
<head>
    <title><%=project.getName()%> / MockAPI
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <h1>Hit Logs</h1>
        </div>
    </div>

    <%--Fetching all hit logs--%>
    <%

        int limit;
        try {
            limit = Integer.parseInt(request.getParameter("limit") != null ? request.getParameter("limit") : "10");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            limit = 10;
        }

        final List<HitLog> hitLogList = HitLogs.getInstance().getRecent(project.getId(), request.getParameter("route"), limit);

        if (hitLogList == null) {
            response.sendRedirect("error.jsp?title=NoLogFound&message=NoLogFound");
            return;
        }
    %>

    <div class="row">
        <table class="table table-hover table-striped table-bordered">
            <thead>
            <tr>
                <td>Date</td>
                <td>Request Body</td>
                <td>Status</td>
                <td>Error Response</td>
            </tr>
            </thead>
            <tbody>
            <%
                for (final HitLog hitLog : hitLogList) {
            %>

            <tr>
                <td><%=hitLog.getCreatedAt()%>
                </td>
                <td><%=hitLog.getRequestBody()%>
                </td>
                <td><span
                        class="label label-<%=hitLog.getErrorResponse()==null ? "success" : "danger"%>"><%=hitLog.getErrorResponse() == null ? "SUCCESS" : "FAILED"%></span>
                </td>
                <td><%=hitLog.getErrorResponse() != null ? hitLog.getErrorResponse() : ""%>
                </td>

            </tr>
            <%

                }
            %>
            </tbody>

        </table>
    </div>
</div>

</body>
</html>
