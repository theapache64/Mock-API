<%--suppress HtmlFormInputWithoutLabel --%>
<%--suppress JSDuplicatedDeclaration --%>
<%--suppress ALL --%>
<%@ page import="com.theah64.mock_api.database.Images" %>
<%@ page import="com.theah64.mock_api.database.Preferences" %>
<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.models.Image" %>
<%@ page import="com.theah64.mock_api.models.Preference" %>
<%@ page import="com.theah64.mock_api.models.Route" %>
<%@ page import="com.theah64.mock_api.servlets.SaveJSONServlet" %>
<%@ page import="com.theah64.mock_api.servlets.UploadImageServlet" %>
<%@ page import="com.theah64.mock_api.utils.RandomResponseGenerator" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Calendar" %>
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

    <%
        try {

            final Preference preference = Preferences.getInstance().get();

            if (!preference.isOnline()) {
                StatusResponse.redirect(response, "Maintenance Mode");
                return;
            }
        } catch (QueryBuilderException | SQLException e) {
            e.printStackTrace();
        }
    %>

    <script>

        function setLastModified(message, date) {
            $("p#pLastModified").html("Last modified: <b>" + message + "</b>");
            $("p#pLastModified").attr("title", date);
        }

        var isAlertResult = false;
        var isAutoUpload = false;
        $(document).ready(function () {


            //Control + Alt + R
            function showRandomGenModal() {
                $("#random").modal("show");
                setTimeout(function () {
                    $('input#iWords').focus();
                }, 500);
            }


            $("a#aGenRandomText").on('click', function () {
                showRandomGenModal();
            });


            var dAllProgress = $("div#dAllProgress");
            dAllProgress.hide();

            function formatJSON() {
                var start_cursor = editor.getCursor();  //I need to get the cursor position
                console.log(start_cursor);  //Cursor position
                var cursorLine = start_cursor.line;
                var cursorCh = start_cursor.ch;

                editor.getDoc().setValue(JSON.stringify(JSON.parse(editor.getDoc().getValue()), undefined, 4));
                editor.focus();


                //Code to move cursor back [x] amount of spaces. [x] is the data-val value.
                editor.setCursor({line: cursorLine, ch: cursorCh});
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


            <%
            if(project.isAllSmallRoutes()){
                %>
            $("body").on('keyup', "input#route, input.iNames", function () {
                if (!event.ctrlKey && !event.altKey) {
                    var oldVal = $(this).val();
                    var newVal = $.trim(oldVal.toLowerCase().replace(/(\s+)/g, '_'));
                    $(this).val(newVal);
                }
            });


            <%
        }
        %>


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

            function findRoute() {

                var routeToSearch = prompt("Search route");
                console.log("x:" + routeToSearch);
                $("select#routes option").filter(function () {
                    console.log("text: " + this.text + ":" + routeToSearch);
                    return this.text === routeToSearch || this.text.indexOf(routeToSearch) > 0;
                }).attr('selected', true).trigger("change");
            }

            //Global shortcut listener
            function genApiInterfaceMethod() {

                var selIndex = $("select#routes").prop('selectedIndex');

                if (selIndex == 0) {
                    alert("Please select a route first");
                } else {
                    var route = $.trim($("select#routes option:selected").text());
                    window.open('v1/get_api_interface_method?name=' + route + "&project_name=<%=project.getName()%>");
                }
            }

            function genApiCall() {

                var selIndex = $("select#routes").prop('selectedIndex');
                if (selIndex == 0) {
                    alert("Please select a route first");
                } else {
                    var route = $.trim($("select#routes option:selected").text());
                    window.open('v1/get_api_call?name=' + route + "&project_name=<%=project.getName()%>");
                }

            }

            function addParams() {

                var params = prompt("Type params comma sep", "param1,param2");
                var paramArr = params.split(",");
                for (var i = 0; i < paramArr.length; i++) {
                    var paramRow = $("div#dParamRow");
                    var oldVal = paramArr[i];
                    var newVal = $.trim(oldVal.toLowerCase().replace(/(\s+)/g, '_'));
                    $(paramRow).find("input.iNames").attr('value', newVal);
                    $("form#fParam").append(paramRow.html());
                }

            }

            function genActCode() {

                var selIndex = $("select#routes").prop('selectedIndex');
                if (selIndex == 0) {
                    alert("Please select a route first");
                } else {
                    var route = $.trim($("select#routes option:selected").text());
                    window.open('gen_activity_code.jsp?route_name=' + route + "&project_name=<%=project.getName()%>");
                }
            }

            $(window).keydown(function (event) {

                //F2
                if (event.keyCode === 113) {
                    genActCode();
                }

                //F10
                if (event.keyCode === 121) {
                    addParams();
                }

                //F7
                if (event.keyCode === 118) {
                    $("button#bSubmit").click();
                }

                if (event.ctrlKey && event.altKey && event.keyCode === 70) {
                    findInDefaultResponse();
                }


                if (event.keyCode === 112) {
                    findRoute();
                }


                //F4
                if (event.keyCode === 115) {
                    genApiInterfaceMethod();
                }

                //F8
                if (event.keyCode === 119) {

                    genApiCall();
                }

            });

            var dProgress = $("div#dProgress");
            var dProgressChild = $("div#dProgressChild");

            $("button#bNice").on('click', function () {
                var imageUrl = $(this).parent().parent().find("img").attr('src');
                editor.replaceSelection('"' + imageUrl + '"');
            });

            function findInDefaultResponse() {
                var sValue = prompt("Search for response");
                if (sValue === null || sValue === "") {
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
                                    return this.text === routeName;
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

            //Editor shortcuts
            function insertGoogleImages() {

                var keyword = prompt("Insert random image, enter keyword");
                console.log(keyword);
                if (keyword !== null) {

                    $.ajax({
                        type: "POST",
                        beforeSend: function () {
                            startLoading(false);
                            dProgressChild.text("Searching for '" + keyword + "' image");
                            dProgress.slideDown(100);
                        },
                        data: {
                            keyword: keyword
                        },
                        url: "v1/search_images",
                        success: function (data) {
                            stopLoading(false);
                            dProgress.slideUp(200);

                            if (!data.error) {
                                var entry = data.data.images[Math.floor(Math.random() * data.data.images.length)];

                                var imageViewer = $("#image_viewer");
                                imageViewer
                                    .find("img")
                                    .attr('src', entry.image_url);

                                imageViewer.modal("show");

                            } else {
                                alert(data.message);
                            }
                        },
                        error: function (e) {
                            dProgress.slideUp(200);
                            stopLoading(false);
                            alert("Network error occurred, please check your connection");
                        }
                    });

                }

            }

            function uploadImage() {
                isAutoUpload = true;
                $("input#iFile").trigger('click');
            }

            function insertPicsumImages() {
                var dimen = prompt("Enter dimension", "500x500");
                dimen = dimen.split("x");
                if (dimen.length === 2) {

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

            function generatePOJO() {

                var selection = editor.getSelection();

                if (selection.trim().length > 0) {

                    var isRetrofitModel = confirm("Is this a retrofit model?");

                    $("input#iJoString").val(selection);
                    $("input#iIsRetrofitModel").val(isRetrofitModel);
                    $("input#iRouteName").val($("input#route").val());
                    $("form#fJsonToModel").submit();

                } else {
                    alert("No JSON text selected!");
                }
            }

            function insertSuccessResponse() {
                var successResponse = '<%=project.getDefaultSuccessResponse().replaceAll("[\r\n]+", " ")%>';

                if (successResponse.indexOf("SUCCESS_MESSAGE") !== -1) {
                    var successMsg = prompt("Success message", "This is a sample success message");
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(successResponse.replace('SUCCESS_MESSAGE', successMsg)), undefined, 4));
                } else {
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(successResponse), undefined, 4));
                }
            }

            function insertErrorResponse() {

                var errorResponse = '<%=project.getDefaultErrorResponse().replaceAll("[\r\n]+", " ")%>';

                if (errorResponse.indexOf("ERROR_MESSAGE") != -1) {
                    var errorMsg = prompt("Error message", "This is a sample error message");
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(errorResponse.replace('ERROR_MESSAGE', errorMsg)), undefined, 4));
                } else {
                    editor.getDoc().setValue(JSON.stringify(JSON.parse(errorResponse), undefined, 4));
                }

            }

            function insertKeyValue() {

                var key = prompt("Key for the object");
                if (key) {
                    var value = prompt("Value for " + key);
                    //check if the data int or not
                    editor.replaceSelection('"' + key + '":' + (isNaN(value) ? '"' + value + '"' : value));
                } else {
                    alert("Can't accept empty key");
                }

            }

            function generateDuplicate() {

                var selection = editor.getSelection();

                if (selection.length > 0) {

                    var n = prompt("Number of nodes? ", 1);

                    if (n) {
                        var builder = "";
                        var temp = selection;

                        var isIntFound = false;

                        for (var i = 1; i < n; i++) {
                            temp = temp.replace(/\.*\((\d+)\)\.*/g, function (fullMatch, n) {
                                isIntFound = true;
                                return "(" + (Number(n) + 1) + ")";
                            });
                            builder += temp + "\n";
                        }


                        if (isIntFound) {
                            selection = selection.replace(/\(/g, "");
                            selection = selection.replace(/\)/g, "");

                            builder = builder.replace(/\(/g, "");
                            builder = builder.replace(/\)/g, "");
                        }

                        editor.replaceSelection(selection + "\n" + builder);
                    }
                } else {
                    alert("Please select some text");
                }

            }

            editor.on('keyup', function () {

                console.log(event.keyCode);

                //Control + Alt+  O
                if (event.keyCode === 120) {
                    insertGoogleImages();
                }


                //Control + Alt + U
                if (event.ctrlKey && event.altKey && event.keyCode === 85) {
                    uploadImage();
                }

                //Control + Alt + I
                if (event.ctrlKey && event.altKey && event.keyCode === 73) {
                    insertPicsumImages();
                }


                if (event.ctrlKey && event.altKey && event.keyCode === 82) {
                    showRandomGenModal();
                }


                //Route to java model


                //Control + Alt + M
                if (event.ctrlKey && event.altKey && event.keyCode === 77) {
                    generatePOJO();
                }

                if (event.ctrlKey && event.altKey && event.keyCode === 76) {
                    formatJSON();
                }


                if (event.ctrlKey && event.altKey && event.keyCode === 78) {
                    insertSuccessResponse();
                }


                if (event.ctrlKey && event.altKey && event.keyCode === 83) {
                    insertKeyValue();
                }


                if (event.ctrlKey && event.altKey && event.keyCode === 69) {
                    insertErrorResponse();
                }

                //D
                if (event.ctrlKey && event.altKey && event.keyCode === 68) {
                    generateDuplicate();
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

                if (selIndex !== 0) {

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

                                $("select#<%=Routes.COLUMN_METHOD%>").val(data.data.method);

                                //Looping through required params
                                var isParamsAdded = false;
                                $.each(data.data.params, function (i, item) {

                                    isParamsAdded = true;


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
                                    $("input#" + iIsRequiredHiddenId).val(item.is_required);


                                    //Setting names
                                });

                                if (!isParamsAdded) {
                                    var paramRow = $("div#dParamRow").html();
                                    $("form#fParam").append(paramRow);
                                }

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
                                $("form#fParam").html("");
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
                window.location = "index.jsp?api_key=<%=project.getApiKey()%>";
            });

            var resultDiv = $("div#resultDiv");

            $("form#fParam").on('change', 'input.iIsRequired', function () {
                $(this).parent().parent().find("input.iIsRequiredHidden").val($(this).is(":checked"));
            });

            $("button#bSubmit").on('click', function () {


                resultDiv.hide();
                var route = $("input#route").val();
                var response = editor.getDoc().getValue();
                var params = $("form#fParam").serialize();
                console.log("Params:" + params);
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
                    "&name=" + encodeURIComponent(route) +
                    "&response_id=" + encodeURIComponent($('select#responses :selected').val()) +
                    "&response=" + encodeURIComponent(response) +
                    "&optional_params=" + encodeURIComponent(opParams) +
                    "&external_api_url=" + encodeURIComponent(external_api_url) +
                    "&is_secure=" + encodeURIComponent(isSecure) +
                    "&delay=" + encodeURIComponent(delay) +
                    "&method=" + encodeURIComponent($("select#<%=Routes.COLUMN_METHOD%>").val()) +
                    "&description=" + encodeURIComponent(description),
                    success: function (data) {
                        stopLoading(true);
                        console.log(data);

                        setLastModified("Just now", "Just now");

                        if (!data.error) {

                            $(resultDiv).removeClass('alert-danger').addClass('alert-success');
                            var link = "<a target='blank' href='get_json/<%=project.getName()%>/" + route + "?" + data.data.dummy_params + "'>/" + route + "</a>";
                            $(resultDiv).html("<strong>Success! </strong> " + data.message + ": " + link);
                            $(resultDiv).show();

                            console.log(data.data);
                            preSelRespId = $('select#responses :selected').val();
                            if ('id' in data.data) {

                                //Adding added route to select list
                                $("select#routes option:eq(0)").after("<option value=" + data.data.id + ">" + route + " </option>");

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
                dAllProgress.show();
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
                dAllProgress.slideUp(500);
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

            var paramRow = $("div#dParamRow").html();
            $("form#fParam").append(paramRow);

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
                    url: "v1/delete_route",
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


            var selectedRoute = "<%=request.getParameter("route")%>";

            if (selectedRoute !== "null") {
                $("select#routes option").filter(function () {
                    return this.text === selectedRoute;
                }).attr('selected', true).trigger("change");
            }


            $("button#bGenRandomFromModel").on('click', function () {
                $("#fRandom").submit();
            });

            $("#fRandom").submit(function (e) {
                e.preventDefault();

                $("#random").modal("hide");


                var randomWhat = $(this).serializeArray()[0].value;
                var count = 1;
                if (randomWhat === 'words' || randomWhat === 'paragraphs') {
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


            $("#fParam").on('click', 'a.aAddParam', function () {
                var paramRow = $("div#dParamRow").html();

                $(paramRow).insertAfter($(this).parent().parent());
            });

            $("#fParam").on('click', 'a.aCloseParam', function () {
                var childCount = $("form#fParam > div").length;
                if (childCount > 1) {
                    $(this).parent().parent().remove();
                }
            });


            $("div#dGallery").on('mouseenter', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bDelete").fadeIn(100);
            }).on('mouseleave', 'div.dGalleryRow1', function () {
                //do
                $(this).find("button.bDelete").fadeOut(100);
            });

            $("div#dGallery").on('click', 'button.bDelete', function (e) {

                e.stopPropagation();

                var image = $(this).siblings("img");
                var id = image.attr('id');

                $.ajax({
                    type: "POST",
                    beforeSend: function () {

                    },
                    headers: {
                        Authorization: "<%=project.getApiKey()%>"
                    },
                    data: {
                        id: id
                    },
                    url: "v1/delete_image",
                    success: function (data) {

                        if (!data.error) {
                            image.parent().remove();
                        } else {
                            alert(data.message);
                        }
                    },
                    error: function (e) {
                        alert("Network error occurred, please check your connection");
                    }
                });

            });

            $("form#fInsertImage").on('submit', function (e) {

                var dInsertImageProgressContainer = $("div#dInsertImageProgressContainer");
                var dInsertImageProgress = $("div#dInsertImageProgress");

                dInsertImageProgressContainer.show();


                e.preventDefault();
                var formData = new FormData(this);

                $.ajax({
                    type: 'POST',
                    url: "v1/upload_image",
                    headers: {"Authorization": "<%=project.getApiKey()%>"},
                    data: formData,
                    cache: false,
                    contentType: false,
                    processData: false,

                    beforeSend: function () {
                        $(dInsertImageProgress)
                            .attr('aria-valuenow', '100')
                            .css('width', '100%')
                            .text("Initializing upload...");

                        startLoading(false);
                        $(resultDiv).addClass('alert-success').removeClass('alert-danger');
                        $(resultDiv).html("<strong>Uploading: </strong> Initializing upload...");
                        $(resultDiv).show();
                    },

                    xhr: function () {
                        var xhr = new window.XMLHttpRequest();
                        xhr.upload.addEventListener("progress", function (evt) {

                            if (evt.lengthComputable) {
                                var percentComplete = parseInt((evt.loaded / evt.total) * 100);
                                //Do something with upload progress here

                                console.log(percentComplete);


                                $(dInsertImageProgress)
                                    .attr('aria-valuenow', percentComplete)
                                    .css('width', percentComplete + '%')
                                    .text(percentComplete + "%");


                                $(resultDiv).html("<strong>Uploading: </strong> " + percentComplete + "%");

                                if (percentComplete === 100) {
                                    $(dInsertImageProgress).text("Processing image...");
                                    $(resultDiv).html("<strong>Processing image...</strong> ");
                                }
                            }
                        }, false);

                        return xhr;
                    },

                    success: function (data) {
                        console.log("success");
                        console.log(data);

                        stopLoading(false);

                        dInsertImageProgressContainer.hide();

                        if (!data.error) {

                            var galleryRow = $("div#dGalleryRow");
                            $(galleryRow)
                                .find('img')
                                .attr('id', data.data.id)
                                .attr('data-image-url', data.data.image_url)
                                .attr('data-thumb-url', data.data.image_url)
                                .attr('src', data.data.image_url);

                            $("div#dGallery").prepend(galleryRow.html());

                            if (isAutoUpload) {
                                editor.replaceSelection('"' + data.data.image_url + '"');
                                editor.focus();
                            }

                            $(resultDiv).html("<strong>Success!! </strong> <a target='_blank' href='" + data.data.image_url + "'>image</a> uploaded");

                        } else {
                            $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                            $(resultDiv).html("<strong>Error! </strong> " + data.message);
                            $(resultDiv).show();
                            alert(data.message);
                        }

                    },
                    error: function (data) {
                        console.log("error");
                        console.log(data);
                        if (!isAutoUpload) {
                            alert("Network error, please check your connection");
                        }
                        $(resultDiv).addClass('alert-danger').removeClass('alert-success');
                        $(resultDiv).html("<strong>Error! </strong> Please check your connection");
                        $(resultDiv).show();
                    }
                });


            });

            $("button#bUploadNewImage").on('click', function () {
                isAutoUpload = false;
                $("input#iFile").trigger('click');
            });

            $("input#iFile").on('change', function () {
                var filePath = $(this).val();
                if (filePath !== "") {
                    $("form#fInsertImage").submit();
                }
            });

            var dGallery = $("div#dGallery");
            dGallery.on('click', '.dGalleryRow', function () {
                var imageUrl = $(this).data("image-url");
                editor.replaceSelection('"' + imageUrl + '"');
                editor.focus();
                $("div#dInsertImage").modal("hide");
            });

            $("div.dGalleryRow").hover(function (e) {
                $(this).find("button.bDeleteImage").fadeIn("300");
            }, function () {
                $(this).find("button.bDeleteImage").fadeOut("300");
            });

            dGallery.on('click', "button.bDeleteImage", function (e) {
                alert("delete");
                e.stopPropagation();
            });

            function addParamsFromURL() {

                var url = prompt("Enter your URL", "http://google.com/login?username=myUsername&password=1234");
                //http://google.com/login?username=myUsername&password=1234
                var params = url.substr(url.indexOf("?") + 1);
                var keyValues = params.split("&");
                $.each(keyValues, function (key, val) {

                    var paramName = val.split("=")[0];

                    var paramRow = $("div#dParamRow");
                    console.log(paramName);
                    $(paramRow).find("input.iNames").attr('value', paramName);
                    $("form#fParam").append(paramRow.html());

                });

            }

            $("nav.navbar a").on('click', function () {

                var clickedAnchorId = $(this).attr('id');

                switch (clickedAnchorId) {

                    case "aFindInDefRes":
                        findInDefaultResponse();
                        break;

                    case "aParamFromURL":
                        addParamsFromURL();
                        break;

                    case "aFindRoute":
                        findRoute();
                        break;

                    case "aSaveRoute":
                        $("button#bSubmit").click();
                        break;

                    case "aGenRandomText":
                        showRandomGenModal();
                        break;

                    case "aDefSucResp":
                        insertSuccessResponse();
                        break;

                    case "aDefErrResp":
                        insertErrorResponse();
                        break;

                    case "aDuplicate":
                        generateDuplicate();
                        break;

                    case "aJsonKeyValue":
                        insertKeyValue();
                        break;

                    case "aRandomImageURL":
                        insertPicsumImages();
                        break;
                    case "aAddParams":
                        addParams();
                        break;

                    case "aFormatResponse":
                        formatJSON();
                        break;
                    case "aPojo":
                        generatePOJO();
                        break;

                    case "aAPIInterfaceMethod":
                        genApiInterfaceMethod();
                        break;

                    case "aAPICall":
                        genApiCall();
                        break;

                    case "aUploadImage":
                        uploadImage();
                        break;

                }
            });


            //$("#image_viewer").modal("show");

        });
    </script>
    <style>


        .menu_shortcut {
            float: right;
        }

        .randomItems {
            cursor: pointer;
        }

        .randomItems:hover {
            background-color: #053d76;
        }

        div#dGallery div {
            margin-bottom: 10px;
        }

        img#xmas {
            -webkit-transform: scaleX(-1);
            transform: scaleX(-1);
            position: absolute;
            width: 56px;
            top: 7px;
            right: -13px;
        }

        ul.dropdown-menu li {
            width: 304px;
        }

        #iNotificationEmails_tagsinput {
            width: 100%;
            min-height: 100px;
            border-radius: 3px;
        }

        #iNotificationEmails_tag {
            width: 100% !important;
        }

    </style>
</head>
<body>

<%
    List<Route> routes = null;
    try {
        routes = Routes.getInstance().getAll(project.getId());
    } catch (SQLException e) {
        e.printStackTrace();
    }

    request.setAttribute("is_home_page", true);
%>

<%@include file="nav_bar.jsp" %>


<div class="container">

    <%--Hidden form--%>
    <%--$.ajax({
                            type: "POST",
                            beforeSend: function () {
                                startLoading(true);
                            },
                            headers: {
                                "Authorization": '<%=project.getApiKey()%>'
                            },
                            data: {
                                jo_string: selection,
                                is_retrofit_model: isRetrofitModel,
                                route_name: $("input#route").val()
                            },
                            url: "json_to_model_engine.jsp",--%>

    <form id="fJsonToModel" action="json_to_model_engine.jsp" style="display: none" target="_blank" method="POST">
        <input id="iAuthorization" name="Authorization" value="<%=project.getApiKey()%>"/>
        <input id="iJoString" name="jo_string"/>
        <input id="iIsRetrofitModel" name="is_retrofit_model"/>
        <input id="iRouteName" name="route_name"/>
        <input value="X" name="<%=Form.KEY_IS_SUBMITTED%>"/>
    </form>


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
                                max-height: 500px;"/>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button id="bNice" type="button" class="btn btn-success" data-dismiss="modal">Nice!</button>
                </div>

            </div>


        </div>


    </div>


    <br>


    <p id="pLastModified" title="" class="pull-right"></p>

    <div class="row">
        <div class="col-md-12">
            <div id="dAllProgress" class="progress">
                <div class="progress-bar progress-bar-striped active" role="progressbar"
                     aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%">
                    Please wait...
                </div>
            </div>
        </div>
    </div>


    <div class="row">

        <br><br>


        <%--Add new route panel--%>
        <div class="col-md-12">

            <div class="row">

                <%--Available routes--%>
                <div class=" col-md-2">


                    <label for="routes">Select route</label>
                    <div>
                        <%
                            if (Calendar.getInstance().get(Calendar.MONTH) == 11) {
                        %>
                        <img id="xmas" class="pull-right" src="assets/xmas_hat.png"/>
                        <%
                            }
                        %>

                        <select id="routes" class="form-control" title="Routes">
                            <option value="">Select a route</option>
                            <%
                                if (routes != null) {
                                    for (final Route route : routes) {
                            %>
                            <option value="<%=route.getId()%>"><%=route.getName()%>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>


                </div>

                <div class="col-md-2">
                    <label for="<%=Routes.COLUMN_METHOD%>">Method</label>
                    <%--<input class="form-control" type="text" maxlength="50" id="route" placeholder="Route">--%>
                    <select id="<%=Routes.COLUMN_METHOD%>" class="form-control">
                        <option value="GET">GET</option>
                        <option value="POST">POST</option>
                        <option value="PUT">PUT</option>
                        <option value="PATCH">PATCH</option>
                        <option value="DELETE">DELETE</option>
                    </select>
                </div>

                <div class="col-md-6">
                    <label for="route">Route</label>
                    <input class="form-control" type="text" maxlength="50" id="route" placeholder="Route">
                </div>

                <div class="col-md-2" style="margin-top: 30px;">
                    <label class="checkbox-inline"><input type="checkbox" id="is_secure">Authorization</label>
                </div>

            </div>

            <br>
            <label for="fParam">Param</label>

            <form id="fParam" class="fParam">

            </form>

            <div class="row">
                <div class="col-md-8">
                    <label for="external_api_url">External API URL</label>
                    <input class="form-control" type="text" id="external_api_url" placeholder="External API URL"><br>
                </div>
                <div class="col-md-4">

                    <label for="delay">Delay (in seconds)</label>
                    <input class="form-control" type="number" placeholder="Delay" id="delay"/>


                </div>
            </div>


            <div class="row">

                <div class="col-md-12">
                    <label for="description">Description</label>
                    <textarea class="form-control" placeholder="Description" id="description"></textarea>
                </div>
            </div>

            <br>

            <br>

            <div id="resultDiv" style="display: none" class="alert">
            </div>

            <br>


            <div class="row">

                <div class="col-md-4" id="response_panel" style="visibility: hidden">

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

                <div class="col-md-8">

                    <div class="pull-right">

                        <a target="_blank" id="aParamResp" href="param_resp.jsp" style="display: none"
                           class="btn btn-info btn-sm"><span
                                class="glyphicon glyphicon-th-list"></span> PARAM-RESP
                        </a>

                        <button id="bDelete" style="display: none" class="btn btn-danger btn-sm"><span
                                class="glyphicon glyphicon-trash"></span> DELETE
                        </button>

                        <button data-toggle="modal" data-target="#dInsertImage" id="bInsertImage"
                                class="btn btn-default  btn-sm"><span
                                class="glyphicon glyphicon-picture"></span>
                            Insert Image
                        </button>

                        <button id="bClear" class="btn btn-default  btn-sm"><span
                                class="glyphicon glyphicon-flash"></span>
                            Clear
                        </button>
                        <button id="bSubmit" class="btn btn-primary  btn-sm"><span
                                class="glyphicon glyphicon-save"></span>
                            Save
                        </button>
                    </div>

                </div>


            </div>

            <div class="row">
                <div class="col-md-12">
                    <div id="dProgress" style="display: none" class="progress">
                        <div id="dProgressChild" class="progress-bar progress-bar-striped progress-bar-active active"
                             role="progressbar"
                             aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%">

                        </div>
                    </div>
                </div>
            </div>


            <div class="row text-center" style="line-height: 3">
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

    <%--Param row--%>
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
                    <option value="File">File</option>
                </select>
            </div>

            <div class="col-md-2">
                <input class="iDefaultValues form-control" name="<%=SaveJSONServlet.KEY_DEFAULT_VALUES%>"
                       type="text" placeholder="Default value"><br>
            </div>

            <div class="col-md-3">
                    <textarea class="taDescriptions form-control" name="<%=SaveJSONServlet.KEY_DESCRIPTIONS%>"
                              placeholder="Description"></textarea>
            </div>

            <div class="col-md-1 checkbox">
                <input class="iIsRequiredHidden" type="hidden"
                       name="<%=SaveJSONServlet.KEY_IS_REQUIRED%>" value="true">
                <label><input class="iIsRequired" type="checkbox" checked>Required</label>
            </div>

            <div class="col-md-2">
                <a class="btn aCloseParam btn-danger pull-right"> <b>&times;</b> </a>
                <a class="btn aAddParam btn-primary pull-right" style="margin-right: 5px"> <b>&plus;</b> </a>
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
                    <button id="bGenRandomFromModel" type="button" class="btn btn-primary" data-dismiss="modal">
                        Generate
                    </button>
                </div>
            </div>
        </div>
    </div>

    <%--Shortcuts--%>
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
                    <p><code>Control + Alt + F </code>To search in default response</p>
                    <p><code>F1 </code>To search for a route</p>
                    <p><code>F4 </code>To generate API interface method</p>
                    <p><code>F7 </code>To save</p>
                    <p><code>F9 </code>Search and insert random image</p>
                    <p><code>F10 </code>Legacy param adding</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>


    <%--Inset image--%>
    <div class="modal fade" id="dInsertImage" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="btn close btn-default pull-right" data-dismiss="modal"
                            style="margin-left: 10px;margin-top: 5px"> &times;
                    </button>
                    <button id="bUploadNewImage" class="btn btn-success pull-right" type="button"> Upload new &plus;

                    </button>


                    <h4 class="modal-title">Insert Image
                        <small id="sInsertImageProgress"></small>
                    </h4>
                </div>
                <div class="modal-body">

                    <div id="dInsertImageProgressContainer" style="display: none" class="progress">
                        <div id="dInsertImageProgress"
                             class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar"
                             aria-valuenow="100" aria-valuemin="0" aria-valuemax="0" style="width:0%">
                        </div>
                    </div>

                    <form id="fInsertImage" style="width: 0px;height: 0px;overflow: hidden">
                        <input id="iFile" type="file" accept="image/*" name="<%=UploadImageServlet.KEY_IMAGE%>"
                               required/>
                        <input type="submit" value="Upload"/>
                    </form>


                    <div id="dGallery" style="max-height: 400px; overflow-y: auto;overflow-x: hidden">


                        <%

                            try {
                                List<Image> images = Images.getInstance().getAll(Images.COLUMN_PROJECT_ID, project.getId());

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
                <div class="modal-footer">

                </div>
            </div>
        </div>
    </div>

</div>

<div id="dGalleryRow" style="display: none">
    <div class="col-md-2 dGalleryRow1">
        <img class="center-cropped dGalleryRow"
             id
             data-image-url
             data-thumb-url
             src>

        <button
                class="pull-right bDelete"><span style="color: white"
                                                 class="glyphicon glyphicon-remove"></span>
        </button>
    </div>
</div>

</body>
</html>
