<%--
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

    <div class="row">
        <table class="table table-hover table-striped table-bordered">
            <thead>
                <tr>
                    <td>Date</td>
                    <td>Request Body</td>
                </tr>
            </thead>



        </table>
    </div>
</div>

</body>
</html>
