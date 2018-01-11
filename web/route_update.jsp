<%@ page import="com.theah64.mock_api.database.RouteUpdates" %>
<%@ page import="com.theah64.mock_api.models.DiffView" %>
<%@ page import="com.theah64.mock_api.models.RouteUpdate" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 10/1/18
  Time: 10:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    final String key = request.getParameter("key");
    final String projectName = request.getParameter("project_name");
    final String routeName = request.getParameter("route_name");

    RouteUpdate newRouteUpdate = null;
    RouteUpdate oldRouteUpdate = null;
    try {
        newRouteUpdate = RouteUpdates.getInstance().get(RouteUpdates.COLUMN_KEY, key);
    } catch (QueryBuilderException | SQLException e) {
        e.printStackTrace();
        StatusResponse.redirect(response, "Error", e.getMessage());
    }
    if (newRouteUpdate == null) {
        StatusResponse.redirect(response, "Error", "Invalid route update key");
        return;
    }

    try {
        oldRouteUpdate = RouteUpdates.getInstance().getSecondLast(newRouteUpdate.getId(), RouteUpdates.COLUMN_ROUTE_ID, newRouteUpdate.getRouteId());
        System.out.println("New:" + newRouteUpdate);
        System.out.println("Old:" + oldRouteUpdate);
    } catch (QueryBuilderException | SQLException e) {
        e.printStackTrace();
    }

    if (oldRouteUpdate == null) {
        StatusResponse.redirect(response, "Error", "No history found, better luck next time ;)");
        return;
    }


%>
<html>
<head>
    <title>Route Update : <%=projectName + "/" + routeName%>
    </title>
    <%@include file="common_headers.jsp" %>
    <link rel="stylesheet" type="text/css" href="jsdifflib/diffview.css"/>
    <script type="text/javascript" src="jsdifflib/diffview.js"></script>
    <script type="text/javascript" src="jsdifflib/difflib.js"></script>
    <style>
        table.diff {
            width: 100%;
        }

        table.diff tbody th {
            width: 60px;
            background-color: #fcfcfc;
        }

        .author {
            display: none;
        }
    </style>


    <script type="text/javascript">

        $(document).ready(function () {

            <%
                final List<DiffView> diffViews= new  ArrayList<>();

                //Method
                if(!oldRouteUpdate.getMethod().equals(newRouteUpdate.getMethod())){
                    //Change in method
                           diffViews.add(new DiffView(
                            "Method","oldMethod",
                            "newMethod",
                            "methodDiffOuput",
                            newRouteUpdate.getMethod(),
                            oldRouteUpdate.getMethod()
                            ));
                }


                //Params
                if(!oldRouteUpdate.getParams().equals(newRouteUpdate.getParams())){

                    diffViews.add(new DiffView(
                            "Parameters","oldParams",
                            "newParams",
                            "paramsDiffOuput",
                            newRouteUpdate.getParams(),
                            oldRouteUpdate.getParams()
                            ));

                }


                //Delay
                if(!oldRouteUpdate.getDelay().equals(newRouteUpdate.getDelay())){

                    diffViews.add(new DiffView(
                            "Delay","oldDelay",
                            "newDelay",
                            "delayDiffOutput",
                            newRouteUpdate.getDelay(),
                            oldRouteUpdate.getDelay()
                            ));

                }

                //Descriptions
                 if(!oldRouteUpdate.getDescription().equals(newRouteUpdate.getDescription())){

                    diffViews.add(new DiffView(
                            "Description","oldDescription",
                            "newDescription",
                            "descriptionDiffOutput",
                            newRouteUpdate.getDescription(),
                            oldRouteUpdate.getDescription()
                            ));

                }


                //Default response
                if(!oldRouteUpdate.getDefaultResponse().equals(newRouteUpdate.getDefaultResponse())){
                    //Change in default response
                    diffViews.add(new DiffView(
                            "Default response","oldDefaultResponse",
                            "newDefaultResponse",
                            "defaultResponseDiffOutput",
                            newRouteUpdate.getDefaultResponse(),
                            oldRouteUpdate.getDefaultResponse()
                            ));
                }


                for(final DiffView diffView : diffViews){

                    %>
            //Default response
            base = difflib.stringAsLines($("#<%=diffView.getOldDataId()%>").val());
            newtxt = difflib.stringAsLines($("#<%=diffView.getNewDataId()%>").val());
            sm = new difflib.SequenceMatcher(base, newtxt);
            opcodes = sm.get_opcodes();
            diffoutputdiv = $("#<%=diffView.getDiffOutputId()%>");
            contextSize = "";

            diffoutputdiv.innerHTML = "";
            contextSize = contextSize || null;

            diffoutputdiv.append(diffview.buildView({
                baseTextLines: base,
                newTextLines: newtxt,
                opcodes: opcodes,
                baseTextName: "<%=oldRouteUpdate.getCreatedAt()%>",
                newTextName: "<%=newRouteUpdate.getCreatedAt()%>",
                contextSize: contextSize,
                viewType: 1
            }));
            <%

        }

    %>


        });


    </script>
</head>
<body>
<nav class="navbar navbar-inverse" style="border-radius: 0px">
    <div class="container-fluid">
        <div class="navbar-header">

            <a class="navbar-brand" href="login.jsp">
                <%=projectName + " / " + routeName%>
            </a>

        </div>

        <ul class="nav navbar-nav navbar-right">
            <li><a href="#"><span class="glyphicon glyphicon-calendar"></span> <%=newRouteUpdate.getCreatedAt()%>
            </a></li>
        </ul>

    </div>
</nav>

<div class="container" style="padding: 50px">

    <%
        if (diffViews.isEmpty()) {
    %>
    <h2 class="text-center">No changes made</h2>
    <%
        }
    %>

    <%
        for (DiffView diffView : diffViews) {
    %>

    <%--Response--%>
    <h4><%=diffView.getTitle()%>
    </h4>
    <div style="display:none;" class="textInput">
        <h2>Base Text</h2>
        <textarea id="<%=diffView.getOldDataId()%>"><%=diffView.getOldData()%></textarea>
    </div>
    <div style="display:none;" class="textInput spacer">
        <h2>New Text</h2>
        <textarea id="<%=diffView.getNewDataId()%>"><%=diffView.getNewData()%></textarea>
    </div>
    <div id="<%=diffView.getDiffOutputId()%>"></div>


    <%
        }
    %>


</div>


</body>
</html>
