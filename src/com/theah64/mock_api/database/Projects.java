package com.theah64.mock_api.database;

import com.sun.istack.internal.Nullable;
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
    public static final String COLUMN_API_KEY = "api_key";
    public static final String COLUMN_BASE_OG_API_URL = "base_og_api_url";

    private Projects() {
        super("projects");
    }

    public static Projects getInstance() {
        return instance;
    }


    @Override
    public Project get(String column, String value) {
        return get(column, value, null, null);
    }

    @Override
    public Project get(String column1, String value1, @Nullable String column2, @Nullable String value2) {
        Project project = null;
        final String query;

        if (column2 != null && value2 != null) {
            query = String.format("SELECT id,name,api_key,base_og_api_url,pass_hash FROM %s WHERE %s = ? AND %s = ? AND is_active = 1 LIMIT 1", tableName, column1, column2);
        } else {
            query = String.format("SELECT id,name,api_key,base_og_api_url,pass_hash FROM %s WHERE %s = ? AND is_active = 1 LIMIT 1", tableName, column1);
        }
        String resultValue = null;
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value1);
            if (column2 != null && value2 != null) {
                ps.setString(2, value2);
            }


            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String apiKey = rs.getString(COLUMN_API_KEY);
                final String baseOgApiUrl = rs.getString(COLUMN_BASE_OG_API_URL);
                final String passHash = rs.getString(COLUMN_PASS_HASH);
                project = new Project(id, name, passHash, apiKey, baseOgApiUrl);
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

        return project;
    }

    @Override
    public String addv3(Project project) throws SQLException {
        String error = null;
        String id = null;
        final String query = "INSERT INTO projects (name, pass_hash,api_key,base_og_api_url) VALUES (?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, project.getName().replaceAll("\\s+", "").trim().toLowerCase());
            ps.setString(2, project.getPassHash());
            ps.setString(3, project.getApiKey());
            ps.setString(4, project.getBaseOgApiUrl());
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
