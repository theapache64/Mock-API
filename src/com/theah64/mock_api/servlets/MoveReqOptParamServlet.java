package com.theah64.mock_api.servlets;

import com.theah64.mock_api.models.Route;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
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

        final java.sql.Connection con = com.theah64.mock_api.database.Connection.getConnection();
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
                    //Move required params
                    final String[] reqParams = route.getRequiredParams().split(",");
                    for (final String reqParam : reqParams) {
                        String reqInsQuery = "INSERT INTO params (name, route_id, type) VALUES (?,?,?);";
                        final PreparedStatement ps = con.prepareStatement(reqInsQuery);
                        ps.setString(1, reqParam);
                        ps.setString(2, route.getId());
                        ps.setString(3, "REQUIRED");
                        ps.executeUpdate();
                        ps.close();
                    }
                }


                if (route.getOptionalParams() != null) {
                    //Move required params
                    final String[] optParams = route.getOptionalParams().split(",");
                    for (final String optParam : optParams) {
                        String reqInsQuery = "INSERT INTO params (name, route_id, type) VALUES (?,?,?);";
                        final PreparedStatement ps = con.prepareStatement(reqInsQuery);
                        ps.setString(1, optParam);
                        ps.setString(2, route.getId());
                        ps.setString(3, "OPTIONAL");
                        ps.executeUpdate();
                        ps.close();
                    }
                }
            }

            con.prepareStatement("UPDATE routes SET required_params = NULL, optional_params = NULL;").executeUpdate();


        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

    }
}
