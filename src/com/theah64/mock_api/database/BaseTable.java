package com.theah64.mock_api.database;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.theah64.mock_api.exceptions.RequestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 11/22/2015.
 */
public class BaseTable<T> {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    private static final String ERROR_MESSAGE_UNDEFINED_METHOD = "Undefined method.";
    private static final String COLUMN_AS_TOTAL_ROWS = "total_rows";
    protected final String tableName;

    public BaseTable(String tableName) {
        this.tableName = tableName;
    }

    protected static String[] getGroupDecatenated(String data) {
        if (data != null) {
            return data.split(",");
        }
        return null;
    }

    public T get(final String column, final String value) throws SQLException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public boolean add(@Nullable final String victimId, final JSONObject jsonObject) {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public String addv3(T newInstance) throws SQLException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public void add(T newInstance) throws SQLException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public List<T> getAll() throws SQLException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    private static final Pattern DUPLICATE_ENTRY_ERROR_FORMAT = Pattern.compile("Duplicate entry '(?<value>.+)' for key '(?<column>.+)'");

    void manageError(String error) throws SQLException {

        if (error == null) {
            return;
        }

        final Matcher matcher = DUPLICATE_ENTRY_ERROR_FORMAT.matcher(error);
        if (matcher.matches()) {
            //It's a duplicate entry error
            final String column = matcher.group("column");
            final String value = matcher.group("value");
            error = String.format("Existing %s : %s", column, value);
        }

        //Some mysterious error
        throw new SQLException(error);
    }

    public void update(String whereColumn, String whereColumnValue, String updateColumn, String newUpdateColumnValue) throws SQLException {
        String error = null;
        final String query = String.format("UPDATE %s SET %s = ? WHERE %s = ?;", tableName, updateColumn, whereColumn);

        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newUpdateColumnValue);
            ps.setString(2, whereColumnValue);

            error = ps.executeUpdate() > 0 ? null : "Failed to update " + updateColumn;
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

    public void update(String whereColumn, String whereColumnValue, String whereColumn2, String whereColumnValue2, String updateColumn, String newUpdateColumnValue) throws SQLException {
        String error = null;
        final String query = String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?;", tableName, updateColumn, whereColumn, whereColumn2);
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newUpdateColumnValue);
            ps.setString(2, whereColumnValue);
            ps.setString(3, whereColumnValue2);

            error = ps.executeUpdate() > 0 ? null : "Failed to update " + updateColumn;
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

        manageError(error);
    }

    public T get(final String column1, final String value1, final String column2, final String value2) {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public void addv2(@Nullable final String victimId, final JSONArray jsonArray) throws RuntimeException, JSONException, SQLException {
        throw new RuntimeException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    protected List<T> parse(final String victimId, @NotNull JSONArray jsonArray) throws JSONException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public boolean update(T t) throws SQLException {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public boolean isExist(final T t) {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    protected boolean isExist(final String whereColumn, final String whereColumnValue) {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public String get(String byColumn, String byValue, String columnToReturn, final boolean isActive) {

        final String query = String.format("SELECT %s FROM %s WHERE %s = ? %s ORDER BY id DESC LIMIT 1", columnToReturn, tableName, byColumn, isActive ? " AND is_active = 1 " : "");

        String resultValue = null;
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, byValue);
            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                resultValue = rs.getString(columnToReturn);
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

        return resultValue;
    }

    public boolean isExist(String whereColumn1, String whereColumnValue1, String whereColumn2, String whereColumnValue2) {
        boolean isExist = false;
        final String query = String.format("SELECT id FROM %s WHERE %s = ? AND %s = ? AND is_active = 1 LIMIT 1", tableName, whereColumn1, whereColumn2);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, whereColumnValue1);
            ps.setString(2, whereColumnValue2);
            final ResultSet rs = ps.executeQuery();
            isExist = rs.first();
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
        return isExist;
    }

    public List<T> getAll(final String whereColumn, final String whereColumnValue) {
        throw new IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD);
    }

    public int getTotal(final String victimId) {

        int totalCount = 0;
        final String query = String.format("SELECT COUNT(id) AS total_rows FROM %s  WHERE victim_id = ?", tableName);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, victimId);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                totalCount = rs.getInt(COLUMN_AS_TOTAL_ROWS);
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

        return totalCount;
    }

    public JSONArray get(final String columnToReturn, final String whereInColumn, JSONArray whereInValue) throws JSONException {

        JSONArray jaFcmIds = null;
        final StringBuilder queryBuilder = new StringBuilder(String.format("SELECT %s FROM %s WHERE %s IN (", columnToReturn, tableName, whereInColumn));

        for (int i = 0; i < whereInValue.length(); i++) {
            queryBuilder.append("'").append(whereInValue.getString(i)).append("'");

            if (i < (whereInValue.length() - 1)) {
                queryBuilder.append(",");
            } else {
                queryBuilder.append(");");
            }
        }

        final java.sql.Connection con = Connection.getConnection();
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(queryBuilder.toString());

            if (rs.first()) {
                jaFcmIds = new JSONArray();
                do {
                    jaFcmIds.put(rs.getString(columnToReturn));
                } while (rs.next());
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return jaFcmIds;
    }

    public String get(String column1, String value1, String column2, String value2, String columnToReturn) {
        final String query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ? AND is_active = 1 LIMIT 1", columnToReturn, tableName, column1, column2);

        String resultValue = null;
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value1);
            ps.setString(2, value2);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                resultValue = rs.getString(columnToReturn);
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

        return resultValue;
    }

    public List<String> getLike(String column1, String value1, String column2, String value2Like, String columnToReturn) {
        final String query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s LIKE '%%%s%%' AND is_active = 1", columnToReturn, tableName, column1, column2, value2Like);

        List<String> resultValues = null;
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value1);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                resultValues = new ArrayList<>();
                do {
                    resultValues.add(rs.getString(columnToReturn));
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

        return resultValues;
    }

    public void delete(final String column1, final String value1, final String column2, final String value2) throws RequestException {
        boolean isDeleted = false;
        final String query = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", tableName, column1, column2);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value1);
            ps.setString(2, value2);
            isDeleted = ps.executeUpdate() > 0;
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

        if (!isDeleted) {
            throw new RequestException("Failed to delete");
        }
    }


}

