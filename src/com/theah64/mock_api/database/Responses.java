package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Response;

import java.sql.SQLException;

/**
 * Created by theapache64 on 1/12/17.
 */
public class Responses extends BaseTable<Response> {

    private static final Responses instance = new Responses();
    public static final String COLUMN_RESPONSE = "response";

    public Responses() {
        super("responses");
    }

    public static Responses getInstance() {
        return instance;
    }

    @Override
    public void add(Response newInstance) throws SQLException {
        super.add(newInstance);
    }
}
