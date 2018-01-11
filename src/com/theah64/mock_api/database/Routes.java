package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Routes extends BaseTable<Route> {

    private static final Routes instance = new Routes();
    public static final String COLUMN_DEFAULT_RESPONSE = "default_response";
    public static final String COLUMN_PROJECT_ID = "project_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_SECURE = "is_secure";
    public static final String COLUMN_DELAY = "delay";
    public static final String COLUMN_EXTERNAL_API_URL = "external_api_url";
    public static final String COLUMN_UPDATED_AT_IN_MILLIS = "updated_at_in_millis";
    public static final String KEY_PARAMS = "params";
    public static final String COLUMN_METHOD = "method";

    private Routes() {
        super("routes");
    }

    public static Routes getInstance() {
        return instance;
    }

    @Override
    public String addv3(Route route) throws SQLException {
        String error = null;
        String id = null;
        final String query = "INSERT INTO routes (project_id, name, default_response, description, is_secure, delay,external_api_url,updated_at_in_millis,method) VALUES (?,?,?,?,?,?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps0 = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps0.setString(1, route.getProjectId());
            ps0.setString(2, route.getName());
            ps0.setString(3, route.getDefaultResponse());
            ps0.setString(4, route.getDescription());
            ps0.setBoolean(5, route.isSecure());
            ps0.setLong(6, route.getDelay());
            ps0.setString(7, route.getExternalApiUrl());
            ps0.setLong(8, System.currentTimeMillis());
            ps0.setString(9, route.getMethod());

            ps0.executeUpdate();
            final ResultSet rs = ps0.getGeneratedKeys();
            if (rs.first()) {
                id = rs.getString(1);
            }
            rs.close();
            ps0.close();

            route.setId(id);
            Params.getInstance().addParamsFromRoute(route);

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

    public List<Route> getAll(final String projectId) throws SQLException {

        final List<Route> jsonList = new ArrayList<>();
        final String query = "SELECT id, name, external_api_url FROM routes WHERE project_id = ? AND is_active = 1 ORDER BY updated_at_in_millis DESC";
        String error = null;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectId);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                do {

                    final String id = rs.getString(COLUMN_ID);
                    final String route = rs.getString(COLUMN_NAME);
                    final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);

                    jsonList.add(new Route(id, projectId, route, null, null, externalApiUrl, null, null, false, 0, -1));

                } while (rs.next());
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

        return jsonList;

    }

    public List<Route> getAllDetailed(final String projectId) throws SQLException {

        List<Route> routeList = new ArrayList<>();

        final String query = "SELECT r.id,r.name, r.updated_at_in_millis,r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE p.id = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id;";
        String error = null;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectId);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                routeList = new ArrayList<>();

                do {

                    final String id = rs.getString(COLUMN_ID);
                    final String routeName = rs.getString(COLUMN_NAME);
                    final String response = rs.getString(COLUMN_DEFAULT_RESPONSE);
                    final String description = rs.getString(COLUMN_DESCRIPTION);
                    final boolean isSecure = rs.getBoolean(COLUMN_IS_SECURE);
                    final long delay = rs.getLong(COLUMN_DELAY);
                    final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);
                    final long updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS);
                    final String method = rs.getString(COLUMN_METHOD);

                    final List<Param> allParams = Params.getInstance().getAll(Params.COLUMN_ROUTE_ID, id);

                    routeList.add(new Route(id, projectId, routeName, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis));
                    ;

                } while (rs.next());
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

        return routeList;

    }

    public Route get(String projectName, String routeName) throws SQLException {

        String error = null;
        Route route = null;
        final String query = "SELECT r.id, r.updated_at_in_millis,r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE p.name = ? AND r.name = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id LIMIT 1;";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectName);
            ps.setString(2, routeName);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String response = rs.getString(COLUMN_DEFAULT_RESPONSE);
                final String description = rs.getString(COLUMN_DESCRIPTION);
                final boolean isSecure = rs.getBoolean(COLUMN_IS_SECURE);
                final long delay = rs.getLong(COLUMN_DELAY);
                final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);
                final long updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS);
                final String method = rs.getString(COLUMN_METHOD);

                final List<Param> allParams = Params.getInstance().getAll(Params.COLUMN_ROUTE_ID, id);

                route = new Route(id, null, routeName, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis);
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

        if (route == null) {
            throw new SQLException("No response found for " + route);
        }
        return route;
    }

    public Route getRouteBy(String column, String value) throws SQLException {

        String error = null;
        Route route = null;
        final String query = String.format("SELECT r.id,r.name, r.updated_at_in_millis,r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE r.%s = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id LIMIT 1;", column);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String routeName = rs.getString(COLUMN_NAME);
                final String response = rs.getString(COLUMN_DEFAULT_RESPONSE);
                final String description = rs.getString(COLUMN_DESCRIPTION);
                final boolean isSecure = rs.getBoolean(COLUMN_IS_SECURE);
                final long delay = rs.getLong(COLUMN_DELAY);
                final String externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL);
                final long updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS);
                final String method = rs.getString(COLUMN_METHOD);

                final List<Param> allParams = Params.getInstance().getAll(Params.COLUMN_ROUTE_ID, id);

                route = new Route(id, null, routeName, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis);
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

        if (route == null) {
            throw new SQLException("No response found for " + route);
        }

        return route;
    }

    @Override
    public boolean update(Route route) {
        boolean isUpdated = false;
        final String query = "UPDATE routes SET default_response = ?, description = ? , is_secure = ? , delay = ?, external_api_url = ?, updated_at_in_millis = ?, method = ?  WHERE name = ? AND project_id = ?;";
        java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            String r = route.getDefaultResponse() == null ? get(COLUMN_ID, route.getId(), COLUMN_DEFAULT_RESPONSE, true) : route.getDefaultResponse();
            System.out.println("R is : " + r);
            ps.setString(1, r);
            ps.setString(2, route.getDescription());
            ps.setBoolean(3, route.isSecure());
            ps.setLong(4, route.getDelay());
            ps.setString(5, route.getExternalApiUrl());
            ps.setLong(6, System.currentTimeMillis());
            ps.setString(7, route.getMethod());
            ps.setString(8, route.getName());
            ps.setString(9, route.getProjectId());

            Params.getInstance().updateParamFromRoute(route);

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

    public void updateBaseOGAPIURL(String projectId, String oldBaseUrl, String newBaseUrl) throws SQLException {
        final String query = String.format("UPDATE routes SET %s = REPLACE(%s, ?, ?) WHERE INSTR(%s, ?) > 0 AND project_id = ?;", COLUMN_EXTERNAL_API_URL, COLUMN_EXTERNAL_API_URL, COLUMN_EXTERNAL_API_URL);
        final java.sql.Connection con = Connection.getConnection();
        String error = null;
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, oldBaseUrl);
            ps.setString(2, newBaseUrl);
            ps.setString(3, oldBaseUrl);
            ps.setString(4, projectId);
            ps.executeUpdate();
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
    }

    @Override
    public Route get(String column1, String value1, String column2, String value2) {
        return super.get(column1, value1, column2, value2);
    }
}
