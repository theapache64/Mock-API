<%@ page import="com.theah64.mock_api.database.Routes" %>
<%@ page import="com.theah64.mock_api.servlets.AdvancedBaseServlet" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.CommonUtils" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="java.sql.SQLException" %>
<%--suppress JSDuplicatedDeclaration --%>
<%--suppress ALL --%>
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
    <script src="tags/jquery.tagsinput.js"></script>
    <link rel="stylesheet" href="tags/jquery.tagsinput.css"/>
    <script src="//codemirror.net/mode/xml/xml.js"></script>
    <script>
        $(document).ready(function () {

            $('#iNotificationEmails').tagsInput({
                'placeholderColor': '#666666',
                'width': '100%',
                'defaultText': 'Add email '
            });


            var successEditor = CodeMirror.fromTextArea(document.getElementById("tDefaultSuccessResponse"), {
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
                    },
                    "Ctrl-Alt-L": function (cm) {
                        cm.getDoc().setValue(JSON.stringify(JSON.parse(cm.getDoc().getValue()), undefined, 4));
                    }
                },
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });

            successEditor.getDoc().setValue(JSON.stringify(JSON.parse(successEditor.getDoc().getValue()), undefined, 4));

            var errorEditor = CodeMirror.fromTextArea(document.getElementById("tDefaultErrorResponse"), {
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
                    },
                    "Ctrl-Alt-L": function (cm) {
                        cm.getDoc().setValue(JSON.stringify(JSON.parse(cm.getDoc().getValue()), undefined, 4));
                    }
                },
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });

            errorEditor.getDoc().setValue(JSON.stringify(JSON.parse(errorEditor.getDoc().getValue()), undefined, 4));

            var htmlEditor = CodeMirror.fromTextArea(document.getElementById("tBaseResponseStructure"), {
                lineNumbers: true,
                mode: "text/html",
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

            htmlEditor.on('keyup', function () {
                $("div#dBaseResponseStructureOutput").html(htmlEditor.getDoc().getValue());
            });

            $("div#dBaseResponseStructureOutput").html($("textarea#tBaseResponseStructure").text());

            $("form#fSettings").on('submit', function () {
                $("textarea#tDefaultSuccessResponse").val(successEditor.getDoc().getValue());
                $("textarea#tdefaulterrorresponse").val(errorEditor.getDoc().getValue());
                $("textarea#tBaseResponseStructure").val(htmlEditor.getDoc().getValue());
            });

        });
    </script>
    <style>
        #iNotificationEmails_tagsinput {
            width: 100%;
            min-height: 100px;
            border-radius: 3px;
        }

        #iNotificationEmails_tag {
            width: 100% !important;
        }

        .CodeMirror {
            height: 150px;
        }
    </style>
</head>


<body>
<%@include file="nav_bar.jsp" %>

<div class="container">

    <h2>Settings</h2>
    <br>

    <div class="col-md-12">

        <form id="fSettings" method="POST" action="settings.jsp?api_key=<%=project.getApiKey()%>" novalidate>

            <%
                final Form form = new Form(request, new String[]{
                        Projects.COLUMN_NAME,
                        Projects.COLUMN_PACKAGE_NAME,
                        Projects.COLUMN_BASE_OG_API_URL,
                        Projects.COLUMN_DEFAULT_SUCCESS_RESPONSE,
                        Projects.COLUMN_DEFAULT_ERROR_RESPONSE,
                        Projects.COLUMN_BASE_RESPONSE_STRUCTURE
                });
                try {
                    if (form.isSubmitted() && form.isAllRequiredParamsAvailable()) {


                        final String projectName = form.getString(Projects.COLUMN_NAME);
                        final String packageName = form.getString(Projects.COLUMN_PACKAGE_NAME);
                        final String baseOgAPIUrl = form.getString(Projects.COLUMN_BASE_OG_API_URL);
                        final boolean isAllSmallRoutes = form.getBoolean(Projects.COLUMN_IS_ALL_SMALL_ROUTES);
                        String emailNotifications = form.getString(Projects.COLUMN_NOTIFICATION_EMAILS);


                        if (!baseOgAPIUrl.matches(AdvancedBaseServlet.URL_REGEX)) {
                            throw new Request.RequestException("Invalid URL passed for base og API URL");
                        }

                        StringBuilder emailBuilder = new StringBuilder();
                        if (emailNotifications != null) {
                            final String[] emails = emailNotifications.split(",");

                            for (final String email : emails) {
                                if (email.matches(Form.EMAIL_REGEX)) {
                                    if (emailBuilder.indexOf(email) == -1) {
                                        emailBuilder
                                                .append(email)
                                                .append(",");
                                    }
                                } else {
                                    throw new Request.RequestException("Invalid email :" + email);
                                }
                            }
                        }

                        if (emailBuilder.length() == 0) {
                            emailBuilder = null;
                        }


                        final Projects projectsTable = Projects.getInstance();
                        final String oldBaseUrl = project.getBaseOgApiUrl();

                        String dsr = form.getString(Projects.COLUMN_DEFAULT_SUCCESS_RESPONSE);
                        String der = form.getString(Projects.COLUMN_DEFAULT_ERROR_RESPONSE);
                        String brs = form.getString(Projects.COLUMN_BASE_RESPONSE_STRUCTURE);

                        if (CommonUtils.isJSONValid(dsr) && CommonUtils.isJSONValid(der)) {

                            dsr = dsr.replaceAll("'([^\"]+)'", "\"$1\"");
                            der = der.replaceAll("'([^\"]+)'", "\"$1\"");

                            project.setDefaultSuccessResponse(dsr);
                            project.setDefaultErrorResponse(der);
                            project.setBaseResponseStructure(brs);

                            //Setting new values
                            project.setName(projectName);
                            project.setBaseOgApiUrl(baseOgAPIUrl);
                            project.setPackageName(packageName);
                            project.setAllSmallRoutes(isAllSmallRoutes);
                            project.setNotificationEmails(emailBuilder != null ? emailBuilder.substring(0, emailBuilder.length() - 1) : null);


                            projectsTable.update(project);
                            Routes.getInstance().updateBaseOGAPIURL(project.getId(), oldBaseUrl, baseOgAPIUrl);


            %>
            <div class="alert alert-success">
                <strong>Success!</strong> Settings saved.
            </div>

            <%
                    }

                }
            } catch (Request.RequestException | SQLException | QueryBuilderException e) {
                e.printStackTrace();
            %>
            <div class="alert alert-danger">
                <strong>Error!</strong> <%=e.getMessage()%>
            </div>

            <%
                }
            %>

            <div class="row">
                <div class="col-md-4">
                    <%--package name--%>
                    <div class="form-group">
                        <label for="iProjectName">Project Name</label>
                        <input id="iProjectName" class="form-control" value="<%=project.getName()%>"
                               name="<%=Projects.COLUMN_NAME%>" placeholder="Project name" required/>
                    </div>

                </div>
            </div>

            <div class="row">
                <div class="col-md-4">
                    <%--package name--%>
                    <div class="form-group">
                        <label for="iPackageName">Package Name</label>
                        <input id="iPackageName" class="form-control" value="<%=project.getPackageName()%>"
                               name="<%=Projects.COLUMN_PACKAGE_NAME%>" placeholder="Package name" required/>
                    </div>

                </div>


                <div class="col-md-4">
                    <div class="form-group">
                        <label for="iBaseOGAPIURL">Base OG API URL</label>
                        <input id="iBaseOGAPIURL" class="form-control" value="<%=project.getBaseOgApiUrl()%>"
                               name="<%=Projects.COLUMN_BASE_OG_API_URL%>" placeholder="Base OG API URL" required/>
                    </div>

                </div>

                <div class="col-md-4">

                    <br>

                    <div class="form-group">

                        <input type="checkbox" id="iAllSmallRoutes"
                               name="<%=Projects.COLUMN_IS_ALL_SMALL_ROUTES%>" <%=project.isAllSmallRoutes() ? "checked" : "" %>
                        />

                        <label for="iAllSmallRoutes">Strict Mode</label>
                    </div>
                </div>

            </div>


            <div class="form-group">
                <label for="tDefaultSuccessResponse">Default success response</label> <br>
                <textarea name="<%=Projects.COLUMN_DEFAULT_SUCCESS_RESPONSE%>" style="min-height: 100px"
                          id="tDefaultSuccessResponse"><%=project.getDefaultSuccessResponse()%></textarea>
            </div>


            <div class="form-group">
                <label for="tDefaultErrorResponse">Default error response</label> <br>
                <textarea name="<%=Projects.COLUMN_DEFAULT_ERROR_RESPONSE%>" style="min-height: 100px"
                          id="tDefaultErrorResponse"><%=project.getDefaultErrorResponse()%></textarea>
            </div>

            <div class="form-group">
                <label for="tBaseResponseStructure">Base response structure</label> <br>
                <textarea name="<%=Projects.COLUMN_BASE_RESPONSE_STRUCTURE%>" style="min-height: 100px"
                          id="tBaseResponseStructure"><%=project.getBaseResponseStructure()%></textarea>
            </div>

            <div class="row">
                <label for="dBaseResponseStructureOutput">&nbsp;&nbsp;&nbsp;&nbsp;Output</label>
                <div id="dBaseResponseStructureOutput" class="col-md-12">

                </div>
            </div>

            <%--Notification email--%>
            <div class="form-group">
                <label for="iNotificationEmails">Notification emails</label>
                <input id="iNotificationEmails" class="form-control"
                       value="<%=project.getNotificationEmails()!=null ? project.getNotificationEmails() : ""%>"
                       name="<%=Projects.COLUMN_NOTIFICATION_EMAILS%>"
                       placeholder="Notification emails (comma separated)" required/>
            </div>

            <input name="<%=Form.KEY_IS_SUBMITTED%>" class="pull-right btn btn-primary" type="submit" value="Save">
            <br>
            <br>

        </form>

    </div>

</div>

</body>
</html>
