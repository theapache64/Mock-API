<%@ page import="com.theah64.mock_api.database.Images" %>
<%@ page import="com.theah64.mock_api.models.Image" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 10/12/17
  Time: 11:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<html>
<head>
    <title>Images / <%=project.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<%@include file="nav_bar.jsp" %>

<div class="container">
    <div class="row">
        <div class="col-md-6">
            <h3>Project Images</h3>

            <%

                try {
                    List<Image> images = Images.getInstance().getAll(Images.COLUMN_PROJECT_ID, project.getId());

                    for (final Image image : images) {
            %>


            <div class="col-md-3">
                <div class="center-cropped dGalleryRow"
                     data-image-url="<%=image.getImageUrl()%>"
                     style="background-image: url('<%=image.getThumbUrl()%>')">
                    <button
                            style="display:none;margin: 10px;background-color: transparent;border: 0;"
                            class="pull-right bDeleteImage"><span style="color: white"
                                                                  class="glyphicon glyphicon-trash"></span>
                    </button>
                </div>
            </div>


            <%
                    }
                } catch (QueryBuilderException | SQLException e) {
                    e.printStackTrace();
                }
            %>

        </div>

        <div class="col-md-6">
            <h3>Search</h3>
            <br>
            <form class="form-inline">

                <div class="form-group">
                    <input id="iSearch" class="form-control" name="search_keyword" placeholder="Keyword"/>
                </div>

                <button type="submit" class="btn btn-primary">&nbsp; <span class="glyphicon glyphicon-search"></span> &nbsp;</button>
            </form>


        </div>

    </div>
</div>

</body>
</html>
