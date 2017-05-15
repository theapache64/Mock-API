<%@ page import="com.theah64.mock_api.database.JSONS" %>
<%@ page import="com.theah64.mock_api.models.JSON" %>
<%@ page import="com.theah64.mock_api.models.Project" %>
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

            var editor = CodeMirror.fromTextArea(document.getElementById("response"), {
                lineNumbers: true,
                mode: "application/json",
                matchBrackets: true
            });

            $("input#route, input#required_params, input#optional_params").on('keyup', function () {
                var oldVal = $(this).val();
                var newVal = $.trim(oldVal.toLowerCase().replace(/(\s+)/, '_'));
                $(this).val(newVal);
            });


            editor.on('keyup', function () {
                if (event.ctrlKey && event.altKey && event.keyCode == 76) {
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));
                }
            });


            $("select#routes").on('change', function () {
                var selIndex = $(this).prop('selectedIndex');
                if (selIndex != 0) {

                    var selOption = $(this).find(":selected");
                    var route = $.trim(selOption.text());
                    $.ajax({
                        type: "GET",
                        beforeSend: function () {
                            startLoading(true);
                        },
                        url: "fetch_json/<%=project.getName()%>/" + route,
                        success: function (data) {
                            stopLoading(true);
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "'>/" + route + "</a>";

                            if (!data.error) {
                                $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                                $(resultDiv).html("<strong>Success! </strong> " + data.message + " : " + link);
                                $(resultDiv).show();

                                $("input#route").val(route);
                                $("button#bDelete").show();

                                $("input#required_params").val(data.data.required_params);
                                $("input#optional_params").val(data.data.optional_params);
                                editor.getDoc().setValue(JSON.stringify(JSON.parse(data.data.response), undefined, 4));

                            } else {

                                $(resultDiv).removeClass('alert-success').addClass('alert-danger');
                                $(resultDiv).html("<strong>Failure! </strong> " + data.message + ":" + link);
                                $(resultDiv).show();

                                $("input#required_params").val("");
                                $("input#optional_params").val("");
                                $("input#route").val("");
                                $("button#bDelete").hide();

                                editor.getDoc().setValue("");
                            }


                        },
                        error: function () {
                            stopLoading(true);
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                            $(resultDiv).show();
                        }
                    });

                } else {
                    $("button#bDelete").hide();
                }
            });

            $("button#bClear").on('click', function () {
                $("input#route").val("");
                editor.getDoc().setValue("");
                $("input#required_params").val("");
                $("input#optional_params").val("");
                $("select#routes").val($("select#routes option:first").val());
                $("button#bDelete").hide();
            });

            $("button#bSubmit").on('click', function () {

                var resultDiv = $("div#resultDiv");
                resultDiv.hide();
                var route = $("input#route").val();
                var response = editor.getDoc().getValue();
                var reqParams = $("input#required_params").val();
                var opParams = $("input#optional_params").val();

                //Processing the add/update request
                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading(true);
                        request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                    },
                    url: "v1/save_json",
                    data: {
                        route: route,
                        response: response,
                        required_params: reqParams,
                        optional_params: opParams
                    },
                    success: function (data) {
                        stopLoading(true);
                        console.log(data);

                        if (!data.error) {
                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "'>/" + route + "</a>";
                            $(resultDiv).html("<strong>Success! </strong> " + data.message + ": " + link);
                            $(resultDiv).show();

                            //Adding added route to select list
                            $("select#routes").append("<option value=" + data.data.id + ">" + route + " </option>");

                        } else {
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> " + data.message);
                            $(resultDiv).show();
                        }
                    },
                    error: function () {
                        stopLoading(true);
                        $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                        $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                        $(resultDiv).show();
                    }
                });
            });

            function startLoading(isSubmit) {
                $("button#bDelete").prop('disabled', true);
                $("button#bSubmit").prop('disabled', true);
                $("input#required_params").prop('disabled', true);
                $("input#optional_params").prop('disabled', true);
                $("button#bClear").prop('disabled', true);
                $("select#routes").prop('disabled', true);
                $("input#route").prop('disabled', true);
                editor.setOption('readOnly', 'nocursor');
                $("div#resultDiv").hide();

                if (isSubmit) {
                    $("button#bSubmit").html('<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> SAVE');
                } else {
                    $("button#bDelete").html('<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> DELETE');
                }
            }

            function stopLoading(isSubmit) {
                $("div#resultDiv").show();
                $("button#bDelete").prop('disabled', false);
                $("button#bSubmit").prop('disabled', false);
                $("button#bClear").prop('disabled', false);
                $("select#routes").prop('disabled', false);
                $("input#route").prop('disabled', false);
                $("input#required_params").prop('disabled', false);
                $("input#optional_params").prop('disabled', false);
                editor.setOption('readOnly', false);


                if (isSubmit) {
                    $("button#bSubmit").html('<span class="glyphicon glyphicon-save"></span> SAVE');
                } else {
                    $("button#bDelete").html('<span class="glyphicon glyphicon-trash"></span> DELETE');
                }
            }

            $("button#bDelete").on('click', function () {

                if (!confirm('Do you really want to delete this route?')) {
                    return;
                }

                var selOption = $("select#routes").find(":selected");

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading(false);
                        request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                    },
                    url: "v1/delete_json",
                    data: {id: selOption.val()},
                    success: function (data) {
                        stopLoading(false);
                        console.log(data);

                        if (!data.error) {
                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            $(resultDiv).html("<strong>Success! </strong> " + data.message);
                            $(resultDiv).show();

                            //Adding added route to select list
                            $("select#routes option[value='" + selOption.val() + "']").remove();
                            $("button#bClear").click();
                        } else {
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> " + data.message);
                            $(resultDiv).show();
                        }
                    },
                    error: function () {
                        stopLoading(false);
                        $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                        $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                        $(resultDiv).show();
                    }
                });

            });


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
                    jsonList = JSONS.getInstance().getAll(project.getId());
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
                <option value="<%=json.getId()%>"><%=json.getRoute()%>
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
            <input class="form-control" type="text" id="required_params" placeholder="Required params"><br>
            <input class="form-control" type="text" id="optional_params" placeholder="Optional params"><br>

            <textarea class="form-control" id="response" name="response" style="width: 100%;height: 50%"
                      placeholder="Response" title="JSON"></textarea>
            <br>

            <div id="resultDiv" style="display: none" class="alert">
            </div>


            <div class="pull-right">
                <button id="bDelete" style="display: none" class="btn btn-danger btn-sm"><span
                        class="glyphicon glyphicon-trash"></span> DELETE
                </button>
                <button id="bClear" class="btn btn-info  btn-sm"><span class="glyphicon glyphicon-flash"></span> CLEAR
                </button>
                <button id="bSubmit" class="btn btn-primary  btn-sm"><span class="glyphicon glyphicon-save"></span> SAVE
                </button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
