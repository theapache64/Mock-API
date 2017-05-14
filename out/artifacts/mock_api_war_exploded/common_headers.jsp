<%@ page import="com.theah64.mock_api.Connection" %>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<%
    if (Connection.isDebugMode()) {
%>
<%--OFFLINE RESOURCES--%>
<link rel="stylesheet" href="/mock_api/styles/bootstrap.min.css">
<script src="/mock_api/js/jquery-2.2.4.min.js"></script>
<script src="/mock_api/js/bootstrap.min.js"></script>
<%
} else {
%>
<%--ONLINE RESOURCES--%>
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<%
    }
%>

<link rel="stylesheet" href="/mock_api/styles/style.css">
<script src="/mock_api/js/code.js"></script>


