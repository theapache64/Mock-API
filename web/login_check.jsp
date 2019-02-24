<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%


    final String apiKey = request.getParameter(Projects.Companion.getCOLUMN_API_KEY());
    Project project = null;

    if (apiKey != null) {
        project = Projects.Companion.getINSTANCE().get(Projects.getCOLUMN_API_KEY(), apiKey);
    }

    if (project == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
