package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 30/11/17.
 */
public class Params extends BaseTable<Param> {

    private static final Params instance = new Params();
    public static final String TYPE_REQUIRED = "REQUIRED";
    public static final String TYPE_OPTIONAL = "OPTIONAL";
    private static final String COLUMN_ROUTE_ID = "route_id";
    private static final String COLUMN_TYPE = "type";

    private Params() {
        super("params");
    }

    public static Params getInstance() {
        return instance;
    }


    public void addParamsFromRoute(Route route) throws SQLException {
        String error = null;
        final java.sql.Connection con = Connection.getConnection();

        try {

            if (route.getRequiredParams() != null) {
                addParams(con, route, TYPE_REQUIRED);
            }

            if (route.getOptionalParams() != null) {
                addParams(con, route, TYPE_OPTIONAL);
            }

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

    public void addParams(final java.sql.Connection con, final Route route, final String type) throws SQLException {
        //Move required params
        final String[] reqParams = (type.equals(TYPE_OPTIONAL) ? route.getOptionalParams() : route.getRequiredParams()).split(",");
        String reqInsQuery = "INSERT INTO params (name, route_id, type) VALUES (?,?,?);";
        final PreparedStatement ps1 = con.prepareStatement(reqInsQuery);
        for (final String reqParam : reqParams) {
            ps1.setString(1, reqParam);
            ps1.setString(2, route.getId());
            ps1.setString(3, type);
            ps1.executeUpdate();
        }
        ps1.close();
    }

    @Override
    public List<Param> getAll(String whereColumn, String whereColumnValue) {
        java.sql.Connection con = Connection.getConnection();
        final List<Param> params = new ArrayList<>();
        final String query = String.format("SELECT id, name ,route_id, type FROM params WHERE %s = ?", whereColumn);
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, whereColumnValue);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                do {
                    final String id = rs.getString(COLUMN_ID);
                    final String name = rs.getString(COLUMN_NAME);
                    final String routeId = rs.getString(COLUMN_ROUTE_ID);
                    final String type = rs.getString(COLUMN_TYPE);

                    params.add(new Param(id, name, routeId, type));

                } while (rs.next());
            }

            rs.close();
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
        return params;
    }

    public void updateParamFromRoute(Route route) throws SQLException {

        //Get new params
        final List<Param> newParams = new ArrayList<>();
        addParams(newParams, route.getRequiredParams(), TYPE_REQUIRED);
        addParams(newParams, route.getOptionalParams(), TYPE_OPTIONAL);

        //Get all params
        final List<Param> oldParams = getAll(COLUMN_ROUTE_ID, route.getId());
        final List<Param> deletedParams = new ArrayList<>();
        final List<Param> addedParams = new ArrayList<>();

        if (oldParams.isEmpty() && !newParams.isEmpty()) {
            addedParams.addAll(newParams);
        } else if (newParams.isEmpty() && !oldParams.isEmpty()) {
            deletedParams.addAll(oldParams);
        } else {
            //finding deleted params
            for (Param oldParam : oldParams) {
                if (!newParams.contains(oldParam)) {
                    deletedParams.add(oldParam);
                }
            }

            //finding addedParmas
            for (final Param newParam : newParams) {
                if (!oldParams.contains(newParam)) {
                    addedParams.add(newParam);
                }
            }
        }

        java.sql.Connection con = Connection.getConnection();

        if (!deletedParams.isEmpty()) {
            //Delete params
            final String delQuery = "DELETE FROM params WHERE route_id = ? AND name = ?;";
            final PreparedStatement ps = con.prepareStatement(delQuery);
            for (Param delParam : deletedParams) {
                ps.setString(1, route.getId());
                ps.setString(2, delParam.getName());
                ps.executeUpdate();
            }
            ps.close();
        }

        if (!addedParams.isEmpty()) {
            //added params
            String reqInsQuery = "INSERT INTO params (name, route_id, type) VALUES (?,?,?);";
            final PreparedStatement ps1 = con.prepareStatement(reqInsQuery);
            for (final Param addedParam : addedParams) {
                ps1.setString(1, addedParam.getName());
                ps1.setString(2, route.getId());
                ps1.setString(3, addedParam.getType());
                ps1.executeUpdate();
            }
            ps1.close();
        }

    }

    private void addParams(List<Param> newParams, String params, String type) {
        if (params != null) {
            final String[] paramsArr = params.split(",");
            for (final String param : paramsArr) {
                newParams.add(new Param(null, param, null, type));
            }
        }
    }
}
