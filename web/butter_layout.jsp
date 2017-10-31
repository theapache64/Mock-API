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


    </style>
</head>
<body>
<div class="container">

    <div class="row">
        <div class="col-md-12">
            <h1>ButterLayout</h1>
        </div>
    </div>

    <div class="row">
        <div class="col-md-5">
            <textarea id="xmlCode" placeholder="Paste your XML code here" class="form-control" style="width: 100%;height: 80%"></textarea>
        </div>
        <div class="col-md-2 text-center">
            <button class="btn btn-primary"> <span class="glyphicon glyphicon glyphicon-cog"></span> Generate </button>
        </div>
        <div class="col-md-5">
            <textarea id="javaCode" placeholder="Your java code will get generate here" class="form-control" style="width: 100%;height: 80%"></textarea>
        </div>
    </div>
</div>
</body>

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

    });
</script>
</html>