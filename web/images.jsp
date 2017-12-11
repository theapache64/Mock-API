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
    <script>
        $(document).ready(function () {

            $("div.dGalleryRow").hover(function (e) {
                $(this).find("button.bTransfer").fadeIn("300");
            }, function () {
                $(this).find("button.bTransfer").fadeOut("300");
            });

            $("form#fImageSearch").on('submit', function (e) {
                e.preventDefault();
                var keyword = $.trim($("input#iKeyword").val());

                if (keyword !== "") {

                    $.ajax({
                        type: "POST",
                        beforeSend: function () {

                        },
                        dataType: "jsonp",
                        jsonp: false,
                        data: {
                            keyword: keyword,
                            Authorization: "GoZNYVeK9O"
                        },
                        url: "http://theapache64.com/gpix/v1/search",
                        success: function (data) {
                            console.log(data);
                        },
                        error: function () {

                        }
                    });

                }
            });

        });
    </script>
    <style>
        .center-cropped {
            width: 100px;
            height: 100px;
            background-position: center center;
            background-repeat: no-repeat;
        }

        div.dGalleryRow {
            cursor: pointer;
            margin-bottom: 2px;
        }
    </style>
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
                            class="pull-right bDelete"><span style="color: white"
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
            <form id="fImageSearch" class="form-inline">

                <div class="form-group">
                    <input id="iKeyword" class="form-control" name="search_keyword" placeholder="Keyword"/>
                </div>

                <button type="submit" class="btn btn-primary">&nbsp; <span class="glyphicon glyphicon-search"></span>
                    &nbsp;</button>
            </form>

            <br>

            <div class="row">

                <%
                    for (int i = 0; i < 10; i++) {
                %>

                <%
                    }
                %>

            </div>

        </div>

    </div>

    <div id="dSearchImageRow" style="display: none">
        <div class="col-md-2" style="margin-right: 5px">
            <div class="center-cropped dGalleryRow"
                 data-image-url="http://pbs.twimg.com/profile_images/3323288933/120b2f736d1180c9708854159d84c0c5_400x400.jpeg"
                 style="background-image: url('https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT6D434LO8ddPE9TomVjs3iIC3RpKQXTR2lmJxci5gq933EWqU')">
                <button
                        style="display:none;margin: 5px;background-color: transparent;border: 0;"
                        class="pull-right bTransfer"><span style="color: white"
                                                           class="glyphicon glyphicon-transfer"></span>
                </button>
            </div>
        </div>
    </div>

</div>

</body>
</html>
