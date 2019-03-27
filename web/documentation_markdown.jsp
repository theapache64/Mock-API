<%@ page import="com.theah64.webengine.utils.DarKnight" %>
<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="java.util.List" %>
<%@ page import="com.theah64.mock_api.utils.MarkDownUtils" %>
<%@ page import="com.theah64.webengine.utils.WebEngineConfig" %>
<%

    final String apiKey = DarKnight.getDecrypted(request.getParameter(Projects.COLUMN_API_KEY));

    Project project = null;

    if (apiKey != null) {
        project = Projects.Companion.getInstance().get(Projects.COLUMN_API_KEY, apiKey);
    }

    if (project == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    final List<Route> routeList = Routes.Companion.getInstance().getAllDetailed(project.getId());
%>


# <%=project.getName()%>

## API Documentation

This is a lightweight web service, (REST interface), which provides an easy way to access **`<%=project.getName()%>`** data.
The API works through simple commands, so there should not be a problem coding some nice applications.
This API contains total **<%=routeList.size()%>** route(s)


## API Endpoints

All the API endpoints return the same data structure as below

<%=MarkDownUtils.Companion.toMarkDownTable(project.getBaseResponseStructure())%>

## Success response format

```json
<%=project.getDefaultSuccessResponse()%>
```

## Error response format

```json
<%=project.getDefaultErrorResponse()%>
```

## Routes

<%
    for (Route route : routeList) {
%>
- [/<%=route.getName()%>](<%=route.getExternalApiUrl()%>) - [(mock-url)](<%=WebEngineConfig.getBaseURL() + "/get_json/" + project.getName() + "/" + route.getName() + "?" + route.getDummyRequiredParams()%>)
<%
    }
%>

