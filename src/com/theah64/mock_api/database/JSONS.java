package com.theah64.mock_api.database;

import com.theah64.mock_api.models.JSON;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
