package com.theah64.mock_api.database;

import com.theah64.mock_api.models.JSON;
import org.json.JSONException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
public class JSONS extends BaseTable<JSON> {

    private static final JSONS instance = new JSONS();
    public static final String COLUMN_RESPONSE = "response";
    public static final String COLUMN_PROJECT_ID = "project_id";
    public static final String COLUMN_ROUTE = "route";

    private JSONS() {
        super("jsons");
    }

    public static JSONS getInstance() {
        return instance;
    }

    @Override
    public String addv3(JSON json) throws SQLException {
        String error = null;
        String id = null;
        final String query = "INSERT INTO jsons (project_id, route, response) VALUES (?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, json.getProjectId());
            ps.setString(2, json.getRoute());
            ps.setString(3, json.getResponse());
            ps.executeUpdate();
            final ResultSet rs = ps.getGeneratedKeys();
            if (rs.first()) {
                id = rs.getString(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            error = e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        manageError(error);
        return id;
    }

    public List<JSON> getAll(final String projectId) throws SQLException {
        List<JSON> jsonList = null;
        final String query = "SELECT id,response, route FROM jsons WHERE project_id = ? AND is_active = 1";
        String error = null;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectId);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                jsonList = new ArrayList<>();
                do {
                    final String id = rs.getString(COLUMN_ID);
                    final String response = rs.getString(COLUMN_RESPONSE);
                    final String route = rs.getString(COLUMN_ROUTE);
                    jsonList.add(new JSON(id, null, route, response));
                } while (rs.next());
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            error = e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (jsonList == null) {
            throw new SQLException("No route found");
        }

        manageError(error);

        return jsonList;

    }

    public String getResponse(String projectName, String route) throws SQLException {
        String error = null;
        String response = null;
        final String query = "SELECT j.response FROM jsons j INNER JOIN projects p ON p.id = j.project_id WHERE p.name = ? AND j.route = ? AND p.is_active = 1 AND j.is_active = 1 LIMIT 1";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectName);
            ps.setString(2, route);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                response = rs.getString(COLUMN_RESPONSE);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            error = e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        manageError(error);
        if (response == null) {
            throw new SQLException("No response found");
        }
        return response;
    }

}
