<%@ page import="com.theah64.webengine.utils.DarKnight" %>
<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="java.util.List" %>
<%@ page import="com.theah64.mock_api.utils.MarkDownUtils" %>
<%@ page import="com.theah64.webengine.utils.WebEngineConfig" %>
<%@ page import="com.theah64.mock_api.models.Param" %>
<%@ page import="com.theah64.mock_api.utils.DynamicResponseGenerator" %>
<%@ page import="com.theah64.mock_api.utils.GoogleSheetUtils" %>
<%@ page import="com.theah64.mock_api.lab.Main" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.theah64.mock_api.utils.RouteUtils" %>
<%@ page import="java.sql.SQLException" %>
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

    final List<Route> routeList = RouteUtils.INSTANCE.order(Routes.Companion.getInstance().getAllDetailed(project.getId()));
%>


# <%=project.getName()%>

## API Documentation

This is a lightweight web service, (REST interface), which provides an easy way to access **`<%=project.getName()%>`** data.
The API works through simple commands, so there should not be a problem coding some nice applications.
This API contains total **<%=routeList.size()%>** route(s)


## API Endpoints

All the API endpoints return the same data structure as below

<%=MarkDownUtils.Companion.toMarkDownTable(project.getBaseResponseStructure())%>

**Success Response Format**

```json
<%=project.getDefaultSuccessResponse()%>
```

**Error Response Format**

```json
<%=project.getDefaultErrorResponse()%>
```

## Routes

<%
    int i = 1;
    ObjectMapper mapper = new ObjectMapper();
    for (Route route : routeList) {
%>

#### <%=i++%> . /<%=route.getName()%>

- Method : **<%=route.getMethod()%>**
- URL : [/<%=route.getName()%>](<%=route.getExternalApiUrl()%>)
- MockURL : [<%=route.getName()%>](<%=WebEngineConfig.getBaseURL() + "/get_json/" + project.getName() + "/" + route.getName() + "?" + route.getDummyRequiredParams()%>)
<%
    if (route.getDescription() != null && !route.getDescription().trim().isEmpty()) {
%>
**Descriptions**

<%=route.getDescription()%>
<%
    }
%>
<%
    if (route.getRequestBodyType().equals(Project.REQUEST_BODY_TYPE_FORM) && route.getParams() != null && route.getParams().size() > 0) {
%>
| Parameter | Required | Type | Default Value | Description |
|-----------|----------|------|---------------|-------------|
<%
    for (Param param : route.getParams()) {
%><%=String.format("|%s|%s|%s|%s|%s|", param.getName(), param.isRequired(), param.getDataType(), param.getDefaultValue() == null ? "" : param.getDefaultValue(), param.getDescription())%>
<%}%><%}%>
<%
    if (route.getRequestBodyType().equals(Project.REQUEST_BODY_TYPE_JSON) && route.getJsonReqBody() != null && !route.getJsonReqBody().trim().isEmpty()) {
%>
**Request Body**
```json
<%=route.getJsonReqBody()%>
```
<%
    }

    String jsonResp = DynamicResponseGenerator.generate(route.getDefaultResponse());
    jsonResp = Main.ConditionedResponse.INSTANCE.generate(jsonResp);
    jsonResp = GoogleSheetUtils.Companion.generate(jsonResp);
    Object jsonObject = mapper.readValue(jsonResp, Object.class);
    jsonResp = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

%>
**Response Body**
```json
<%=jsonResp%>
```
<%


    }%>



