package com.theah64.mock_api.database;

import com.theah64.mock_api.models.RouteUpdate;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 10/1/18.
 */
public class RouteUpdates extends BaseTable<RouteUpdate> {

    private static final RouteUpdates instance = new RouteUpdates();

    public static final String COLUMN_KEY = "_key";
    public static final String COLUMN_ROUTE_ID = "route_id";
    private static final String COLUMN_METHOD = "method";
    private static final String COLUMN_PARAMS = "params";
    private static final String COLUMN_DELAY = "delay";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DEFAULT_RESPONSE = "default_response";
    private static final String COLUMN_CREATED_AT = "created_at";

    private RouteUpdates() {
        super("route_updates");
    }

    public static RouteUpdates getInstance() {
        return instance;
    }

    //private final String id, key, routeId, method, params, delay, description, defaultResponse;

    @Override
    public boolean add(RouteUpdate ru) throws SQLException, QueryBuilderException {
        return new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_KEY, ru.getKey())
                .add(COLUMN_ROUTE_ID, ru.getRouteId())
                .add(COLUMN_METHOD, ru.getMethod())
                .add(COLUMN_PARAMS, emptyIfNull(ru.getParams()))
                .add(COLUMN_DELAY, emptyIfNull(ru.getDelay()))
                .add(COLUMN_DESCRIPTION, emptyIfNull(ru.getDescription()))
                .add(COLUMN_DEFAULT_RESPONSE, emptyIfNull(ru.getDefaultResponse()))
                .done();
    }

    private String emptyIfNull(String data) {
        return data == null ? "" : data;
    }

    @Override
    public RouteUpdate get(String column, String value) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder.Builder<RouteUpdate>(getTableName(), new SelectQueryBuilder.Callback<RouteUpdate>() {
            @Override
            public RouteUpdate getNode(ResultSet rs) throws SQLException {
                return new RouteUpdate(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_KEY),
                        rs.getString(COLUMN_ROUTE_ID),
                        rs.getString(COLUMN_METHOD),
                        rs.getString(COLUMN_PARAMS),
                        rs.getString(COLUMN_DELAY),
                        rs.getString(COLUMN_DESCRIPTION),
                        rs.getString(COLUMN_DEFAULT_RESPONSE),
                        rs.getString(COLUMN_CREATED_AT));
            }
        }).select(new String[]{COLUMN_ID, COLUMN_KEY, COLUMN_ROUTE_ID, COLUMN_METHOD, COLUMN_PARAMS, COLUMN_DELAY, COLUMN_DESCRIPTION, COLUMN_DEFAULT_RESPONSE, COLUMN_CREATED_AT})
                .where(column, value)
                .limit(1)
                .build()
                .get();
    }

    public RouteUpdate getSecondLast(String limitUpdateId, String column, String value) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder.Builder<RouteUpdate>(getTableName(), new SelectQueryBuilder.Callback<RouteUpdate>() {
            @Override
            public RouteUpdate getNode(ResultSet rs) throws SQLException {
                return new RouteUpdate(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_KEY),
                        rs.getString(COLUMN_ROUTE_ID),
                        rs.getString(COLUMN_METHOD),
                        rs.getString(COLUMN_PARAMS),
                        rs.getString(COLUMN_DELAY),
                        rs.getString(COLUMN_DESCRIPTION),
                        rs.getString(COLUMN_DEFAULT_RESPONSE),
                        rs.getString(COLUMN_CREATED_AT));
            }
        }).select(new String[]{COLUMN_ID, COLUMN_KEY, COLUMN_ROUTE_ID, COLUMN_METHOD, COLUMN_PARAMS, COLUMN_DELAY, COLUMN_DESCRIPTION, COLUMN_DEFAULT_RESPONSE, COLUMN_CREATED_AT})
                .where(column, value)
                .where("id<", limitUpdateId)
                .orderBy("id DESC")
                .limit("1,1")
                .build()
                .get();
    }
}
