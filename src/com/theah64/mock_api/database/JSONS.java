package com.theah64.mock_api.database;

import com.theah64.mock_api.models.JSON;
import org.json.JSONException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public static final String COLUMN_REQUIRED_PARAMS = "required_params";
    public static final String COLUMN_OPTIONAL_PARAMS = "optional_params";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_SECURE = "is_secure";
    public static final String COLUMN_DELAY = "delay";
    public static final String COLUMN_EXTERNAL_API_URL = "external_api_url";

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
        final String query = "INSERT INTO jsons (project_id, route, response, required_params, optional_params, description, is_secure, delay,external_api_url) VALUES (?,?,?,?,?,?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, json.getProjectId());
            ps.setString(2, json.getRoute());
            ps.setString(3, json.getResponse());
            ps.setString(4, json.getRequiredParams());
            ps.setString(5, json.getOptionalParams());
            ps.setString(6, json.getDescription());
            ps.setBoolean(7, json.isSecure());
            ps.setLong(8, json.getDelay());
            ps.setString(9, json.getExternalApiUrl());

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
        final String query = "SELECT id, route, external_api_url FROM jsons WHERE project_id = ? AND is_active = 1";
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
                    final String route = rs.getString(COLUMN_ROUTE);
                    final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);
                    jsonList.add(new JSON(id, null, route, null, null, null, null, externalApiUrl, false, 0));
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

    public JSON get(String projectName, String route) throws SQLException {
        String error = null;
        JSON json = null;
        final String query = "SELECT j.description, j.is_secure, j.delay, j.response, j.required_params, j.optional_params, external_api_url FROM jsons j INNER JOIN projects p ON p.id = j.project_id WHERE p.name = ? AND j.route = ? AND p.is_active = 1 AND j.is_active = 1 LIMIT 1";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectName);
            ps.setString(2, route);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final String response = rs.getString(COLUMN_RESPONSE);
                final String reqPar = rs.getString(COLUMN_REQUIRED_PARAMS);
                final String opPar = rs.getString(COLUMN_OPTIONAL_PARAMS);
                final String description = rs.getString(COLUMN_DESCRIPTION);
                final boolean isSecure = rs.getBoolean(COLUMN_IS_SECURE);
                final long delay = rs.getLong(COLUMN_DELAY);
                final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);

                json = new JSON(null, null, null, response, reqPar, opPar, description, externalApiUrl, isSecure, delay);
            }
            rs.close();
            ps.close();
        } catch (SQLException | JSONException e) {
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

        if (json == null) {
            throw new SQLException("No response found for " + route);
        }
        return json;
    }

    @Override
    public boolean update(JSON json) {
        boolean isUpdated = false;
        final String query = "UPDATE jsons SET response = ?, required_params = ? , optional_params = ? , description = ? , is_secure = ? , delay = ?, external_api_url = ?   WHERE route = ? AND project_id = ?;";
        java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, json.getResponse());
            ps.setString(2, json.getRequiredParams());
            ps.setString(3, json.getOptionalParams());
            ps.setString(4, json.getDescription());
            ps.setBoolean(5, json.isSecure());
            ps.setLong(6, json.getDelay());
            ps.setString(7, json.getExternalApiUrl());
            ps.setString(8, json.getRoute());
            ps.setString(9, json.getProjectId());

            isUpdated = ps.executeUpdate() == 1;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!isUpdated) {
            throw new IllegalArgumentException("Failed to update json");
        }
        return true;
    }
}
