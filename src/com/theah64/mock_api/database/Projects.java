package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Projects extends BaseTable<Project> {

    private static final Projects instance = new Projects();
    public static final String COLUMN_PASS_HASH = "pass_hash";

    private Projects() {
        super("projects");
    }

    public static Projects getInstance() {
        return instance;
    }

    @Override
    public String addv3(Project project) throws SQLException {
        String error = null;
        String id = null;
        final String query = "INSERT INTO projects (name, pass_hash) VALUES (?,?);";
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, project.getName());
            ps.setString(2, project.getPassHash());
            ps.executeUpdate();
            final ResultSet rs = ps.getGeneratedKeys();

            if (rs.first()) {
                id = rs.getString(1);
            }

            ps.close();
            rs.close();
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
