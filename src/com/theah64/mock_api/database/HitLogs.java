package com.theah64.mock_api.database;

import com.theah64.mock_api.models.HitLog;

import java.sql.SQLException;

/**
 * Created by theapache64 on 20/10/17.
 */
public class HitLogs extends BaseTable<HitLog> {
    private HitLogs() {
        super("hit_logs");
    }

    @Override
    public void add(HitLog newInstance) throws SQLException {
        final String query = "INSERT INTO hit_logs (route_id, request_body, is_success) VALUES (?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        final Pre
    }
}
