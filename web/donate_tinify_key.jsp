<%@ page import="com.theah64.mock_api.database.TinifyKeys" %>
<%@ page import="com.theah64.mock_api.models.TinifyKey" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="com.tinify.AccountException" %>
<%@ page import="com.tinify.Tinify" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 10/12/17
  Time: 10:15 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="login_check.jsp" %>
<html>
<head>
    <title>Donate Tinify Key</title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<%@include file="nav_bar.jsp" %>

<div class="container">
    <div class="row">
        <div class="col-md-6">
            <h3>Donate Tinify Key</h3> <br>
            <%--key, email, dashboard_url, usage--%>
            <form method="POST" action="donate_tinify_key.jsp?api_key=<%=project.getApiKey()%>">

                <%--Key--%>
                <div class="form-group">
                    <label for="iKey">API Key</label>
                    <input id="iKey" class="form-control" placeholder="Tinify API key" type="text"
                           name="<%=TinifyKeys.Companion.getCOLUMN_KEY()%>" required/>
                </div>

                <%--Email--%>
                <div class="form-group">
                    <label for="iEmail">Email</label>
                    <input id="iEmail" class="form-control" placeholder="Email address" type="email"
                           name="<%=TinifyKeys.Companion.getCOLUMN_EMAIL()%>" required/>
                </div>

                <%
                    final Form form = new Form(request, new String[]{
                            TinifyKeys.Companion.getCOLUMN_KEY(),
                            TinifyKeys.Companion.getCOLUMN_EMAIL()
                    });

                    try {
                        if (form.isSubmitted() && form.isAllRequiredParamsAvailable()) {

                            final String key = form.getString(TinifyKeys.Companion.getCOLUMN_KEY());


                            Tinify.setKey(key);

                            if (Tinify.validate()) {

                                TinifyKeys.Companion.getINSTANCE().add(new TinifyKey(
                                        null,
                                        form.getString(TinifyKeys.Companion.getCOLUMN_KEY()),
                                        form.getString(TinifyKeys.Companion.getCOLUMN_EMAIL()),
                                        String.valueOf(Tinify.compressionCount())
                                ));


                                StatusResponse.redirect(response, "Done", "Donation succeeded");
                            }

                        }
                    } catch (Request.RequestException | AccountException | SQLException | QueryBuilderException e) {
                        e.printStackTrace();

                %>
                <p class="text-danger"><%=e instanceof AccountException ? "Invalid Tinify API key " : e.getMessage()%>
                </p>
                <%

                    }
                %>

                <input type="submit" name="<%=Form.KEY_IS_SUBMITTED%>" class="btn btn-primary pull-right"
                       value="Donate"/>


            </form>
        </div>

        <div class="col-md-6">
            <h3>Live Keys</h3> <br>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <td>Email</td>
                    <td>Key</td>
                    <td>Usage</td>
                </tr>
                </thead>

                <tbody>
                <%
                    try {
                        final List<TinifyKey> tinifyKeys = TinifyKeys.Companion.getINSTANCE().getAll();
                        for (final TinifyKey tinifyKey : tinifyKeys) {
                %>

                <tr>
                    <td><%=tinifyKey.getEmail()%>
                    </td>
                    <td><%=tinifyKey.getKey().replaceAll("[A-Z]", "*")%>
                    </td>
                    <td><%=tinifyKey.getUsage()%>/500</td>
                </tr>

                <%
                        }

                    } catch (QueryBuilderException | SQLException e) {
                        e.printStackTrace();
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
