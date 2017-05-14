<%@ page import="com.theah64.mock_api.database.Projects" %>
<%@ page import="com.theah64.mock_api.exceptions.RequestException" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="com.theah64.mock_api.utils.DarKnight" %>
<%@ page import="com.theah64.mock_api.utils.Form" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.theah64.mock_api.utils.RandomString" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 12/9/16
  Time: 4:08 PM=
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute(Project.KEY) != null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Admin Login - WASP</title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<div class="container">

    <div class="row ">
        <div class="col-md-12 text-center">
            <h1>Mock API</h1>
            <p>
                <small>Fill the form</small>
            </p>
        </div>
    </div>

    <div class="row">

        <div class="col-md-4 content-centered">

            <%--Form--%>
            <form action="login.jsp" method="POST" role="form">

                <div class="form-group">
                    <label for="iProject Name">Project Name : </label>
                    <input name="name" type="text"
                           id="iProject Name" class="form-control"
                           placeholder="Project Name"/>
                </div>


                <div class="form-group">
                    <label for="iPassword">Password : </label>
                    <input name="password" type="password"
                           id="iPassword" class="form-control"
                           placeholder="Password"/>
                </div>

                <div class="row">

                    <div class="col-md-8">
                        <%

                            Form form = new Form(request);

                            if (form.isSubmitted()) {

                                final String name = request.getParameter("name");
                                final String passHash = DarKnight.getEncrypted(request.getParameter("password"));
                                final Projects projectsTable = Projects.getInstance();

                                Project project = projectsTable.get(Projects.COLUMN_NAME, name, Projects.COLUMN_PASS_HASH, passHash);

                                if (project != null) {

                                    //Project exists
                                    session.setAttribute(Project.KEY, project);
                                    response.sendRedirect("index.jsp");

                                } else {

                                    //Project doesn't exist
                                    final String apiKey = RandomString.getNewApiKey(10);
                                    project = new Project(null, name, passHash, apiKey);

                                    try {

                                        final String projectId = projectsTable.addv3(project);

                                        //Project exists
                                        if (projectId != null) {

                                            project.setId(projectId);
                                            session.setAttribute(Project.KEY, project);

                                            response.sendRedirect("index.jsp");
                                        } else {
                                            throw new RequestException("Failed to create project");
                                        }

                                    } catch (SQLException | RequestException e) {
                                        e.printStackTrace();
                        %>
                        <div class="text-danger pull-left">
                            <%=e.getMessage()%>
                        </div>
                        <%
                                    }


                                }
                            }
                        %>
                    </div>


                    <div class="col-md-4">
                        <input value="SUBMIT" name="<%=Form.KEY_IS_SUBMITTED%>" type="submit"
                               class="btn btn-primary pull-right"/>
                    </div>


                </div>


            </form>


        </div>
    </div>
</div>
</body>
</html>
