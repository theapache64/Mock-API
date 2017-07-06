<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 30/8/16
  Time: 10:32 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%
    final Object project = session.getAttribute(Project.KEY);
    if (project == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
