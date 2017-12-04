<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="java.sql.SQLException" %>
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
<html>
<head>
    <title>Compare - Mock API - <%=project.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>
    <script>
        $(document).ready(function () {
            $("button.bCompare").on('click', function () {
                var url = $(this).data('external-api-url');
                $.ajax({
                    url: url,
                    type: 'post',
                }).done(function (data, statusText, xhr) {
                    var status = xhr.status;                //200
                    if (status == 200) {
                        alert("ok");
                    } else {
                        alert("not ok");
                    }
                }).error(function (data, statusText, xhr) {
                    alert("not ok");
                });
            })
        });
    </script>

</head>
<body>
<div class="container">

    <%--Heading--%>
    <div class="row ">
        <div class="col-md-12 text-center">
            <h1>Mock API</h1>
            <p>
                <small>Project <%=project.getName()%>
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
                    <th>Route (Internal)</th>
                    <th>Route (External)</th>
                    <th>Status</th>
                    <th>
                        <button class="btn btn-info">CHECK ALL</button>
                    </th>
                </tr>
                </thead>

                <tbody>
                <%
                    final List<Route> routes;
                    try {
                        routes = Routes.getInstance().getAll(project.getId());
                        for (final Route route : routes) {
                %>
                <tr>
                    <td width="30%"><a target="blank"
                                       href="get_json/<%=project.getName()%>/<%=route.getName()%>"><%=route.getName()%>
                    </a></td>
                    <td width="30%"><a target="blank"
                                       href="<%=route.getExternalApiUrl()!=null ? route.getExternalApiUrl() : ""%>"><%=route.getExternalApiUrl() != null ? route.getExternalApiUrl() : "-"%>
                    </a></td>
                    <td width="60%"><span class="btn btn-success">UNDER CONSTRUCTION</span></td>
                    <td width="10%">
                        <%=route.getExternalApiUrl() != null ? "<button class=\"bCompare btn btn-info\" data-external-api-url=\"" + route.getExternalApiUrl() + "\">COMPARE</button>" : "-"%>
                    </td>
                </tr>
                <%
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>


</div>

</body>
</html>
