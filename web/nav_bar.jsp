<%@ page import="com.theah64.webengine.utils.DarKnight" %>
<%@ page import="java.net.URLEncoder" %>
<%
    final boolean isHomePage = request.getAttribute("is_home_page") != null;
%>
<nav class="navbar navbar-inverse" style="border-radius: 0px">
    <div class="container-fluid">
        <div class="navbar-header">

            <a class="navbar-brand" href="index.jsp?api_key=<%=project.getApiKey()%>">
                <%=project.getName()%>
            </a>

        </div>

        <ul class="nav navbar-nav">

            <%
                if (isHomePage) {
            %>

            <%--File--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">File
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">

                    <%--Find in default response--%>
                    <li>
                        <a id="aFindInDefRes" href="#">Find In Default Response
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + F)</small>
                        </a>
                    </li>

                    <%--Find route--%>
                    <li>
                        <a onclick="findRoute();" id="aFindRoute" href="#">Find Route
                            <small class="text-muted menu_shortcut">(F1)</small>
                        </a>
                    </li>

                    <%--Save Route--%>
                    <li>
                        <a id="aSaveRoute" href="#">Save Route
                            <small class="text-muted menu_shortcut">(F7)</small>
                        </a>
                    </li>


                </ul>
            </li>


            <%--Insert--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Insert
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">

                    <li>
                        <a id="aGenRandomText" href="#">Random Text
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + R)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aDefSucResp" href="#">Default Success Response
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + N)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aDefErrResp" href="#">Default Error Response
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + E)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aDuplicate" href="#">Duplicate
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + D)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aJsonKeyValue" href="#">JSON Key-Value
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + S)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aRandomImageURL" href="#">Random Image URL
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + I)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aAddParams" href="#">Add Parameters
                            <small class="text-muted menu_shortcut">(F10)</small>
                        </a>
                    </li>

                </ul>
            </li>

            <%--Edit--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Edit
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">

                    <li>
                        <a id="aFormatResponse" href="#">Format Response
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + L)</small>
                        </a>
                    </li>

                </ul>
            </li>

            <%--Java--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Java
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">

                    <li>
                        <a id="aPojo"
                           href="#">POJO
                            <small class="text-muted menu_shortcut pull-right">(Ctrl + Alt + M)</small>
                        </a>
                    </li>


                    <li>
                        <a id="aAPIInterfaceMethod"
                           href="#">API Interface Method
                            <small class="text-muted menu_shortcut">(F4)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aAPICall"
                           href="#">API Call
                            <small class="text-muted menu_shortcut">(F8)</small>
                        </a>
                    </li>

                </ul>
            </li>

            <%--JavaScript--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">TypeScript
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">


                    <li>
                        <a id="aTypeScriptInterface"
                           href="#">Interface
                            <small class="text-muted menu_shortcut pull-right">(Ctrl + Alt + J)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aTypeScriptClass"
                           href="#">Class
                            <small class="text-muted menu_shortcut pull-right">(Ctrl + Alt + K)</small>
                        </a>
                    </li>

                    <li>
                        <a id="aReduxDuck"
                           href="#">Redux Duck
                            <small class="text-muted menu_shortcut">(F8)</small>
                        </a>
                    </li>

                </ul>
            </li>


            <%--Generate--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Generate
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">

                    <li>
                        <a target="_blank"
                           href="documentation.jsp?api_key=<%=URLEncoder.encode(DarKnight.getEncrypted(project.getApiKey()),"UTF-8")%>">Documentation</a>
                    </li>

                    <li>
                        <a target="_blank"
                           href="documentation_markdown.jsp?api_key=<%=URLEncoder.encode(DarKnight.getEncrypted(project.getApiKey()),"UTF-8")%>">Documentation (MarkDown)</a>
                    </li>

                    <li>
                        <a id="aParamFromURL" href="#">Params From URL</a>
                    </li>

                </ul>
            </li>


            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Images
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <li>
                        <a id="aUploadImage"
                           href="#">Upload Image
                            <small class="text-muted menu_shortcut">(Ctrl + Alt + U)</small>
                        </a>
                    </li>
                    <li><a href="images.jsp?api_key=<%=project.getApiKey()%>">Browse Images</a></li>
                </ul>
            </li>

            <%
                }
            %>


            <%--More--%>
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Other
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <%=isHomePage ? "<li><a href=\"#\" data-toggle=\"modal\" data-target=\"#shortcuts\">Shortcuts</a></li>" : "" %>
                    <li><a href="donate_tinify_key.jsp?api_key=<%=project.getApiKey()%>">Donate Tinify Key</a></li>
                    <li><a id="duplicateProject" href="#">Duplicate Project</a></li>
                </ul>
            </li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <%=isHomePage ? "<li><a href=settings.jsp?api_key=" + project.getApiKey() + "><span class=\"glyphicon glyphicon-cog\"></span> Settings</a></li>" : "" %>
            <li><a href="logout.jsp"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
        </ul>
    </div>
</nav>