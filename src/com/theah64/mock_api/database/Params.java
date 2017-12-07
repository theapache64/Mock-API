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
 * Created by theapache64 on 30/11/17.
 */
public class Params extends BaseTable<Param> {

    private static final Params instance = new Params();
    public static final String COLUMN_ROUTE_ID = "route_id";
    private static final String COLUMN_IS_REQUIRED = "is_required";
    private static final String COLUMN_DEFAULT_VALUE = "default_value";
    private static final String COLUMN_DESCRITION = "description";
    private static final String COLUMN_DATA_TYPE = "data_type";

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

            addParams(con, route.getRequiredParams());
            addParams(con, route.getOptionalParams());

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

    public void addParams(final java.sql.Connection con, List<Param> params) throws SQLException {
        //Move required params
        String reqInsQuery = "INSERT INTO params (name, route_id, is_required,data_type,default_value,description) VALUES (?,?,?,?,?,?);";
        final PreparedStatement ps1 = con.prepareStatement(reqInsQuery);
        for (final Param param : params) {
            ps1.setString(1, param.getName());
            ps1.setString(2, param.getRouteId());
            ps1.setBoolean(3, param.isRequired());
            ps1.setString(4, param.getDataType());
            ps1.setString(5, param.getDefaultValue());
            ps1.setString(6, param.getDescription());
            ps1.executeUpdate();

            System.out.println("Added: " + param.toStringAll());
        }


        ps1.close();
    }

    public void updateParams(final java.sql.Connection con, List<Param> params) throws SQLException {
        //Move required params
        final String updateQuery = "UPDATE params SET is_required = ? , data_type = ? ,  default_value = ? , description = ? WHERE route_id = ? AND name = ?";
        final PreparedStatement ps1 = con.prepareStatement(updateQuery);
        for (final Param param : params) {
            ps1.setBoolean(1, param.isRequired());
            ps1.setString(2, param.getDataType());
            ps1.setString(3, param.getDefaultValue());
            ps1.setString(4, param.getDescription());
            ps1.setString(5, param.getRouteId());
            ps1.setString(6, param.getName());
            ps1.executeUpdate();

            System.out.println("Added: " + param.toStringAll());
        }


        ps1.close();
    }

    @Override
    public List<Param> getAll(String whereColumn, String whereColumnValue) {

        java.sql.Connection con = Connection.getConnection();
        final List<Param> params = new ArrayList<>();
        final String query = String.format("SELECT id, name ,route_id, is_required, data_type, default_value, description FROM params WHERE %s = ?", whereColumn);
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, whereColumnValue);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                do {
                    final String id = rs.getString(COLUMN_ID);
                    final String name = rs.getString(COLUMN_NAME);
                    final String routeId = rs.getString(COLUMN_ROUTE_ID);
                    final String defaultValue = rs.getString(COLUMN_DEFAULT_VALUE);
                    final String dataType = rs.getString(COLUMN_DATA_TYPE);
                    final String description = rs.getString(COLUMN_DESCRITION);
                    final boolean isRequired = rs.getBoolean(COLUMN_IS_REQUIRED);

                    params.add(new Param(id, name, routeId, dataType, defaultValue, description, isRequired));

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

    void updateParamFromRoute(Route route) {

        //Get new params
        final List<Param> newParams = new ArrayList<>();
        newParams.addAll(route.getRequiredParams());
        newParams.addAll(route.getOptionalParams());


        //Get all params
        final List<Param> oldParams = getAll(COLUMN_ROUTE_ID, route.getId());
        final List<Param> deletedParams = new ArrayList<>();
        final List<Param> addedParams = new ArrayList<>();
        final List<Param> updatedParams = new ArrayList<>();

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
                } else {
                    updatedParams.add(newParam);
                }
            }
        }

        System.out.println("---------------------------------");
        System.out.println("OLDPARAMS: " + oldParams);
        System.out.println("NEWPARAMS: " + newParams);

        System.out.println("---------------------------------");

        java.sql.Connection con = Connection.getConnection();

        try {

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
                addParams(con, addedParams);
            }

            if (!updatedParams.isEmpty()) {
                updateParams(con, updatedParams);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }


}
