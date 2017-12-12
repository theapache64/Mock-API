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

            var dSearchProgress = $("div#dSearchProgress");
            var dSearchResult = $("div#dSearchResult");
            var dTransferProgress = $("div#dTransferProgress");


            dSearchResult.on('mouseenter', 'div.dGalleryRow', function () {
                //do
                $(this).find("button.bTransfer").fadeIn("300");
            }).on('mouseleave', 'div.dGalleryRow', function () {
                //do
                $(this).find("button.bTransfer").fadeOut("300");
            });

            $("form#fImageSearch").on('submit', function (e) {
                e.preventDefault();
                var keyword = $.trim($("input#iKeyword").val());

                if (keyword !== "") {


                    $.ajax({
                        type: "POST",
                        beforeSend: function () {

                            $("form#fImageSearch :input").prop("disabled", true);

                            dSearchResult.hide();
                            dSearchProgress.slideDown(100);
                        },
                        data: {
                            keyword: keyword
                        },
                        url: "v1/search_images",
                        success: function (data) {
                            dSearchProgress.slideUp(100);
                            $("form#fImageSearch :input").prop("disabled", false);

                            if (!data.error) {

                                dSearchResult.html("");
                                dSearchResult.show();

                                $.each(data.data.images, function (index, image) {

                                    var imageRow = $("div#dSearchImageRow");

                                    $(imageRow)
                                        .find("div.dGalleryRow")
                                        .attr('data-image-url', image.image_url)
                                        .attr('data-thumb-url', image.thumb_url)
                                        .css('background-image', 'url(\'' + image.thumb_url + '\')');

                                    $("div#dSearchResult").append(imageRow.html());
                                });

                            } else {
                                alert(data.message);
                            }
                        },
                        error: function (e) {
                            $("form#fImageSearch :input").prop("disabled", false);
                            dSearchProgress.slideUp(100);
                            alert("Network error occurred, please check your connection");
                        }
                    });

                }
            });

            var isTranferInProgress = false;
            dSearchResult.on('click', 'button.bTransfer', function () {

                if (isTranferInProgress) {
                    alert("Failed, Another transfer in progress. Please try again later");
                    return;
                }

                var dImage = $(this).parent();
                var imageUrl = dImage.data('image-url');
                var thumbUrl = dImage.data('thumb-url');
                var isCompress = confirm("Do you want to compress the image?");

                $.ajax({
                    type: "POST",
                    beforeSend: function () {
                        dTransferProgress.slideDown(100);
                    },
                    data: {
                        image_url: imageUrl,
                        thumb_url: thumbUrl,
                        is_compress: isCompress
                    },
                    headers:{
                        "Authorization": "<%=project.getApiKey()%>"
                    },
                    url: "v1/add_to_project_images",
                    success: function (data) {
                        dTransferProgress.slideUp(100);

                        console.log(data);

                        if (!data.error) {

                            var imageRow = $("div#dProjectImageRow");

                            $(imageRow)
                                .find("div.dGalleryRow")
                                .attr('id', data.data.id)
                                .attr('data-image-url', data.data.image_url)
                                .attr('data-thumb-url', data.data.thumb_url)
                                .css('background-image', 'url(\'' + data.data.thumb_url + '\')');

                            $("div#dProjectImages").prepend(imageRow.html());

                        } else {
                            alert(data.message);
                        }
                    },
                    error: function (e) {
                        dTransferProgress.slideUp(100);
                        alert("Network error occurred, please check your connection");
                    }
                });

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

            <br>

            <div id="dTransferProgress" style="display: none" class="progress">
                <div class="progress-bar progress-bar-striped progress-bar-success active" role="progressbar"
                     aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%">
                    Transferring to project...
                </div>
            </div>
            <br>

            <div id="dProjectImages" style="height: 408px;overflow-y: hidden" class="row">

                <%

                    try {
                        List<Image> images = Images.getInstance().getAll(Images.COLUMN_PROJECT_ID, project.getId());

                        

                        for (final Image image : images) {
                %>

                <div class="col-md-2" style="margin-right: 5px">
                    <div class="center-cropped dGalleryRow"
                         id="<%=image.getId()%>"
                         data-image-url="<%=image.getImageUrl()%>"
                         data-thumb-url="<%=image.getThumbUrl()%>"
                         style="background-image: url('<%=image.getThumbUrl()%>')">
                        <button
                                style="display:none;margin: 5px;background-color: transparent;border: 0;"
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

            <div id="dSearchProgress" style="display: none" class="progress">
                <div class="progress-bar progress-bar-striped progress-bar-success active" role="progressbar"
                     aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%">
                    Searching...
                </div>
            </div>


            <br>


            <div id="dSearchResult" style="display:none;height: 408px;overflow-y: scroll" class="row">

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

    <div id="dProjectImageRow" style="display: none">
        <div class="col-md-2" style="margin-right: 5px">
            <div class="center-cropped dGalleryRow"
                 data-image-url="http://pbs.twimg.com/profile_images/3323288933/120b2f736d1180c9708854159d84c0c5_400x400.jpeg"
                 style="background-image: url('https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT6D434LO8ddPE9TomVjs3iIC3RpKQXTR2lmJxci5gq933EWqU')">
                <button
                        style="display:none;margin: 5px;background-color: transparent;border: 0;"
                        class="pull-right bDelete"><span style="color: white"
                                                         class="glyphicon glyphicon-trash"></span>
                </button>
            </div>
        </div>
    </div>

</div>

</body>
</html>
