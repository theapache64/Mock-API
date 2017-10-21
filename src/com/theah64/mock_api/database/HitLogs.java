package com.theah64.mock_api.database;

import com.theah64.mock_api.models.HitLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLogs extends BaseTable<HitLog> {

    private static final HitLogs instance = new HitLogs();
    private static final String COLUMN_REQUEST_BODY = "request_body";
    private static final String COLUMN_ERROR_RESPONSE = "error_response";
    private static final String COLUMN_CREATED_AT = "created_at";

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

    public List<HitLog> getRecent(final String projectId, final String route, final int count) {
        final String query = "SELECT hl.request_body, hl.error_response, hl.created_at FROM hit_logs hl INNER JOIN jsons j ON j.id = hl.route_id WHERE j.project_id = ? AND  j.route = ? ORDER BY hl.id DESC LIMIT ?;";
        List<HitLog> hitLogs = null;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, projectId);
            ps.setString(2, route);
            ps.setInt(3, count);

            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                hitLogs = new ArrayList<>();
                do {
                    final String reqBody = rs.getString(COLUMN_REQUEST_BODY);
                    final String errorResp = rs.getString(COLUMN_ERROR_RESPONSE);
                    final String createdAt = rs.getString(COLUMN_CREATED_AT);
                    hitLogs.add(new HitLog(null, reqBody, errorResp, createdAt));
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

        return hitLogs;
    }

}
