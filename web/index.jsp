<%@ page import="com.theah64.mock_api.database.JSONS" %>
<%@ page import="com.theah64.mock_api.models.JSON" %>
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
    <title><%=project.getName()%> / MockAPI
    </title>
    <%@include file="common_headers.jsp" %>
    <script>
        function setLastModified(message, date) {
            $("p#pLastModified").html("Last modified: <b>" + message + "</b>");
            $("p#pLastModified").attr("title", date);
        }
        $(document).ready(function () {

            var editor = CodeMirror.fromTextArea(document.getElementById("response"), {
                lineNumbers: true,
                mode: "application/json",
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

            $("input#route, input#required_params, input#optional_params").on('keyup', function () {
                if (!event.ctrlKey && !event.altKey) {
                    var oldVal = $(this).val();
                    var newVal = $.trim(oldVal.toLowerCase().replace(/(\s+)/, '_'));
                    $(this).val(newVal);
                }
            });

            $("input#route").on('keyup', function () {
                if (!event.ctrlKey && !event.altKey) {
                    var oldVal = $(this).val();
                    $("input#external_api_url").val('<%=project.getBaseOgApiUrl()%>/' + oldVal);
                }
            });

            $(window).keydown(function(event){
                if (event.ctrlKey && event.altKey && event.keyCode == 70) {
                    var routeToSearch = prompt("Search route");
                    console.log("x:" + routeToSearch);
                    $("select#routes option").filter(function () {
                        return this.text.indexOf(routeToSearch) > 0;
                    }).attr('selected', true).trigger("change");

                }
            });


            editor.on('keyup', function () {


                if (event.ctrlKey && event.altKey && event.keyCode == 76) {
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));
                }


                if (event.ctrlKey && event.altKey && event.keyCode == 76) {
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));
                }


                if (event.ctrlKey && event.altKey && event.keyCode == 78) {
                    var successMsg = prompt("Success message", "This is a sample success message");
                    editor.getDoc().setValue(JSON.stringify(JSON.parse('{ "error": false, "message": "' + successMsg + '", "data": {} }'), undefined, 4));
                }


                if (event.ctrlKey && event.altKey && event.keyCode == 83) {
                    //'S' Pressed along with control and alt
                    var key = prompt("Key for the object");
                    if (key) {
                        var value = prompt("Value for " + key);
                        //check if the data int or not
                        editor.replaceSelection('"' + key + '":' + (isNaN(value) ? '"' + value + '"' : value));
                    } else {
                        alert("Can't accept empty key");
                    }
                }


                if (event.ctrlKey && event.altKey && event.keyCode == 69) {
                    var errorMsg = prompt("Error message", "This is a sample error message");
                    editor.getDoc().setValue(JSON.stringify(JSON.parse('{ "error": true, "message": "' + errorMsg + '"}'), undefined, 4));
                }

                if (event.ctrlKey && event.altKey && event.keyCode == 68) {

                    var selection = editor.getSelection();

                    if (selection.length > 0) {

                        var n = prompt("Number of nodes? ", 1);
                        var builder = "";
                        var temp = selection;

                        for (var i = 1; i < n; i++) {
                            temp = temp.replace(/\.*(\d+)\.*/g, function (fullMatch, n) {
                                return (Number(n) + 1);
                            });
                            builder += temp + "\n";
                        }

                        editor.replaceSelection(selection + "\n" + builder);
                    }
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

                            //Changing browser url without reloading the page
                            history.pushState(null, null, 'index.jsp?api_key=<%=project.getApiKey()%>&route=' + route);


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
                                $("input#external_api_url").val(data.data.external_api_url);

                                //Setting last modified
                                //$("p#pLastModified").html("Last modified: <b>" + data.data.last_modified + "</b>");
                                setLastModified(data.data.last_modified, data.data.last_modified_date);
                                if (data.data.delay > 0) {
                                    $("input#delay").val(data.data.delay);
                                }
                                $("input#description").val(data.data.description);
                                $("input#is_secure").prop('checked', data.data.is_secure);

                                editor.getDoc().setValue(JSON.stringify(JSON.parse(data.data.response), undefined, 4));

                            } else {

                                $(resultDiv).removeClass('alert-success').addClass('alert-danger');
                                $(resultDiv).html("<strong>Failure! </strong> " + data.message + ":" + link);
                                $(resultDiv).show();

                                $("input#required_params").val("");
                                $("input#optional_params").val("");
                                $("input#external_api_url").val("");
                                $("p#pLastModified").html("");
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
                $("input#external_api_url").val("");
                $("input#delay").val("");
                $("input#description").val("");
                $("p#pLastModified").html("");
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
                var isSecure = $("input#is_secure").is(":checked") ? true : false;
                var delay = $("input#delay").val();
                var description = $("input#description").val();
                var external_api_url = $("input#external_api_url").val();

                console.log("isSecure: " + isSecure);

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
                        optional_params: opParams,
                        external_api_url: external_api_url,
                        is_secure: isSecure,
                        delay: delay,
                        description: description

                    },
                    success: function (data) {
                        stopLoading(true);
                        console.log(data);

                        setLastModified("Just now", "Just now");

                        if (!data.error) {

                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "'>/" + route + "</a>";
                            $(resultDiv).html("<strong>Success! </strong> " + data.message + ": " + link);
                            $(resultDiv).show();

                            if ('id' in data.data) {
                                //Adding added route to select list
                                $("select#routes").append("<option value=" + data.data.id + ">" + route + " </option>");
                            }


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
                $("input#external_api_url").prop('disabled', true);
                $("button#bClear").prop('disabled', true);
                $("select#routes").prop('disabled', true);
                $("input#route").prop('disabled', true);
                $("input#delay").prop('disabled', true);
                $("input#description").prop('disabled', true);
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
                $("input#external_api_url").prop('disabled', false);
                $("input#delay").prop('disabled', false);
                $("input#description").prop('disabled', false);
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

            $("input#external_api_url").on('dblclick', function () {
                var oldVal = $(this).val();
                var newVal = oldVal.replace('<%=project.getBaseOgApiUrl()%>', '');
                $(this).val(newVal);
            });

            $("p#base_og_api_url").on('click', function () {
                var curVal = $.trim($(this).text());
                if (!curVal.startsWith("http")) {
                    //It's not a url so no pre-text
                    curVal = "";
                }

                var newUrl = prompt("Set new base og API URL", curVal);

                if (newUrl != null) {
                    $.ajax({
                        type: "POST",
                        beforeSend: function (request) {
                            startLoading(true);
                            request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                        },
                        url: "v1/update_project",
                        data: {
                            column: 'base_og_api_url',
                            value: newUrl

                        },
                        success: function (data) {
                            stopLoading(true);
                            if (!data.error) {
                                $("p#base_og_api_url").text(newUrl);

                            } else {
                                alert(data.message);
                            }
                        },
                        error: function () {
                            alert("Failed to update theProject");
                        }
                    });
                }


            });

            var selectedRoute = "<%=request.getParameter("route")%>";
            if (selectedRoute != "null") {
                $("select#routes option").filter(function () {
                    return this.text == selectedRoute;
                }).attr('selected', true).trigger("change");
            }

        });
    </script>
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

    <div class="row ">
        <div class="col-md-12 text-center">
            <h1><%=project.getName()%>
            </h1>
            <%
                if (project.getBaseOgApiUrl() != null) {
            %>
            <a href="compare.jsp?api_key=<%=project.getApiKey()%>">Compare with </a>
            <%
                }
            %>
            <p id="base_og_api_url">
                <%=project.getBaseOgApiUrl() != null ? project.getBaseOgApiUrl() : "Tap here to set base og API URL"%>
            </p>

            <p>
                <a href="logout.jsp"><i>(logout)</i></a>
            </p>


        </div>
    </div>


    <br>


    <p id="pLastModified" title="" class="pull-right"></p>


    <div class="row">
        <%--Available jsonList--%>
        <div class=" col-md-2">
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
            <label for="route">Route</label>
            <input class="form-control" type="text" maxlength="50" id="route" placeholder="Route"><br>
            <label for="required_params">Required params</label>
            <input class="form-control" type="text" id="required_params" placeholder="Required params"><br>
            <label for="optional_params">Optional params</label>
            <input class="form-control" type="text" id="optional_params" placeholder="Optional params"><br>
            <label for="external_api_url">External API URL</label>
            <input class="form-control" type="text" id="external_api_url" placeholder="External API URL"><br>


            <div class="row">

                <div class="col-md-1 checkbox">
                    <input type="checkbox" id="is_secure"/>Secure</label>
                </div>

                <div class="col-md-3">

                    <label for="delay">Delay</label>
                    <input class="form-control" type="number" placeholder="Delay" id="delay"/>
                </div>

                <div class="col-md-8">
                    <label for="description">Description</label>
                    <input class="form-control" type="text" placeholder="Description" id="description"/>
                </div>
            </div>

            <br>

            <div id="resultDiv" style="display: none" class="alert">
            </div>

            <br>


            <div class="row">
                <div class="pull-right">
                    <button id="bDelete" style="display: none" class="btn btn-danger btn-sm"><span
                            class="glyphicon glyphicon-trash"></span> DELETE
                    </button>
                    <button class="btn btn-sm btn-info" data-toggle="modal" data-target="#shortcuts">SHORTCUTS</button>
                    <button id="bClear" class="btn btn-info  btn-sm"><span class="glyphicon glyphicon-flash"></span>
                        CLEAR
                    </button>
                    <button id="bSubmit" class="btn btn-primary  btn-sm"><span class="glyphicon glyphicon-save"></span>
                        SAVE
                    </button>
                </div>
            </div>

            <br>

            <textarea class="form-control" id="response" name="response"
                      placeholder="Response" title="JSON"></textarea>
            <br>


        </div>
    </div>

    <div class="modal fade" id="shortcuts" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Shortcuts</h4>
                </div>
                <div class="modal-body">
                    <p><code>Control + Alt + L </code>To format JSON</p>
                    <p><code>Control + Alt + N </code>To get default success response</p>
                    <p><code>Control + Alt + E </code>To get default error response</p>
                    <p><code>Control + Alt + D </code>To duplicate selection (with numerical increment)</p>
                    <p><code>Control + Alt + S </code>To insert a string object at selected position</p>
                    <p><code>Control + Alt + F </code>To search for a route</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

</div>

</body>
</html>
