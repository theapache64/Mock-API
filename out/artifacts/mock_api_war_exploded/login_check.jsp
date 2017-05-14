<%@ page import="com.theah64.mock_api.database.Projects" %>
><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 30/8/16
  Time: 10:32 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%
    final Object projectId = session.getAttribute(Projects.COLUMN_ID);
    if (projectId == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
