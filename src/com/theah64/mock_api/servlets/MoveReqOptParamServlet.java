package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Params;
import com.theah64.mock_api.models.Route;
import com.theah64.webengine.database.Connection;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 30/11/17.
 */
@WebServlet(urlPatterns = {"/move_req"})
public class MoveReqOptParamServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final java.sql.Connection con = Connection.getConnection();
        final String query = "SELECT id, required_params, optional_params FROM routes;";
        try {
            final java.sql.Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(query);

            final List<Route> routes = new ArrayList<>();
            if (rs.first()) {

                do {

                    final String id = rs.getString("id");
                    final String reqParams = rs.getString("required_params");
                    final String optionalParams = rs.getString("optional_params");

                    Route route = new Route(id, null, null, null, reqParams, optionalParams, null, null, false, -1, -1);
                    routes.add(route);

                } while (rs.next());
            }

            rs.close();
            stmt.close();

            System.out.println("Total routes: " + routes.size());

            for (final Route route : routes) {

                if (route.getRequiredParams() != null) {
                    Params.getInstance().addParams(con, route.getRequiredParams());
                }

                if (route.getOptionalParams() != null) {
                    Params.getInstance().addParams(con, route.getOptionalParams());
                }
            }

            con.prepareStatement("UPDATE routes SET required_params = NULL, optional_params = NULL;").executeUpdate();


        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

    }

}
