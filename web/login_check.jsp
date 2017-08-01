<%@ page import="com.theah64.mock_api.database.Projects" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 30/8/16
  Time: 10:32 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%

    final String apiKey = request.getParameter(Projects.COLUMN_API_KEY);
    Project project = null;

    if (apiKey != null) {
        project = Projects.getInstance().get(Projects.COLUMN_API_KEY, apiKey);
    }

    if (project == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
