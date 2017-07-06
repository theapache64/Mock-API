<%@ page import="com.theah64.mock_api.database.JSONS" %>
<%@ page import="com.theah64.mock_api.models.JSON" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 12/9/16
  Time: 4:08 PM=
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<%
    final Project theProject = (Project) session.getAttribute(Project.KEY);
%>
<html>
<head>
    <title>Compare - Mock API - <%=theProject.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>

</head>
<body>
<div class="container">

    <%--Heading--%>
    <div class="row ">
        <div class="col-md-12 text-center">
            <h1>Mock API</h1>
            <p>
                <small>Project <%=theProject.getName()%>
                </small>
                <a href="logout.jsp"><i>(logout)</i></a>
            </p>

        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <table class="table table-bordered table-responsive table-striped">
                <thead>
                <tr>
                    <th>Route (M-API)</th>
                    <th>Route (External)</th>
                    <th>Status</th>
                    <th>
                        <button class="btn btn-info">CHECK ALL</button>
                    </th>
                </tr>
                </thead>

                <tbody>
                <%
                    final List<JSON> jsons = JSONS.getInstance().getAll(theProject.getId());
                    for (final JSON json : jsons) {
                %>
                <tr>
                    <td width="30%"><a target="blank"
                                       href="get_json/<%=theProject.getName()%>/<%=json.getRoute()%>"><%=json.getRoute()%>
                    </a></td>
                    <td width="30%"><a target="blank"
                                       href="<%=json.getExternalApiUrl()!=null ? json.getExternalApiUrl() : ""%>"><%=json.getExternalApiUrl() != null ? json.getExternalApiUrl() : "-"%>
                    </a></td>
                    <td width="60%">-</td>
                    <td width="10%">
                        <%=json.getExternalApiUrl() != null ? "<button class=\"bCompare btn btn-info\" data-json-id=\"" + json.getId() + "\">COMPARE</button>" : "-"%>
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>


</div>

</body>
</html>
