<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 12/9/16
  Time: 4:08 PM=
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>ButterLayout
    </title>
    <%@include file="common_headers.jsp" %>
    <script src="https://codemirror.net/mode/xml/xml.js"></script>
    <%--<script src="https://codemirror.net/addon/edit/matchbrackets.js"></script>
    <link rel="stylesheet" href="https://codemirror.net/addon/hint/show-hint.css">
    <script src="https://codemirror.net/addon/hint/show-hint.js"></script>--%>
    <script src="https://codemirror.net/mode/clike/clike.js"></script>

    <style>
        .CodeMirror-fullscreen {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            height: auto;
            z-index: 9;
        }

        .CodeMirror {
            height: 80%;
        }

    </style>

    <script>

        $(document).ready(function () {


            var xmlEditor = CodeMirror.fromTextArea(document.getElementById("xmlCode"), {
                lineNumbers: true,
                mode: "text/html",
                matchBrackets: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                }
            });

            var javaEditor = CodeMirror.fromTextArea(document.getElementById("javaCode"), {
                lineNumbers: true,
                mode: "text/x-java",
                matchBrackets: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                }
            });

            function startLoading() {
                xmlEditor.setOption('readOnly', 'nocursor');
                $("button#bGenButterLayout").prop('disabled', true);
            }

            function stopLoading() {
                xmlEditor.setOption('readOnly', 'false');
                $("button#bGenButterLayout").prop('disabled', false);
            }

            $("button#bGenButterLayout").click(function () {

                $("p#error_message").text("");

                var xmlData = xmlEditor.getDoc().getValue();
                var rSeries = $("select#rSeries").val();

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading();
                    },
                    url: "butter_layout_engine",
                    data: {
                        xml_data: xmlData,
                        r_series : rSeries
                    },
                    success: function (data) {
                        stopLoading();
                        console.log(data);


                        if (!data.error) {
                            javaEditor.getDoc().setValue(data.data.butter_layout);
                            $("p#error_message").text("");
                        } else {
                            $("p#error_message").text(data.message);
                        }
                    },
                    error: function () {
                        stopLoading();
                        $("p#error_message").text("Network error occurred, Please check your connection");
                    }
                });


            });


        });
    </script>

</head>
<body>
<div class="container">

    <div class="row">
        <div class="col-md-12">
            <h1>ButterLayout</h1>
        </div>
    </div>


    <br>


    <div class="row">
        <div class="col-md-12">
            <p id="error_message" class="text-danger"></p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-5">
            <textarea id="xmlCode" placeholder="Paste your XML code here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
        <div class="col-md-2 text-center">
            <select id="rSeries" class="form-control">
                <option value="R">R</option>
                <option value="R2">R2</option>
            </select>
            <br>
            <button id="bGenButterLayout" class="btn btn-primary"><span
                    class="glyphicon glyphicon glyphicon-cog"></span> Generate
            </button>
        </div>
        <div class="col-md-5">
            <textarea id="javaCode" placeholder="Your java code will get generate here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
    </div>


</div>
</body>


</html>