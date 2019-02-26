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
            var dProjectImages = $("div#dProjectImages");
            var dSearchResult = $("div#dSearchResult");
            var dTransferProgress = $("div#dTransferProgress");
            var dProgress = $("div#dProgress");
            var dProgressChild = $("div#dProgressChild");


            dSearchResult.on('mouseenter', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bTransfer").fadeIn("300");
            }).on('mouseleave', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bTransfer").fadeOut("300");
            });


            dProjectImages.on('mouseenter', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bDelete").fadeIn(100);
            }).on('mouseleave', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bDelete").fadeOut(100);
            });

            $("body").on('click', 'div.dGalleryRow1', function () {
                var imageUrl = $(this).find("img").data("image-url");
                $("img#imgImage").attr('src', imageUrl);
                $("div#image_viewer").modal("show");
            });

            dProjectImages.on('click', 'button.bDelete', function (e) {

                e.stopPropagation();

                var image = $(this).siblings("img");
                var id = image.attr('id');

                $.ajax({
                    type: "POST",
                    beforeSend: function () {
                        dProgressChild.text("Deleting image...");
                        dProgress.slideDown(100);
                    },
                    headers: {
                        Authorization: "<%=project.getApiKey()%>"
                    },
                    data: {
                        id: id
                    },
                    url: "v1/delete_image",
                    success: function (data) {
                        dProgress.slideUp(200);

                        if (!data.error) {
                            image.parent().remove();
                        } else {
                            alert(data.message);
                        }
                    },
                    error: function (e) {
                        dProgress.slideUp(200);
                        alert("Network error occurred, please check your connection");
                    }
                });

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
                                        .find("img")
                                        .attr('data-image-url', image.image_url)
                                        .attr('data-thumb-url', image.thumb_url)
                                        .attr('src', image.thumb_url);

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

            var isTransferInProgress = false;
            dSearchResult.on('click', 'button.bTransfer', function (e) {
                e.stopPropagation();

                if (isTransferInProgress) {
                    alert("Failed, Another transfer in progress. Please try again later");
                    return;
                }

                var dImage = $(this).siblings("img");
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
                    headers: {
                        "Authorization": "<%=project.getApiKey()%>"
                    },
                    url: "v1/add_to_project_images",
                    success: function (data) {
                        dTransferProgress.slideUp(100);

                        console.log(data);

                        if (!data.error) {

                            var imageRow = $("div#dProjectImageRow");

                            $(imageRow)
                                .find("img")
                                .attr('id', data.data.id)
                                .attr('data-image-url', data.data.image_url)
                                .attr('data-thumb-url', data.data.thumb_url)
                                .attr('src', data.data.thumb_url);

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

</head>
<body>
<%@include file="nav_bar.jsp" %>

<div class="container">


    <div class="modal fade" id="image_viewer" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">

                <div class="modal-body content-centred">
                    <img id="imgImage"
                         alt="image-failed-to-load"
                         style="display: block;
                                margin-left: auto;
                                margin-right: auto;
                                max-width: 500px;
                                max-height: 500px;"
                         src="https://vignette.wikia.nocookie.net/dbxfanon/images/6/67/Iron_Man.png/revision/latest?cb=20160403042153"/>
                </div>

            </div>
        </div>
    </div>

    <div class="row">
        <div id="dProgress" style="display: none" class="progress">
            <div id="dProgressChild" class="progress-bar progress-bar-striped progress-bar-danger active"
                 role="progressbar"
                 aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%">

            </div>
        </div>
    </div>

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
                        List<Image> images = Images.Companion.getInstance().getAll(Images.COLUMN_PROJECT_ID, project.getId());


                        for (final Image image : images) {
                %>

                <div class="col-md-2 dGalleryRow1">
                    <img class="center-cropped dGalleryRow"
                         id="<%=image.getId()%>"
                         data-image-url="<%=image.getImageUrl()%>"
                         data-thumb-url="<%=image.getThumbUrl()%>"
                         src="<%=image.getThumbUrl()%>">

                    <button
                            class="pull-right bDelete"><span style="color: white"
                                                             class="glyphicon glyphicon-remove"></span>
                    </button>
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
                    &nbsp;
                </button>
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
        <div class="col-md-2 dGalleryRow1">
            <img class="center-cropped dGalleryRow"
                 id=""
                 data-image-url=""
                 data-thumb-url=""
                 src="">

            <button class="pull-right bTransfer">
                <span style="color: white" class="glyphicon glyphicon-transfer"></span>
            </button>
        </div>
    </div>

    <div id="dProjectImageRow" style="display: none">
        <div class="col-md-2 dGalleryRow1">
            <img class="center-cropped dGalleryRow"
                 data-image-url=""
                 data-thumb-url=""
                 src="">

            <button
                    class="pull-right bDelete"><span style="color: white"
                                                     class="glyphicon glyphicon-remove"></span>
            </button>
        </div>
    </div>

</div>

</body>
</html>
