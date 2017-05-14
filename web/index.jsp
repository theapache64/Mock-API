<%@ page import="com.theah64.mock_api.models.Project" %>
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
    final Project project = (Project) session.getAttribute(Project.KEY);
%>
<html>
<head>
    <title>Mock API - <%=project.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<div class="container">

    <div class="row ">
        <div class="col-md-12 text-center">
            <h1>Mock API</h1>
            <p>
                <small>Project <%=project.getName()%>
                </small>
            </p>
            <a href="logout.jsp">logout</a>
        </div>
    </div>

    <div class="row">

    </div>
</div>
</body>
</html>
