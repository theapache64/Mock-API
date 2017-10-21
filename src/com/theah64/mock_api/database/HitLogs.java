package com.theah64.mock_api.database;

import com.theah64.mock_api.models.HitLog;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLogs extends BaseTable<HitLog> {

    private static final HitLogs instance = new HitLogs();

    private HitLogs() {
        super("hit_logs");
    }


    public static HitLogs getInstance() {
        return instance;
    }

    @Override
    public void add(HitLog hitLog) throws SQLException {
        final String query = "INSERT INTO hit_logs (route_id, request_body, error_response) VALUES (?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        String error = null;
        try {

            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, hitLog.getRouteId());
            ps.setString(2, hitLog.getRequestBody());
            ps.setString(3, hitLog.getErrorResponse());
            if (ps.executeUpdate() != 1) {
                error = "Failed to insert new hit log";
            }
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
}
