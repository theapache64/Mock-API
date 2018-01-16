package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Preference;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.SQLException;

/**
 * Created by theapache64 on 15/1/18.
 */
public class Preferences extends BaseTable<Preference> {

    private static final Preferences instance = new Preferences();
    private static final String COLUMN_KEY = "_key";
    private static final String COLUMN_VALUE = "_value";
    private static final String COLUMN_DEFAULT_SUCCESS_RESPONSE = "default_success_response";
    private static final String COLUMN_DEFAULT_ERROR_RESPONSE = "default_error_response";
    private static final String COLUMN_BASE_RESPONSE_STRUCTURE = "base_response_structure";

    private Preferences() {
        super("preferences");
    }

    public static Preferences getInstance() {
        return instance;
    }

    public Preference get() throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder.Builder<Preference>(getTableName(), rs -> new Preference(
                rs.getString(COLUMN_DEFAULT_SUCCESS_RESPONSE),
                rs.getString(COLUMN_DEFAULT_ERROR_RESPONSE),
                rs.getString(COLUMN_BASE_RESPONSE_STRUCTURE)
        )).select(new String[]{COLUMN_DEFAULT_SUCCESS_RESPONSE, COLUMN_DEFAULT_ERROR_RESPONSE, COLUMN_BASE_RESPONSE_STRUCTURE})
                .limit("1")
                .build()
                .get();
    }
}
