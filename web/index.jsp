<%@ page import="com.theah64.mock_api.models.Project" %>
<%@ page import="com.theah64.mock_api.models.JSON" %>
<%@ page import="java.util.List" %>
<%@ page import="com.theah64.mock_api.database.JSONS" %>
<%@ page import="java.sql.SQLException" %>
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
    <script>
        $(document).ready(function () {

            $("select#routes").on('change', function () {
                var selIndex = $(this).prop('selectedIndex');
                if (selIndex != 0) {
                    var selOption = $(this).find(":selected");
                    $("input#route").val(selOption.text());
                    $("textarea#response").val(JSON.stringify(selOption.data("response")));
                }
            });

            $("button#bClear").on('click', function () {
                $("input#route").val("");
                $("textarea#response").val("");
                $('select#routes option:eq(0)').attr('selected', 'selected');
            });

            $("button#bSubmit").on('click', function () {

                var resultDiv = $("div#resultDiv");
                resultDiv.hide();
                var route = $("input#route").val();
                var response = $("textarea#response").val();
                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                    },
                    url: "v1/save_json",
                    data: {route: route, response: response},
                    success: function (data) {

                        console.log(data);

                        if (!data.error) {
                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            $(resultDiv).html("<strong>Success! </strong> " + data.message);
                            $(resultDiv).show();
                        } else {
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> " + data.message);
                            $(resultDiv).show();
                        }
                    },
                    error: function () {
                        $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                        $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                        $(resultDiv).show();
                    }
                });
            })

        });
    </script>
</head>
<body>
<div class="container">

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
        <%--Available jsonList--%>
        <div class="col-md-2">
            <%
                List<JSON> jsonList = null;
                try {
                    jsonList = JSONS.getInstance().getAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            %>
            <select id="routes" class="form-control" title="Routes">
                <option value="">Select a route</option>
                <%
                    if (jsonList != null) {
                        for (final JSON json : jsonList) {
                %>
                <option data-response='<%=json.getResponse()%>' value="<%=json.getId()%>"><%=json.getRoute()%>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <%--Add new route panel--%>
        <div class="col-md-10">
            <input class="form-control" type="text" maxlength="50" id="route" placeholder="Route"><br>
            <textarea class="form-control" id="response" name="response" style="width: 100%;height: 50%"
                      placeholder="Response" title="JSON"></textarea>
            <br>

            <div id="resultDiv" style="display: none" class="alert">
            </div>


            <div class="pull-right">
                <button id="bClear" class="btn btn-default">CLEAR</button>
                <button id="bSubmit" class="btn btn-primary">SAVE</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
