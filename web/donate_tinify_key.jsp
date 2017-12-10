<%@ page import="com.theah64.mock_api.database.TinifyKeys" %>
<%@ page import="com.theah64.webengine.utils.Form" %>
<%@ page import="com.theah64.mock_api.models.TinifyKey" %>
<%@ page import="com.theah64.webengine.utils.Request" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.theah64.webengine.database.querybuilders.QueryBuilderException" %>
<%@ page import="com.theah64.webengine.utils.StatusResponse" %>
<%@ page import="com.tinify.Tinify" %>
<%@ page import="com.tinify.AccountException" %><%--
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
        <div class="col-md-5">
            <h3>Donate Tinify Key</h3> <br>
            <%--key, email, dashboard_url, usage--%>
            <form method="POST" action="donate_tinify_key.jsp?api_key=<%=project.getApiKey()%>">

                <%--Key--%>
                <div class="form-group">
                    <label for="iKey">API Key</label>
                    <input id="iKey" class="form-control" placeholder="Tinify API key" type="text"
                           name="<%=TinifyKeys.COLUMN_KEY%>" required/>
                </div>

                <%--Email--%>
                <div class="form-group">
                    <label for="iEmail">Email</label>
                    <input id="iEmail" class="form-control" placeholder="Email address" type="email"
                           name="<%=TinifyKeys.COLUMN_EMAIL%>" required/>
                </div>

                <%
                    final Form form = new Form(request, new String[]{
                            TinifyKeys.COLUMN_KEY,
                            TinifyKeys.COLUMN_EMAIL
                    });

                    try {
                        if (form.isSubmitted() && form.isAllRequiredParamsAvailable()) {

                            final String key = form.getString(TinifyKeys.COLUMN_KEY);


                            Tinify.setKey(key);

                            if (Tinify.validate()) {

                                TinifyKeys.getInstance().add(new TinifyKey(
                                        null,
                                        form.getString(TinifyKeys.COLUMN_KEY),
                                        form.getString(TinifyKeys.COLUMN_EMAIL),
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
    </div>
</div>

</body>
</html>
