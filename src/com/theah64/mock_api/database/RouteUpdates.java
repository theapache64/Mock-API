package com.theah64.mock_api.database;

import com.theah64.mock_api.models.RouteUpdate;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;

import java.sql.SQLException;

/**
 * Created by theapache64 on 10/1/18.
 */
public class RouteUpdates extends BaseTable<RouteUpdate> {

    private static final RouteUpdates instance = new RouteUpdates();

    private static final String COLUMN_KEY = "_key";
    private static final String COLUMN_ROUTE_ID = "route_id";
    private static final String COLUMN_METHOD = "method";
    private static final String COLUMN_PARAMS = "params";
    private static final String COLUMN_DELAY = "delay";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DEFAULT_RESPONSE = "default_response";

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
                .add(COLUMN_PARAMS, ru.getParams())
                .add(COLUMN_DELAY, ru.getDelay())
                .add(COLUMN_DESCRIPTION, ru.getDescription())
                .add(COLUMN_DEFAULT_RESPONSE, ru.getDefaultResponse())
                .done();
    }
}
