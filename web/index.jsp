<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.mock_api.servlets.SaveJSONServlet" %>
<%@ page import="com.theah64.mock_api.utils.RandomResponseGenerator" %>
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

        var isAlertResult = false;
        $(document).ready(function () {


            function formatJSON() {
                editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));
            }

            var editor = CodeMirror.fromTextArea(document.getElementById("response"), {
                lineNumbers: true,
                mode: "application/json",
                matchBrackets: true,
                foldGutter: true,
                extraKeys: {
                    "Ctrl-Q": function (cm) {
                        cm.foldCode(cm.getCursor());
                    },
                    "F11": function (cm) {
                        isAlertResult = !cm.getOption("fullScreen");
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                },
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });

            $("span.randomItems").on('click', function () {

                var x = $(this).text();
                if (x.indexOf("(\\d+)") !== -1) {
                    var count = prompt("How many?");
                    if (count != null) {
                        x = x.replace("(\\d+)", count);
                    } else {
                        x = "";
                    }
                }

                editor.replaceSelection(x);
                editor.focus();
            });


            $("body").on('keyup', "input#route, input.iNames", function () {
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

            $("a#aAddResponse").on('click', function () {

                var responseName = prompt("Enter response name?");
                if (responseName !== null) {

                    var response = editor.getDoc().getValue();

                    $.ajax({
                        type: "POST",
                        beforeSend: function () {
                            startLoading(true);
                        },
                        data: {
                            name: responseName,
                            response: response,
                            project_id: <%=project.getId()%>,
                            route: $("input#route").val()
                        },
                        url: "v1/add_response",
                        headers: {"Authorization": "<%=project.getApiKey()%>"},
                        success: function (data) {

                            stopLoading(true);

                            if (!data.error) {
                                console.log("Adding new response to response select box");
                                $('select#responses').append($('<option>', {
                                    value: data.data.id,
                                    text: responseName,
                                    selected: true
                                }));
                                $("a#aDeleteResponse").show();
                            } else {
                                alert(data.message);
                            }


                        },
                        error: function () {
                            stopLoading(true);
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                            $(resultDiv).show();
                        }
                    });
                }


            });


            $(window).keydown(function (event) {

                //F7
                if (event.keyCode == 118) {
                    $("button#bSubmit").click();
                }

                if (event.ctrlKey && event.altKey && event.keyCode == 70) {

                    var sValue = prompt("Search for response");
                    if (sValue == null || sValue == "") {
                        return;
                    }

                    $.ajax({
                        type: "GET",
                        beforeSend: function () {
                            startLoading(true);
                        },
                        url: "v1/search?column=<%=Routes.COLUMN_DEFAULT_RESPONSE%>&value=" + sValue,
                        headers: {"Authorization": "<%=project.getApiKey()%>"},
                        success: function (data) {

                            stopLoading(true);

                            if (!data.error) {

                                console.log(data.data.routes);

                                //Clearing old data
                                $("div#search_result_content").html("");
                                $("div#search_result").modal("show");

                                $.each(data.data.routes, function (key, value) {
                                    $("div#search_result_content").append('<button class="btn btn-default btn-search-result">' + value + '</button> ');
                                });

                                $("button.btn-search-result").on('click', function () {
                                    var routeName = $(this).text();

                                    $("select#routes option").filter(function () {
                                        return this.text == routeName;
                                    }).attr('selected', true).trigger("change");

                                    $("div#search_result").modal("hide");
                                });

                                $("#search_result_title").text("Search result for '" + sValue + "'");
                                $("div#search_result").modal("show");

                            } else {
                                alert(data.message);
                            }


                        },
                        error: function () {
                            stopLoading(true);
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                            $(resultDiv).show();
                        }
                    });
                }

                if (event.keyCode == 112) {
                    event.preventDefault();

                    var routeToSearch = prompt("Search route");
                    console.log("x:" + routeToSearch);
                    $("select#routes option").filter(function () {
                        console.log("text: " + this.text + ":" + routeToSearch);
                        return this.text == routeToSearch || this.text.indexOf(routeToSearch) > 0;
                    }).attr('selected', true).trigger("change");

                }


                //F4
                if (event.keyCode == 115) {

                    var route = $("input#route").val();
                    var responseClassName = prompt("Response class name ? ", "GetProductResponse");
                    window.open('v1/get_api_interface_method?name=' + route + "&project_name=<%=project.getName()%>&response_class=" + responseClassName);

                }

            });


            editor.on('keyup', function () {


                //Control + Alt + I
                if (event.ctrlKey && event.altKey && event.keyCode == 73) {
                    var dimen = prompt("Enter dimension", "500x500");
                    dimen = dimen.split("x");
                    if (dimen.length == 2) {

                        var imgCount = prompt("Enter number of images", "1");
                        var imageUrls = "";
                        for (var j = 0; j < imgCount; j++) {
                            var min = 1;
                            var max = 1084;
                            var imageId = Math.floor(Math.random() * (max - min + 1)) + min;
                            var imageUrl = "https://picsum.photos/" + dimen[0] + "/" + dimen[1] + "/?image=" + imageId;
                            imageUrls += '"' + imageUrl + '",';
                        }

                        editor.replaceSelection(imageUrls.substring(0, imageUrls.length - 1));

                    } else {
                        alert("Invalid dimension format " + dimen);
                    }

                }


                //Control + Alt + R
                if (event.ctrlKey && event.altKey && event.keyCode == 82) {
                    $("#random").modal("show");
                    setTimeout(function () {
                        $('input#iWords').focus();
                    }, 500);
                }


                //Route to java model


                //Control + Alt + M
                if (event.ctrlKey && event.altKey && event.keyCode == 77) {

                    var selection = editor.getSelection();

                    if (selection.length > 0) {
                        var modelName = prompt("Model name ? ", "MyModel");
                        if (modelName) {

                            var isRetrofitModel = confirm("is this a Retrofit model ?");

                            $.ajax({
                                type: "POST",
                                beforeSend: function () {
                                    startLoading(true);
                                },
                                data: {
                                    jo_string: selection,
                                    is_retrofit_model: isRetrofitModel,
                                    model_name: modelName
                                },
                                url: "v1/json_to_model_engine",
                                success: function (data) {

                                    stopLoading(true);

                                    if (!data.error) {
                                        console.log(data.data.data);

                                        var newWindow = window.open();
                                        newWindow.document.write(data.data.data);

                                    } else {
                                        alert(data.message);
                                    }


                                },
                                error: function () {
                                    stopLoading(true);
                                    $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                                    $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                                    $(resultDiv).show();
                                }
                            });


                        }


                    }

                }

                if (event.ctrlKey && event.altKey && event.keyCode == 76) {
                    formatJSON();
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

                //D
                if (event.ctrlKey && event.altKey && event.keyCode == 68) {

                    var selection = editor.getSelection();

                    if (selection.length > 0) {

                        var n = prompt("Number of nodes? ", 1);

                        if (n) {
                            var builder = "";
                            var temp = selection;

                            var isIntFound = false;

                            for (var i = 1; i < n; i++) {
                                temp = temp.replace(/\.*\[(\d+)\]\.*/g, function (fullMatch, n) {
                                    isIntFound = true;
                                    return "[" + (Number(n) + 1) + "]";
                                });
                                builder += temp + "\n";
                            }


                            if (isIntFound) {
                                selection = selection.replace(/\[/g, "");
                                selection = selection.replace(/\]/g, "");

                                builder = builder.replace(/\[/g, "");
                                builder = builder.replace(/\]/g, "");
                            }

                            editor.replaceSelection(selection + "\n" + builder);
                        }
                    }
                }

            });


            //Response listener
            $("select#responses").on('change', function () {

                var selResp = $("select#responses").find(":selected");
                console.log("Selected response:");
                console.log(selResp);

                if (selResp.val() === 'default_response') {
                    $("a#aDeleteResponse").hide();
                } else {
                    $("a#aDeleteResponse").show();
                }


                //load response here
                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading(true);
                        request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                    },
                    url: "v1/get_response",
                    data: {
                        id: selResp.val(),
                        route_name: $('input#route').val(),
                        project_name: '<%=project.getName()%>'
                    },
                    success: function (data) {
                        stopLoading(true);

                        if (!data.error) {

                            history.pushState(null, null, 'index.jsp?api_key=<%=project.getApiKey()%>&route=' + $('input#route').val() + "&response_id=" + data.data.id);

                            //Deleted response from db
                            editor.getDoc().setValue(JSON.stringify(JSON.parse(data.data.response), undefined, 4));
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


            //Delete response
            $("a#aDeleteResponse").on('click', function () {

                if (confirm("Do you really want to delete the response?")) {
                    $.ajax({
                        type: "POST",
                        beforeSend: function (request) {
                            startLoading(true);
                            request.setRequestHeader('Authorization', '<%=project.getApiKey()%>')
                        },
                        url: "v1/delete_response",
                        data: {
                            id: $('select#responses :selected').val()
                        },
                        success: function (data) {
                            stopLoading(true);
                            console.log(data);

                            if (!data.error) {
                                //Deleted response from db
                                $('select#responses option:selected').remove();
                                $('select#responses').trigger('change');
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

                            console.log(data);

                            //Changing browser url without reloading the page

                            stopLoading(true);
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "?" + data.data.dummy_params + "'>/" + route + "</a>";

                            if (!data.error) {

                                $("input#route").val(route);

                                //Clearing responses select box
                                $("select#responses")
                                    .find("option")
                                    .remove()
                                    .end()
                                    .append('<option value="default_response">Default response</option>');

                                $.each(data.data.responses, function (i, item) {
                                    $("select#responses").append('<option value="' + item.id + '">' + item.name + '</option>');
                                });

                                var respoId = '<%=request.getParameter("response_id")==null ? Routes.COLUMN_DEFAULT_RESPONSE : request.getParameter("response_id")%>';

                                var isExist = $("select#responses option[value='" + respoId + "']").length > 0;
                                if (isExist) {
                                    $("select#responses").val(respoId).trigger('change');
                                }


                                $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                                $(resultDiv).html("<strong>Success! </strong> " + data.message + " : " + link);
                                $(resultDiv).show();


                                $("button#bDelete").show();
                                $("a#aParamResp").show();
                                $("a#aParamResp").attr('href', 'param_resp.jsp?api_key=<%=project.getApiKey()%>&route_name=' + route);
                                $("div#response_panel").css('visibility', 'visible');

                                //Reseting req para
                                $("form#fParam").html("");

                                //Looping through required params
                                $.each(data.data.params, function (i, item) {

                                    var paramRow = $("div#dParamRow");

                                    var sDataTypesId = "sDataTypes" + item.id;
                                    $(paramRow).find("select.sDataTypes").attr('id', sDataTypesId);

                                    var iNameId = "iName" + item.id;
                                    $(paramRow).find("input.iNames").attr('id', iNameId);

                                    var iDefauleValuesId = "iDefaultValue" + item.id;
                                    $(paramRow).find("input.iDefaultValues").attr('id', iDefauleValuesId);

                                    var taDescriptionsId = "taDescription" + item.id;
                                    $(paramRow).find("textarea.taDescriptions").attr('id', taDescriptionsId);

                                    var iIsRequiredId = "iIsRequired" + item.id;
                                    $(paramRow).find("input.iIsRequired").attr('id', iIsRequiredId);

                                    var iIsRequiredHiddenId = iIsRequiredId + "Hidden";
                                    $(paramRow).find("input.iIsRequiredHidden").attr('id', iIsRequiredHiddenId);

                                    $("form#fParam").append(paramRow.html());


                                    $("input#" + iNameId).val(item.name);
                                    $("select#" + sDataTypesId).val(item.data_type);
                                    $("input#" + iDefauleValuesId).val(item.default_value);
                                    $("textarea#" + taDescriptionsId).val(item.description);
                                    $("input#" + iIsRequiredId).prop('checked', item.is_required);

                                    //Setting names


                                });

                                $("input#external_api_url").val(data.data.external_api_url);

                                //Setting last modified
                                //$("p#pLastModified").html("Last modified: <b>" + data.data.last_modified + "</b>");
                                setLastModified(data.data.last_modified, data.data.last_modified_date);
                                if (data.data.delay > 0) {
                                    $("input#delay").val(data.data.delay);
                                }
                                $("textarea#description").val(data.data.description);
                                $("input#is_secure").prop('checked', data.data.is_secure);

                                //editor.getDoc().setValue(JSON.stringify(JSON.parse(data.data.default_response), undefined, 4));


                            } else {

                                $(resultDiv).removeClass('alert-success').addClass('alert-danger');
                                $(resultDiv).html("<strong>Failure! </strong> " + data.message + ":" + link);
                                $(resultDiv).show();

                                $("input#external_api_url").val("");
                                $("p#pLastModified").html("");
                                $("input#route").val("");
                                $("button#bDelete").hide();
                                $("a#aParamResp").hide();
                                $("div#response_panel").css('visibility', 'hidden');

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
                    $("a#aParamResp").hide();
                    $("div#response_panel").css('visibility', 'hidden');
                }
            });

            $("button#bClear").on('click', function () {
                $("input#route").val("");
                editor.getDoc().setValue("");
                $("input#external_api_url").val("");
                $("input#delay").val("");
                $("textarea#description").val("");
                $("p#pLastModified").html("");
                $("select#routes").val($("select#routes option:first").val());
                $("button#bDelete").hide();
                $("a#aParamResp").hide();
                $("div#response_panel").css('visibility', 'hidden');
            });

            $("button#bSubmit").on('click', function () {

                $('#fParam *').filter(':input').each(function () {
                    //your code here
                    if ($(this).attr('name') === '<%=SaveJSONServlet.KEY_IS_REQUIRED%>') {
                        var curId = $(this).attr('id');
                        if (curId.indexOf("Hidden") == -1) {
                            var hidId = "input#" + curId + "Hidden";
                            if ($(this).is(":checked")) {
                                console.log("Disabling hidden:" + hidId);
                                $(hidId).attr('disabled', true);
                            } else {
                                console.log("Enabling hidden:" + hidId);
                                $(hidId).attr('disabled', false);
                            }
                        }

                    }
                });

                console.log("DONE!");

                var resultDiv = $("div#resultDiv");
                resultDiv.hide();
                var route = $("input#route").val();
                var response = editor.getDoc().getValue();
                var params = $("form#fParam").serialize();
                var opParams = $("input#optional_params").val();
                var isSecure = $("input#is_secure").is(":checked") ? true : false;
                var delay = $("input#delay").val();
                var description = $("textarea#description").val();
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
                    data: params +
                    "&name=" + route +
                    "&response_id=" + $('select#responses :selected').val() +
                    "&response=" + response +
                    "&optional_params=" + opParams +
                    "&external_api_url=" + external_api_url +
                    "&is_secure=" + isSecure +
                    "&delay=" + delay +
                    "&description=" + description,
                    success: function (data) {
                        stopLoading(true);
                        console.log(data);

                        setLastModified("Just now", "Just now");

                        if (!data.error) {

                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "'>/" + route + "</a>";
                            $(resultDiv).html("<strong>Success! </strong> " + data.message + ": " + link);
                            $(resultDiv).show();

                            console.log(data.data);
                            preSelRespId = $('select#responses :selected').val();
                            if ('id' in data.data) {

                                //Adding added route to select list
                                $("select#routes").append("<option value=" + data.data.id + ">" + route + " </option>");

                                //alert("selecting : " + data.data.id);
                                $("select#routes").val(data.data.id).trigger('change');

                                history.pushState(null, null, 'index.jsp?api_key=<%=project.getApiKey()%>&route=' + route);

                            }

                        } else {
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> " + data.message);
                            $(resultDiv).show();
                        }

                        if (isAlertResult) {
                            alert(data.message);
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
                $("a#aParamResp").prop('disabled', true);
                $("button#bSubmit").prop('disabled', true);
                $("div#response_panel").prop('disabled', true);
                $("input#external_api_url").prop('disabled', true);
                $("button#bClear").prop('disabled', true);
                $("select#routes").prop('disabled', true);
                $("input#route").prop('disabled', true);
                $("input#delay").prop('disabled', true);
                $("textarea#description").prop('disabled', true);
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
                $("a#aParamResp").prop('disabled', false);
                $("button#bSubmit").prop('disabled', false);
                $("div#response_panel").prop('disabled', false);
                $("button#bClear").prop('disabled', false);
                $("select#routes").prop('disabled', false);
                $("input#route").prop('disabled', false);
                $("input#external_api_url").prop('disabled', false);
                $("input#delay").prop('disabled', false);
                $("textarea#description").prop('disabled', false);
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
            if (selectedRoute !== "null") {
                $("select#routes option").filter(function () {
                    return this.text === selectedRoute;
                }).attr('selected', true).trigger("change");
            }


            $("#fRandom").submit(function (e) {
                e.preventDefault();

                $("#random").modal("hide");


                var randomWhat = $(this).serializeArray()[0].value;
                var count = 1;
                if (randomWhat == 'words' || randomWhat == 'paragraphs') {
                    count = prompt("How many?", count);
                }

                if (!isNaN(count)) {

                    $.ajax({
                        type: "POST",
                        beforeSend: function () {
                            startLoading(true);
                        },
                        data: {
                            random_what: randomWhat,
                            count: count
                        },
                        url: "v1/get_random",
                        success: function (data) {

                            stopLoading(true);

                            if (!data.error) {
                                editor.replaceSelection("\"" + data.data.random_output + "\"");
                            } else {
                                alert(data.message);
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
                    alert("Bad count!");
                }

            });


            $("a#aAddReqParam").on('click', function () {
                var paramRow = $("div#dParamRow").html();
                $("form#fParam").append(paramRow);
            });

            $(".fParam").on('click', 'a.aCloseParam', function () {
                $(this).parent().parent().remove();
            });

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

        <br><br>

        <%--Available jsonList--%>
        <div class=" col-md-2">
            <%
                List<Route> jsonList = null;
                try {
                    jsonList = Routes.getInstance().getAll(project.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            %>
            <label for="routes">Select route</label>
            <select id="routes" class="form-control" title="Routes">
                <option value="">Select a route</option>
                <%
                    if (jsonList != null) {
                        for (final Route json : jsonList) {
                %>
                <option value="<%=json.getId()%>"><%=json.getName()%>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div>


        <div id="dParamRow" style="display: none">

            <div class="row" style="margin-bottom: 10px;">


                <div class="col-md-2">
                    <input class="iNames form-control" type="text" name="<%=SaveJSONServlet.KEY_PARAMS%>"
                           placeholder="Name"><br>
                </div>


                <div class="col-md-2">
                    <select name="<%=SaveJSONServlet.KEY_DATA_TYPES%>" class="sDataTypes form-control">
                        <option value="String">String</option>
                        <option value="Integer">Integer</option>
                        <option value="Boolean">Boolean</option>
                        <option value="Long">Long</option>
                        <option value="Float">Float</option>
                        <option value="Double">Double</option>
                    </select>
                </div>

                <div class="col-md-3">
                    <input class="iDefaultValues form-control" name="<%=SaveJSONServlet.KEY_DEFAULT_VALUES%>"
                           type="text" placeholder="Default value"><br>
                </div>

                <div class="col-md-3">
                    <textarea class="taDescriptions form-control" name="<%=SaveJSONServlet.KEY_DESCRIPTIONS%>"
                              placeholder="Description"></textarea>
                </div>

                <div class="col-md-1 checkbox">
                    <input class="iIsRequiredHidden" type="hidden" value="off"
                           name="<%=SaveJSONServlet.KEY_IS_REQUIRED%>">
                    <label><input class="iIsRequired" type="checkbox" value="on"
                                  name="<%=SaveJSONServlet.KEY_IS_REQUIRED%>">Required</label>
                </div>

                <div class="col-md-1">
                    <a class="btn aCloseParam btn-danger pull-right"> <b>&times;</b> </a>
                </div>

            </div>
        </div>

        <%--Add new route panel--%>
        <div class="col-md-10">
            <label for="route">Route</label>
            <input class="form-control" type="text" maxlength="50" id="route" placeholder="Route"><br>
            <label for="fParam">Param</label> <a id="aAddReqParam"> (Add param)</a>

            <form id="fParam" class="fParam">

            </form>

            <label for="external_api_url">External API URL</label>
            <input class="form-control" type="text" id="external_api_url" placeholder="External API URL"><br>


            <div class="row">


                <div class="col-md-4">

                    <label for="delay">Delay (in seconds)</label>
                    <input class="form-control" type="number" placeholder="Delay" id="delay"/>


                </div>

                <div class="col-md-8">
                    <label for="description">Description</label>
                    <textarea class="form-control" type="text" placeholder="Description" id="description"></textarea>
                </div>
            </div>

            <br>
            <%--checkboxes--%>
            <div class="row">

                <div class="col-md-4">
                    <label class="checkbox-inline"><input type="checkbox" id="is_secure">Authorization</label>
                </div>

            </div>

            <br>

            <div id="resultDiv" style="display: none" class="alert">
            </div>

            <br>


            <div class="row">

                <div class="col-md-5" id="response_panel" style="visibility: hidden">

                    <div class="pull-left">
                        <form class="form-inline">
                            <div class="form-group">
                                <select id="responses" class="form-control">
                                    <option value="<%=Routes.COLUMN_DEFAULT_RESPONSE%>">Default response</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <a id="aAddResponse" class="btn btn-default"><span
                                        class="glyphicon glyphicon-floppy-disk"></span></a>
                            </div>

                            <div class="form-group">
                                <a id="aDeleteResponse" class="btn btn-default"><span
                                        class="glyphicon glyphicon-trash"></span></a>
                            </div>
                        </form>
                    </div>


                </div>

                <div class="col-md-7">

                    <div class="pull-right">

                        <a target="_blank" id="aParamResp" href="param_resp.jsp" style="display: none"
                           class="btn btn-info btn-sm"><span
                                class="glyphicon glyphicon-th-list"></span> PARAM-RESP
                        </a>

                        <button id="bDelete" style="display: none" class="btn btn-danger btn-sm"><span
                                class="glyphicon glyphicon-trash"></span> DELETE
                        </button>


                        <button class="btn btn-sm btn-info" data-toggle="modal" data-target="#shortcuts">SHORTCUTS
                        </button>
                        <button id="bClear" class="btn btn-info  btn-sm"><span class="glyphicon glyphicon-flash"></span>
                            CLEAR
                        </button>
                        <button id="bSubmit" class="btn btn-primary  btn-sm"><span
                                class="glyphicon glyphicon-save"></span>
                            SAVE
                        </button>
                    </div>

                </div>


            </div>

            <div class="row text-center">
                <%
                    for (final RandomResponseGenerator.RandomResponse randomResponse : RandomResponseGenerator.randomResponses) {
                %>
                <span class="randomItems label label-primary"><%=randomResponse.getKey()%></span>
                <%
                    }
                %>
            </div>


            <br>


            <textarea class="form-control" id="response" name="response"
                      placeholder="Response" title="JSON"></textarea>
            <br>


        </div>
    </div>

    <div class="modal fade" id="search_result" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title" id="search_result_title"></h4>
                </div>
                <div class="modal-body" id="search_result_content">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <%--Random panel--%>
    <div class="modal fade" id="random" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">I need random</h4>
                </div>
                <div class="modal-body">
                    <form id="fRandom">

                        <p>Tap enter to choose</p>

                        <div class="radio">
                            <label><input id="iWords" type="radio" value="words" name="random_what" checked autofocus>Random
                                words</label>
                        </div>

                        <div class="radio">
                            <label><input type="radio" value="paragraphs" name="random_what">Paragraphs</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="name" name="random_what">Name</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="phone" name="random_what">Phone</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="city" name="random_what">City</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="state" name="random_what">State</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="country" name="random_what">Country</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="male_name" name="random_what">Male name</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="female_name" name="random_what">Female name</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="first_name" name="random_what">First name</label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" value="last_name" name="random_what">Last name</label>
                        </div>
                        <input type="submit" style="display: none">
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <%----%>
    <div class="modal fade" id="shortcuts" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Shortcuts</h4>
                </div>
                <div class="modal-body">
                    <p><code>Control + Alt + R </code>To generate random text</p>
                    <p><code>Control + Alt + L </code>To format <code>JSON</code></p>
                    <p><code>Control + Alt + M </code>To create Java model object from selected <code>JSON</code> object
                    </p>
                    <p><code>Control + Alt + N </code>To get default success response</p>
                    <p><code>Control + Alt + E </code>To get default error response</p>
                    <p><code>Control + Alt + D </code>To duplicate selection (with numerical increment)</p>
                    <p><code>Control + Alt + S </code>To insert a string object at selected position</p>
                    <p><code>Control + Alt + I </code>To insert random image urls at selected position</p>
                    <p><code>F1 </code>To search for a route</p>
                    <p><code>F4 </code>To generate API interface method</p>
                    <p><code>F7 </code>To save</p>
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
