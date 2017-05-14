<%@ page import="com.theah64.wasp.server.database.Preferences" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 30/8/16
  Time: 10:32 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%

    final Object adminPassword = session.getAttribute(Preferences.KEY_ADMIN_PASSWORD);
    if (adminPassword == null) {
        response.sendRedirect("login.jsp");
        return;
    } else {
        final boolean isCorrect = Preferences.getInstance().getAdminPassword().equals(adminPassword);
        if (!isCorrect) {
            //Expired
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }
    }
%>
