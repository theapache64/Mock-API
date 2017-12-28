<%@ page import="com.theah64.webengine.utils.DarKnight" %>
<%@ page import="java.net.URLEncoder" %>
<%
    final boolean isHomePage = request.getAttribute("is_home_page") != null;
%>
<nav class="navbar navbar-inverse" style="border-radius: 0px">
    <div class="container-fluid">
        <div class="navbar-header">

            <a class="navbar-brand" href="#">
                <%=project.getName()%>
            </a>


        </div>
        <ul class="nav navbar-nav">
            <li><a href="index.jsp?api_key=<%=project.getApiKey()%>">Home</a></li>
            <li>
                <a target="_blank"
                   href="documentation.jsp?api_key=<%=URLEncoder.encode(DarKnight.getEncrypted(project.getApiKey()),"UTF-8")%>">Documentation</a>
            </li>

            <%=isHomePage ? "<li><a href=\"#\" data-toggle=\"modal\" data-target=\"#shortcuts\">Shortcuts</a></li>" : "" %>
            <li><a href="compare.jsp?api_key=<%=project.getApiKey()%>">Compare</a></li>
            <li><a href="images.jsp?api_key=<%=project.getApiKey()%>">Images</a></li>
            <li><a href="donate_tinify_key.jsp?api_key=<%=project.getApiKey()%>">Donate Key</a></li>

        </ul>
        <ul class="nav navbar-nav navbar-right">
            <%
                if (isHomePage) {
            %>
            <li><a id="base_og_api_url"
                   href="#"><%=project.getBaseOgApiUrl() == null ? "Set original base URL" : project.getBaseOgApiUrl()%>
            </a></li>
            <%
                }
            %>

            <li><a href="logout.jsp"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
        </ul>
    </div>
</nav>