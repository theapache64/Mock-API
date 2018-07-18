package com.theah64.mock_api.database;


import com.theah64.mock_api.models.Response;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by theapache64 on 1/12/17.
 */
public class Responses extends com.theah64.webengine.database.BaseTable<Response> {

    public static final String COLUMN_RESPONSE = "response";
    public static final String COLUMN_ROUTE_ID = "route_id";
    private static final Responses instance = new Responses();

    public Responses() {
        super("responses");
    }

    public static Responses getInstance() {
        return instance;
    }


    @Override
    public List<Response> getAll(String whereColumn, String whereColumnValue) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder.Builder<>(getTableName(), rs -> new Response(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_NAME),
                rs.getString(COLUMN_ROUTE_ID),
                rs.getString(COLUMN_RESPONSE)
        ))
                .where(whereColumn, whereColumnValue)
                .select(new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ROUTE_ID, COLUMN_RESPONSE})
                .orderBy(COLUMN_ID + " DESC")
                .build()
                .getAll();
    }

    @Override
    public String addv3(Response response) throws QueryBuilderException, SQLException {
        return String.valueOf(new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_NAME, response.getName())
                .add(COLUMN_ROUTE_ID, response.getRouteId())
                .add(COLUMN_RESPONSE, response.getResponse())
                .doneAndReturn());
    }
}
